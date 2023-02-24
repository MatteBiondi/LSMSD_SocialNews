const fs = require('fs')

// WriteConcern test configuration

const REPEAT = 1000;
const N_DOCS = 1;
const DOC_SIZES = [16, 512, 1024, 4096]
const CONFIG = [
    {name: 'W1J0', writeConcern: {w:1, j:false, wtimeout: 0}},
    {name: 'W2J0', writeConcern: {w:2, j:false, wtimeout: 0}},
    {name: 'W3J0', writeConcern: {w:3, j:false, wtimeout: 0}},
    {name: 'WMJ0', writeConcern: {w:'majority', j:false, wtimeout: 0}},
    {name: 'W1J1', writeConcern: {w:1, j:true, wtimeout: 0}},
    {name: 'W2J1', writeConcern: {w:2, j:true, wtimeout: 0}},
    {name: 'W3J1', writeConcern: {w:3, j:true, wtimeout: 0}},
    {name: 'WMJ1', writeConcern: {w:'majority', j:true, wtimeout: 0}},
]


// Index test configuration
const reportersByFullName = (indexes=true) =>
    db.reporters.find({$and:[
            {fullName: {$regex: "(?=.*\\bR.*\\b)" , $options:'i'}},
            {$or:[
                    {$and:[
                            {fullName: 'Mario Rossi'},
                            {reporterId:{$gt: 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44'}}
                        ]},
                    {fullName: {$gt:'Mario Rossi'}}
                ]}
        ]}, {fullName:1}).sort({fullName:1}).limit(25).hint(indexes ? {}:{$natural: 1}).explain('executionStats')

const authenticateReporter = (indexes=true) =>
    db.reporters.find({$and:[{email:'m.rossi@example.it'},
            {password:'4813494d137e1631bba301d5acab6e7bb7aa74ce1185d456565ef51d737677b2'}]}).hint(indexes ? {}:{$natural: 1}).explain('executionStats')

const reportersByReporterId = () =>
    db.reporters.aggregate([
        {$match:{reporterId: 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44'}},
        {$unwind: '$posts'},
        {$sort: {'posts.timestamp':-1, 'posts._id':-1}},
        {$limit: 25},
        {$group: {_id: '$reporterId', posts: {$push: '$posts'}}}
    ]).explain('executionStats')


const postByHashtag = () =>
    db.reporters.aggregate([
        {$match:{'posts.hashtags':'BREAKING'}},
        {$project:{reporterId:1, posts:{$filter:{input:'$posts', as:'posts', cond:{$and:[
                                {$in:['BREAKING', {$ifNull:['$$posts.hashtags',[]]}]},
                                {$gte:['$$posts.timestamp',ISODate('2017-01-01')]}
                            ]}}}}},
        {$unwind:'$posts'},
        {$sort:{'posts.timestamp':-1,'posts._id':-1}},
        {$limit:10},
        {$group:{reporterId: {$first: '$reporterId'}, _id: '$posts._id', posts: { $push: '$posts'}}},
        {$sort:{'posts.timestamp':-1,'posts._id':-1}},
    ]).explain('executionStats')


const allReaders = (indexes=true) =>
    db.users.find({$and:[
            {isAdmin: {$exists:false}},
            {$or:[
                    {$and:[{fullName: 'Lucas French'}, {'_id':{$gt: '84ee74bc-f0b1-4ded-bce5-42eb6ed5911e'}}]},
                    {fullName: {$gt:'Lucas French'}}
                ]}
        ]}, {password:-1}).sort({fullName: 1}).limit(25).hint(indexes ? {}:{$natural: 1}).explain('executionStats')


const commentsByPostId = (indexes=true) =>
    db.comments.find({$and:[
            {'post._id': '327ba9a1-f476-4469-8127-acda3c4096f2'},
            {$or:[
                    {$and:[{timestamp: new Date('2017-07-14T14:47:11.000+00:00')}, {'_id':{$gt: '58397250-cd1d-4f24-8356-64cdfa3bb56f'}}]},
                    {timestamp: {$gt: new Date('2017-07-14T14:47:11.000+00:00')}}
                ]}
        ]}).sort({timestamp: 1, _id: 1}).limit(25).hint(indexes ? {}:{$natural: 1}).explain('executionStats')

const commentsByPostIdPrev = () =>
    db.comments.aggregate([{$match:{$and:[
                {'post._id': '327ba9a1-f476-4469-8127-acda3c4096f2'},
                {$or:[
                        {$and:[{timestamp: new Date('2017-07-14T14:47:11.000+00:00')}, {'_id':{$gt: '58397250-cd1d-4f24-8356-64cdfa3bb56f'}}]},
                        {timestamp: {$gt: new Date('2017-07-14T14:47:11.000+00:00')}}
                    ]}
            ]}}, {$sort:{timestamp:-1, _id:-1}}, {$limit: 25}, {$sort:{timestamp:1, _id:1}}]).explain()

const QUERIES = [
    {name:'authentication', collection:'reporters', query: authenticateReporter,
        indexes: [[{'post._id': 1}, {timestamp: 1, _id:1}], {'post._id': 1, timestamp: 1, _id:1}]},
    {name:'reportersByFullName', collection:'reporters', query: reportersByFullName,
        indexes:[{fullName:1},{fullName: 1, reporterId: 1}]},
    {name:'reportersByReporterId', collection:'reporters', query: reportersByReporterId,
        indexes:[]},
    {name:'allReaders', collection:'users', query: allReaders,
        indexes: [{fullName: 1}, {fullName: 1, _id:1}]},
    {name:'commentsByPostId', collection:'comments', query: commentsByPostId,
        indexes: [[{'post._id': 1}, {timestamp: 1, _id:1}], {'post._id': 1, timestamp: 1, _id:1}]},
    {name:'commentsByPostIdPrev', collection:'comments', query: commentsByPostIdPrev,
        indexes: [[{'post._id': 1}, {timestamp: 1, _id:1}], {'post._id': 1, timestamp: 1, _id:1}]},
    {name:'postsByHashtag', collection:'reporters', query: postByHashtag,
        indexes: []}
]


// Test functions

function randomData(length) {
    let data = '';
    while (data.length < length) {
        data += String.fromCharCode(Math.floor(Math.random() * 128));
    }
    return data;
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

function writeConcernTest(){
    console.log("Write concern test running ...")
    let res = {}
    for (let conf of CONFIG ){
        res[conf['name']] = {}
        for(let docSize of DOC_SIZES){
            let series = []
            for (let i = 0; i < REPEAT; ++i){
                series.push(writeConcern(conf['writeConcern'], docSize))
                res[conf['name']][`${docSize}B`] = series
            }
        }
        console.log(`${conf['name']}: ready`)
    }

    fs.writeFileSync('writeConcernTest.json', JSON.stringify(res, null, 1))
    console.log("Write concern test ready")
}

function indexesTest(){
    stats = {
        users: {},
        reporters: {},
        comments: {}
    }

    for (query of QUERIES){
        execStats = query['query']()
        if(execStats['ok'] !== 1){
            console.error(`Error on: ${query['name']}`)
        }
        else{
            stats[query['collection']][query['name']] =
                execStats['executionStats' in execStats ? 'executionStats':'stages']
        }
    }

    fs.writeFileSync('indexes.json', JSON.stringify(stats, null, 1))
    console.log("Indexes test ready")
}


// Run tests

writeConcernTest()
indexesTest()
