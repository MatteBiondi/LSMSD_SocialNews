import '../util.js'
import { ReporterDashboard } from "../charts.js";
import { computeWindowHeight } from "../util.js";

const widgetHeight = `${
    computeWindowHeight() * (parseInt($('body').css('--content-height'))/100) * 0.40}px`

$(document).ready( () => {
    ReporterDashboard.init(widgetHeight).then(() => {
        $('.reload').on('click', reload);
        $('.settings').on('hide.bs.dropdown', configure)
    });
});

function reload(ev){
    let target = $(this);
    let stop = false;
    let loop = setInterval(() => {
        let deg = parseInt(target.css('rotate') !== 'none' ? target.css('rotate'):0) + 20;
        target.css('rotate',`${deg}deg`);
        if(stop && (deg % 360) === 0){
            clearInterval(loop);
        }
    }, 75);

    ReporterDashboard.reload(ev.target.dataset['statistic']).then(() => { stop = true });
}

function configure(){
    let params = $(this).find('.param');
    let reconfigured = false

    for(let param of params){
        let key = param['dataset']['param'];
        let value = param.value;
        reconfigured |= ReporterDashboard.configure($(this).siblings('.reload')[0].dataset['statistic'], key, value)
    }

    if(reconfigured)
        $(this).siblings('.reload').click()
}