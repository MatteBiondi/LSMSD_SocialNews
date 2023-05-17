const fs = require('fs')

// WriteConcern test configuration

const REPEAT = 1000;
const N_DOCS = 1;
const DOC_SIZES = [16, 512, 1024, 4096]
const CONFIG = [
    {name: 'W1J0', writeConcern: {w:1, j:false, wtimeout: 0}},
    {name: 'WMJ0', writeConcern: {w:'majority', j:false, wtimeout: 0}},
    {name: 'W3J0', writeConcern: {w:3, j:false, wtimeout: 0}},
    {name: 'W1J1', writeConcern: {w:1, j:true, wtimeout: 0}},
    {name: 'WMJ1', writeConcern: {w:'majority', j:true, wtimeout: 0}},
    {name: 'W3J1', writeConcern: {w:3, j:true, wtimeout: 0}},
]

const READ_CONCERN_CONFIG = [
    {name: 'local', readConcern: 'local'},
    {name: 'majority', readConcern: 'majority'}
]

// Test functions

function randomData(length) {
    let data = '';
    while (data.length < length) {
        data += String.fromCharCode(Math.floor(Math.random() * 128));
    }
    return data;
}

function readConcern(readConcernConfig){
    let start = performance.now();
    res = db.users.find({gender:'Male'}).readConcern(readConcernConfig);
    return performance.now() - start
}

function writeConcern(writeConcernConfig, docSize) {
    let docs = []

    for (let i = 0; i < N_DOCS; ++i)
        docs.push({test:true, data: randomData(docSize)})

    db.test.deleteMany({})
    let start = performance.now();
    for (let doc of docs){
        db.test.insertOne(doc,  { writeConcern: writeConcernConfig});
    }
    return performance.now() - start
}

function readConcernTest(){
    console.log("Read concern test running ...")
    let res = {}
    for (let conf of READ_CONCERN_CONFIG ){
        res[conf['name']] = {}
        let series = []
        for (let i = 0; i < REPEAT; ++i){
            series.push(readConcern(conf['readConcern']))
        }
        res[conf['name']] = series
        console.log(`${conf['name']}: ready`)
    }
    
    fs.writeFileSync('readConcernTest.json', JSON.stringify(res, null, 1))
    console.log("Read concern test ready")
}

function writeConcernTest(){
    console.log("Write concern test running ...")
    let res = {}
    for (let conf of CONFIG ){
        res[conf['name']] = {}
        for(let docSize of DOC_SIZES){
            let series = []
            for (let i = 0; i < REPEAT; ++i){
                series.push(writeConcern(conf['writeConcern'], docSize))    
            }
            res[conf['name']][`${docSize}B`] = series
        }
        console.log(`${conf['name']}: ready`)
    }

    fs.writeFileSync('writeConcernTest.json', JSON.stringify(res, null, 1))
    console.log("Write concern test ready")
}

console.log("***********************************\n*** CLUSTER CONFIGURATION TEST ***\n***********************************")
            
// Launch test
//writeConcernTest()
readConcernTest()