// noinspection CssUnresolvedCustomProperty

import {showMessage, loadScript} from "./util.js";

const script = loadScript("https://cdn.zingchart.com/zingchart.min.js", null);

class Chart{
    static widgetHeight = '20%'
    constructor(id, type, params) {
        this.id = id
        this.config = {
            type: type,
            legend: {
                'toggle-action':'remove',
                draggable: true,
                maxItems: 4,
                overflow: 'scroll',
                scroll:{
                    handle: {
                        backgroundColor: 'gray',
                        borderRadius: '15px'
                    }
                },
            },
            tooltip: { text: `%t: %vt` },
            plot: {
                animation:{
                   'on-legend-toggle': true,
                   effect: 2,
                   method: 0,
                   sequence: 1,
                   speed: 1000
                },
                valueBox: {
                    rules: [
                        { rule: '%npv >= 10', placement: 'in'},
                        { rule: '%npv < 10', placement: 'out'}],
                    connector: {
                        lineStyle: 'dashed',
                        lineSegmentSize: '3px'
                    }},
            }
        }
        this.params = params
    }

    configure(key, value){
        if(key in this.params && this.params[key] === value)
            return false;
        this.params[key] = value;
        return true;
    }

    render() {
        zingchart.render({
            id: this.id,
            data: this.config,
            height: Chart.widgetHeight,
            output: 'svg',
            defaults: { graph: {backgroundColor:'var(--bs-dark)'} }
        })
        zingchart.bind(this.id, 'contextmenu', () => false);
    }
}

class Table{
    static widgetHeight = '20%';

    constructor(id, params){
        this.id = id
        this.params = params
    }

    configure(key, value){
        if(key in this.params && this.params[key] === value)
            return false;
        this.params[key] = value;
        return true;
    }

    render(data, columns){
        let rank = 1;
        let rows = [];

        for(let record of data){
            let row = [];
            for(let key in record){
                row.push(`<td>${record[key]}</td>`)
            }
            rows.push(`<tr style="display: none" class="fade-in-row"><td>${rank++}</td>${row.join('')}</tr>`)
        }

        let headers = [];
        for(let column of ['Rank'].concat(columns)){
            headers.push(`<th scope="col">${column}</th>`)
        }

        $(`#${this.id}`).html(`
            <div class="table-responsive scrollbar-black" style="height: ${Table.widgetHeight}; background: var(--bs-dark)">
            <table class="table table-dark table-hover text-center">
                <thead>
                    <tr>
                        ${headers.join('')}
                    </tr>
                </thead>
                <tbody>
                    ${rows.join('')}              
                </tbody>
            </table></div>`
        )
        $(`#${this.id} .fade-in-row`).each((index, row) => {$(row).delay(index*250).fadeIn(500)})
    }
}

class Dashboard{
    #widgets;
    #data;
    #baseURL
    constructor(widgets, url) {
        this.#widgets = widgets;
        this.#baseURL = url;
        this.#data = {}
    }

    async init(widgetHeight) {
        Chart.widgetHeight = widgetHeight;
        Table.widgetHeight = widgetHeight;

        try {
            await script;
        }
        catch (error){
            showMessage('danger', 'Failed to load resources');
            return;
        }

        $.ajax(this.#baseURL, {
            type: 'post',
            data: JSON.stringify({data: true}),
            contentType: 'json',
            dataType: 'json',
            success: (data) => {
                $('.spinner').remove();
                this.#data = data;
                for (let key in this.#widgets)
                    this.#widgets[key].render(data[key]);
            },
            error: (error) => showMessage('danger', `Something went wrong: ${error['responseJSON']['message']}`)
        })
    }

    configure(widget, key, value){
        if (widget in this.#widgets) {
            return this.#widgets[widget].configure(key, value);
        }
        return false;
    }

