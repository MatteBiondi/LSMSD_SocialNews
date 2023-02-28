import {loadScript, computeWindowHeight, showMessage} from "../util.js";
const script = loadScript("https://cdn.zinggrid.com/zinggrid.min.js");

// Report page
const reportPage = new class {
    #numOfLoaded = 0;
    #currentLoaded = 0;
    #page = 0;
    #pageLength = 0
    #reporterId = '';
    #reports = [];

    #clear(){
        this.#numOfLoaded = 0;
        this.#currentLoaded = 0;
        this.#page = 0;
        this.#pageLength = 0
        this.#reporterId = '';
        this.#reports = [];
    }

    async #nextPage() {
        this.#page += 1;
        this.#render(await this.#load());
    }

    async #prevPage(){
        this.#page -= 1;
        this.#numOfLoaded -= this.#currentLoaded + this.#pageLength;
        this.#render(await this.#load(true));
    }

    async #load(prev){
        this.#reports =  await $.ajax({
            type: 'get',
            url: `${location.href.split('admin')[0]}/admin/report`,
            data: { reporterId: this.#reporterId, offset: prev ? this.#page * this.#pageLength : this.#numOfLoaded },
            dataType: 'json'
        });
        this.#currentLoaded = this.#reports.length;
        this.#numOfLoaded += this.#reports.length;
    }

    async #deleteReport(index){
        try{
             await $.ajax({
                type: 'delete',
                url: `${location.href.split('admin')[0]}/admin/report?reportId=${this.#reports[index]['reportId']}`,
             });
             this.reportList.find('li')[index].remove();
             showMessage('success', 'Report successfully deleted')
        }
        catch (error){
            if('responseJSON' in error)
                showMessage('danger', `Something went wrong: ${error['responseJSON']['message']}`);
            else
                showMessage('danger', `Something went wrong`);
        }
    }

    async #showPost(index){
        try{
            let post = await $.ajax({
                type: 'get',
                url: `${location.href.split('admin')[0]}/admin/report?type=post&reporterId=${this.#reporterId}` +
                    `&postId=${this.#reports[index]['postId']}`,
            });
            console.log(post);

            this.postContainer.html(`
                <div class="post-container container my-5 search-result" style="width: auto">
                    <header class="post-header">
                        <a href="${location.href.split('admin')[0]}/reporterPage?id=${this.#reporterId}" 
                            class="option"><i class="bi bi-person"></i></a>
                        <a data-ref="${post.postId}" class="option"><i class="bi bi-trash3"></i></a>
                    </header>
                    <hr>
                    <p class="post-text">${post.text}</p>
                    <footer>
                        <p class="hashtags">${post.hashtags}</p>
                        <p class="related-links">${post.links}</p>
                        <p class="timestamp">${new Date(post.timestamp).toLocaleString()}</p>
                        <hr>
                        <div class="show-comm-div"><button class="show-comm">Show comments</button></div>
                    </footer>
                </div>
            `)
            this.postModal.show();
        }
        catch (error){
            if('responseJSON' in error)
                showMessage('danger', `Something went wrong: ${error['responseJSON']['message']}`);
            else
                showMessage('danger', `Something went wrong`);
        }
    }

    #render(){
        this.reportList.empty();
        for(let [index, report] of this.#reports.entries()){
            this.reportList.append(`
            <li class="list-group-item list-group-item-action report-item">
                <div class="report d-flex flex-column">
                    <span class="report-text">${report['text']}</span>
                    <div class="d-flex justify-content-between">
                        <span class="report-timestamp">${new Date(report['timestamp']).toLocaleString()}</span>
                        <div class="report-controls" data-index="${index}">
                            <i class="fs-5 bi bi-chat-left-text report-view-post"></i>
                            <i class="fs-5 bi bi-trash3-fill report-delete"></i>
                        </div>
                    </div>
                </div>
            </li>
        `);
        }
        if(this.#reports.length === 0){
            this.reportList.append(
            `<li class="list-group-item list-group-item-action report-item">
                <div class="report d-flex flex-column">
                    <span class="fs-3">No reports to show !</span>
                </div>
            </li>`)
        }
        this.prev.prop('disabled', (this.#page === 0));
        this.next.prop('disabled', this.#reports.length < this.#pageLength);
        this.pageElem.text(this.#page+1);

        $('.report-delete').on('click', (ev) => this.#deleteReport(ev.currentTarget.parentElement.dataset['index']));
        $('.report-view-post').on('click', (ev) => this.#showPost(ev.currentTarget.parentElement.dataset['index']));

        return this;
    }

    init(){
        this.reportModalElem = $('#report-modal');
        this.postModalElem =  $('#post-modal');
        this.reportModalElem.on('hide.bs.modal', () => this.#clear());
        this.reportModal = new bootstrap.Modal(this.reportModalElem);
        this.postModal = new bootstrap.Modal(this.postModalElem);
        this.postContainer = $('#post')
        this.reportList = $('#report-content-list');
        this.prev = $('#report-prev');
        this.prev.on('click', () => this.#prevPage());
        this.next = $('#report-next');
        this.next.on('click', () => this.#nextPage());
        this.pageElem = $('#page');
    }

    async loadReports(reporterId){
        this.#reporterId = reporterId;
        await this.#load();
        this.#pageLength = this.#reports.length;
        this.#render();
        return this;
    }

    show(){
        this.reportModal.show();
    }
}

$(document).ready(async () => {
    try {
        reportPage.init();
        await script;
        const zgRef = $('zing-grid');
        zgRef[0].executeOnLoad(() => {
            zgRef[0].setHeight(`${
                computeWindowHeight()
                * (parseInt($('body').css('--content-height'))/100)
                - $('zg-footer').height()
                - $('zg-caption').height()
                - $('zg-row').height()}px`
            );
            $('zg-button').removeAttr('tooltip')
        })
    }
    catch (error){
        showMessage('danger', 'Failed to load resources');
    }
});

// Global render function

window.render = function render(value, elem){
    if(value !== undefined){
        let button = $(elem).find('zg-button');
        let col = button[0]['dataset']['col'];
        let icon = $(elem).find('i')
        if(col === 'home'){
            button.addClass('view-btn btn btn-primary');
            icon.addClass('view-icon bi bi-eye-fill');
            button.on("click", (ev) =>
                location.href = `${document.URL.slice(0,document.URL.search('admin')-1)}` +
                    `/reporterPage?id=${ev.currentTarget.dataset['id']}`);
        }
        else {
            button.addClass('btn btn-danger');
            button.removeAttr('tooltip');
            icon.addClass('bi bi-trash3-fill');
        }
    }
}

window.renderReport = function renderReport(id, numOfReport, elem){
    let button = $(elem).find('zg-button')
    let icon = $(elem).find('i')
    if(numOfReport !== undefined){
        icon.addClass('report-icon bi bi-exclamation-diamond-fill');
        button.addClass('report-btn btn btn-danger');
        button.on("click", async (ev) => (await reportPage.loadReports(ev.currentTarget.dataset['id'])).show());
    }
    else {
        button.hide()
    }
}