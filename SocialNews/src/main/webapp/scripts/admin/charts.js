import {showMessage} from "../util.js";

const baseURL = document.URL;
let body = document.body,  html = document.documentElement;
const height = Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight );

class Chart{
    constructor(id, type) {
        this.id = id
        // this.size = {x:'60%', y:'90%'}
        this.config = {
            type: type,
            legend: {
                'toggle-action':'remove',
                draggable: true,
                maxItems: 4,
                overflow: 'scroll'
            },
            tooltip: { text: `%t: %vt` },
            plot: {
                animation:{
                   'on-legend-toggle': true,
                   effect: 2,
                   method: 0,
                   sequence: 1,
                   speed: 500
                },
                valueBox: {
                    rules: [
                        { rule: '%npv >= 5', placement: 'in'},
                        { rule: '%npv < 5', placement: 'out'}],
                    connector: {
                        lineStyle: 'dashed',
                        lineSegmentSize: '3px'
                    }},
            }
        }
        this.params = {data: true}
    }

    configure(key, value){
        this.params[key] = value;
    }

    reload(){

    }

    render() {
        zingchart.render({
            id: this.id,
            data: this.config,
            height: `${height*0.9*0.45}px`,
            output: 'svg',
            defaults: { graph: {backgroundColor:'#212529'} }
            //height: this.size['x'],
            //width: this.size['y']
        })
        zingchart.bind(this.id, 'contextmenu', () => false);
    }
}

const mostActiveReaders = new class extends Chart{
    constructor(){
        super('most-active-readers-grid', 'grid');
    }

    render(data){
        this.config['series'] = []

        let rank = 1
        for(let reader of data){
            this.config['series'].push({values:[rank++, reader['fullName'], reader['count']]})
        }

        this.config['legend'] = null;
        this.config['options'] =  {
            headerRow: true,
            colLabels: ['Rank', 'Full name', 'Number of comments'],
            colWidths: ['10%','55%','35%'],
        }

        let rows = [];
        for (let i = 0; i < 10; ++i){
            this.config['series'].push({values:[i+1, 'John Doe', 150]})
            rows.push(`<tr><td>${i+1}</td><td>${'John Doe'}</td><td>${150}</td></tr>`)
        }
        $(`#${this.id}`).html(
            `
            <div class="table-responsive" style="height: ${height*0.9*0.45}px; background: #212529"><table class="table table-dark table-hover">
                <thead>
                    <tr>
                        <th scope="col">Rank</th>
                        <th scope="col">Full Name</th>
                        <th scope="col">Num. of comments</th>
                    </tr>
                </thead>
                <tbody>
                    ${rows.join('')}              
                </tbody>
            </table></div>`
        )

        //super.render();
    }
};

const genderStatistic = new class extends Chart{
    constructor() {
        super('gender-statistic-pie', 'pie')
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
        super('nationality-statistic-pie', 'navpie')
        this.config['options'] =  { threshold: '5%' }
    }
    render(data){
        this.config['series'] = []
        for (let record of data) {
            this.config['series'].push({text:record['country'],values:[record['count']]})
        }

        super.render();
    }
}

const hottestMoments = new class extends Chart{
    constructor(){
        super('hottest-moment-bar', 'bar');
    }

    render(data){
        this.config['scaleX'] = {
            label: { text: "Temporal window"},
            labels: []
        }
        this.config['scaleY'] = {
            label: {text: "Number of comments"}
        }
        this.config['legend'] = null;
        this.config['series'] = [{values:[]}]
        for(let window of data){
            this.config['series'][0]['values'].push(window['count'])
            this.config['scaleX']['labels'].push(`${window['lowerBound']}-${window['upperBound']}`)
        }

        super.render();
    }
}

export const Dashboard = new class {

    #widgets;
    #data;
    constructor() {
        this.#widgets ={
            'genderStatistic': genderStatistic,
            'nationalityStatistic': nationalityStatistic,
            'mostActiveReaders': mostActiveReaders,
            'hottestMomentsOfDay': hottestMoments,
        }
        this.#data = {}
    }

    init(){
        this.#data = $.ajax(baseURL, {
            type: 'get',
            data: {data: true},
            dataType:'json',
            success: (data) => {for (let key in this.#widgets) this.#widgets[key].render(data[key]);},
            error: (error) => showMessage('danger', `Something went wrong: ${error}`)
        })


    }

    configure(element){
        if (this.#widgets.contain(element)) {
            this.#widgets[element].configure();
        }
    }

    reload(widget) {
        if (this.#widgets.contain(widget)) {
            this.#widgets[widget].render(this.#data);
        }
    }
}