    async reload(widget) {
        try{
            if (!widget in this.#widgets)
                return;

            // Reload data
            let data = await $.ajax(this.#baseURL, {
                type: 'post',
                data: JSON.stringify(this.#widgets[widget]['params']),
                contentType: 'json',
                dataType:'json'
            });

            // Set data
            this.#data[widget] = data[widget];

            // Re-render widget
            this.#widgets[widget].render(this.#data[widget]);
        }
        catch (error){
            if('responseJSON' in error)
                showMessage('danger', `Something went wrong: ${error['responseJSON']['message']}`);
            else
                showMessage('danger', `Something went wrong`);
        }
    }
}

const mostPopularReporters = new class extends Table{
    constructor(){
        super('most-popular-reporters-grid', {});
    }

    render(data) {
        super.render(data, ['Full Name', 'Num. followers'])
    }
}

const mostActiveReaders = new class extends Table{
    constructor(){
        super('most-active-readers-grid',{statistic: 'mostActiveReaders', lastN: '1', unitOfTime:'Month'});
    }

    render(data) {
        super.render(data, ['Full Name', 'Num. of comments'])
    }
}

const genderStatistic = new class extends Chart{
    constructor() {
        super('gender-statistic-pie', 'pie', {statistic: 'genderStatistic'})
    }

    render(data){
        this.config['series'] = [
            {
                text: "Female",
                values: [data['Female']],
                'background-color': 'red'
            },
            {
                text:"Male",
                values: [data['Male']],
                'background-color': 'blue'
            },
            {
                text: "Other",
                values: [data['Other']],
                'background-color': 'green'
            }
        ];
        super.render();
    }
}

const nationalityStatistic = new class extends Chart{
    constructor() {
        super('nationality-statistic-pie', 'navpie', {statistic: 'nationalityStatistic'})
        this.config['options'] =  { threshold: '5%' }
    }

    render(data){
        this.config['series'] = []
        let sum = 0
        for (let record of data) {
            this.config['series'].push({text:record['country'],values:[record['count']]})
            sum += record['count']
        }
        this.config['plot']['animation']['speed'] = 1000 / (data.filter(v => v['count']/sum > 0.05).length + 1)
        super.render();
    }
}

const hottestMoments = new class extends Chart{
    constructor(){
        super('hottest-moment', 'bar', {statistic: 'hottestMomentsOfDay', windowSize:'3',
            lastN: '1', unitOfTime:'Month'});
    }

    render(data){
        this.config['scaleX'] = {
            label: { text: "Temporal window", color: 'white'},
            labels: []
        }
        this.config['scaleY'] = {
            label: {text: "Number of comments", color: 'white'}
        }
        this.config['legend'] = null;
        this.config['series'] = [{text: "Window",values:[]}]
        for(let window of data){
            this.config['series'][0]['values'].push(window['count'])
            this.config['scaleX']['labels'].push(`${window['lowerBound']}-${window['upperBound']}`)
        }

        super.render();
    }
}

const hottestPosts = new class extends Table{
    constructor(){
        super('hottest-posts-grid',{statistic: 'hottestPosts', lastN: '1', unitOfTime:'Month'});
    }

    render(data) {
        let tmp = [];
        for(let post of data) {

            let newElem = [];
            newElem["id"] = post.id;
            newElem["text"] = post.text ?? "empty";
            newElem["hashtags"] = post.hashtags ?? "none";
            let milliseconds = parseInt(post.timestamp);
            newElem["timestamp"] = getFormattedTimestamp(milliseconds);
            tmp.push(newElem);
        }

        data = tmp;
        super.render(data, ['ID','Text', 'Hashtags', 'Publication date'])
    }
}

function getFormattedTimestamp(milliseconds) {
    let date = moment(milliseconds);
    return date.format("ddd MMM DD HH:mm:ss [CET] YYYY");
}

export const AdminDashboard = new class extends Dashboard {
    constructor() {
        super({
            'genderStatistic': genderStatistic,
            'nationalityStatistic': nationalityStatistic,
            'mostActiveReaders': mostActiveReaders,
            'mostPopularReporters': mostPopularReporters
        }, document.URL)
    }
}

export const ReporterDashboard = new class extends Dashboard {
    constructor() {
        super({
            'hottestPosts': hottestPosts,
            'hottestMomentsOfDay':hottestMoments
        }, document.URL);
    }
}