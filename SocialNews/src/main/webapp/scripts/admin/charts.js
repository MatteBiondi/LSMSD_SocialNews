// noinspection CssUnresolvedCustomProperty

import {showMessage} from "../util.js";

const baseURL = document.URL;
const body = document.body,  html = document.documentElement;
const widgetHeight = `${
    Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight ) * 
        (parseInt($('body').css('--content-height'))/100) * 0.40}px`

class Chart{
    constructor(id, type, params) {
        this.id = id
        this.height = widgetHeight
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
            height: this.height,
            output: 'svg',
            defaults: { graph: {backgroundColor:'var(--bs-dark)'} }
        })
        zingchart.bind(this.id, 'contextmenu', () => false);
    }
}

const mostActiveReaders = new class {
    constructor(){
        this.id = 'most-active-readers-grid'
        this.params = {statistic: 'mostActiveReaders', lastN: '1', unitOfTime:'Month'}
    }

    configure(key, value){
        if(key in this.params && this.params[key] === value)
            return false;
        this.params[key] = value;
        return true;
    }

    render(data){
        let rank = 1;
        let rows = [];
        for(let reader of data){
            rows.push(`<tr><td>${rank++}</td><td>${reader['fullName']}</td><td>${reader['numOfComment']}</td></tr>`)
        }

        $(`#${this.id}`).html(`
            <div class="table-responsive scrollbar-black" style="height: ${widgetHeight}; background: var(--bs-dark)">
            <table class="table table-dark table-hover text-center">
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
        super('hottest-moment-bar', 'bar', {statistic: 'hottestMomentsOfDay', windowSize:'3',
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
        $.ajax(baseURL, {
            type: 'get',
            data: {data: true},
            dataType:'json',
            success: (data) => {
                $('.spinner').remove();
                this.#data = data;
                for (let key in this.#widgets)
                    this.#widgets[key].render(data[key]);},
            error: (error) => showMessage('danger', `Something went wrong: ${error}`)
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
            let data = await $.ajax(baseURL, {
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