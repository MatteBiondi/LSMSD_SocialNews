import '../util.js'
import { AdminDashboard } from "../charts.js";

const body = document.body,  html = document.documentElement;
const widgetHeight = `${
    Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight ) *
    (parseInt($('body').css('--content-height'))/100) * 0.40}px`

$(document).ready( () => {

    AdminDashboard.init(widgetHeight);

    $('.reload').on('click', reload);
    $('.settings').on('hide.bs.dropdown', configure)
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

    AdminDashboard.reload(ev.target.dataset['statistic']).then(() => { stop = true });
}

function configure(){
    let params = $(this).find('.param');
    let reconfigured = false

    for(let param of params){
        let key = param['dataset']['param'];
        let value = param.value;
        reconfigured |= AdminDashboard.configure($(this).siblings('.reload')[0].dataset['statistic'], key, value)
    }

    if(reconfigured)
        $(this).siblings('.reload').click()
}