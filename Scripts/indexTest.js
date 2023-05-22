const fs = require('fs')

let pageSize = 25
let reporterId = 'a03704eb-ffe4-480a-8715-a238a162a571';
let hashtag = 'kabul'
let regex = "(?=.*\\bM.*\\b)"
let postId = '5bb4ebbb-b715-4254-ad84-eb1a5f7b4e20'
let postOffsetNext={ timestamp:ISODate("2023-01-14T13:21:47.000Z"), id:'5bb4ebbb-b715-4254-ad84-eb1a5f7b4e20'}
let postHashtagsOffset = {timestamp: ISODate("2023-01-17T22:08:32.000Z"), id:'cb8e2d9d-2294-429f-b359-89c46f6fba2e'};
let commentPostId = 'ed01e84a-760c-4346-acee-cbc62702ad7c'
let commentOffset = {timestamp:ISODate("2017-07-06T02:21:34.000Z"),id:'3bc309e9-b2b7-4f2d-93a9-3659fdb0d511'}


/** INDEXES **/

function buildUsersIndexes(){
    db.users.dropIndexes()

    db.users.createIndex(
        {'fullName':  1, '_id': 1},
        {name:'sortByFullNamePagination', unique: false, sparse: false}
    )

    db.users.createIndex(
        {'email':  1},
        {name:'emailUnique', unique: true, sparse: false}
    )
}

function buildReportersIndexes(){
    db.reporters.dropIndexes()

    db.reporters.createIndex(
        {'fullName':  1, 'reporterId': 1},
        {name:'searchByFullNamePagination', unique: false, sparse: false}
    )

    db.reporters.createIndex(
        {'reporterId':  1},
        {name:'filterByReporterId', unique: false, sparse: false}
    )

    db.reporters.createIndex(
        {'reporterId': 1, 'posts._id':  1},
        {name:'filterByReporterIdPostId', unique: false, sparse: true}
    )

    db.reporters.createIndex(
        {'reporterId':1, 'posts.timestamp':  1, 'posts._id': 1},
        {name:'filterPostsByReporterIdPagination', unique: false, sparse: true}
    )

    db.reporters.createIndex(
        {'posts.hashtags':  1, 'posts.timestamp':1, 'posts._id':1},
        {name:'filterByPostHashtag', unique: false, sparse: true}
    )

    db.reporters.createIndex(
        {'email':  1},
        {name:'emailActiveReporterUnique', unique: true, sparse: true}
    )
}

function buildCommentsIndexes(){
    db.comments.dropIndexes()

    db.comments.createIndex(
        {'post._id': 1, 'timestamp':1, '_id':1},
        {name:'searchByPostSortByTimestampPagination', unique: false, sparse: false}
    )
}

/*** QUERIES ***/

const allReaders = () => db.users.find({$and:[
            {isAdmin: {$exists:false}},
            {$or:[
                    {$and:[{fullName: 'Aadi Padmanabha'},
                            {'_id':{$gt: 'b5d21392-2396-4597-98f8-5ce3025bd581'}}]},
                    {fullName: {$gt:'Aadi Padmanabha'}}
                ]}
        ]}).sort({fullName: 1}).limit(pageSize).explain('executionStats')

const reporterByReporterId = () => db.reporters.aggregate([
        {$match: {reporterId: reporterId}},
        {$sort:{'email':-1}},
        {$group:{_id:'$reporterId',
                posts:{$push: '$posts'},
                fullName: {$first:'$fullName'},
                gender: {$first:'$gender'},
                dateOfBirth: {$first:'$dateOfBirth'},
                location: {$first:'$location'},
                cell: {$first:'$cell'},
                picture: {$first:'$picture'}}},
        {$project: {reporterId:1, "email":1, "fullName":1, "gender":1, "dateOfBirth":1, "location":1, "cell":1, "picture":1,
                posts:{$filter: {
                        input:{
                            $sortArray:{
                                input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                                sortBy:{'timestamp':-1}}},
                        cond:{}, limit:pageSize}}}}]).explain('executionStats')

const reportersByFullName = () => db.reporters.find({$and:[
            {fullName: {$regex: regex , $options:'i'}},
            {$or:[
                    {$and:[
                            {fullName: 'Mario Rossi'},
                            {reporterId:{$gt: '9f6a77b2-df70-4de3-a2a4-a6e3136de134'}}
                        ]},
                    {fullName: {$gt:'Mario Rossi'}}
                ]}
        ]}, {fullName:1}).sort({fullName:1}).limit(pageSize).explain('executionStats')

const postByPostId = () => db.reporters.find({$and:[{reporterId: reporterId, 'posts._id':postId}]},
    {posts:{$elemMatch:{'_id':postId}}}).explain('executionStats')

const postsByReporterId = () => db.reporters.aggregate([
    {$match:
            {$and:[
                    {reporterId: reporterId},
                    {'posts.timestamp': {$lt: postOffsetNext.timestamp}}]}
    },
    {$project:{
            reporterId:1,
            posts: {"$filter":
                    {"input": "$posts", "as": "posts", "cond":
                            {"$or": [
                                    {"$and": [{"$eq": ["$$posts.timestamp", postOffsetNext.timestamp]},
                                            {"$lt": ["$$posts._id", postOffsetNext.id]}]},
                                    {"$lt": ["$$posts.timestamp", postOffsetNext.timestamp]}]}}}}},
    {$group:{_id:'$reporterId', posts:{$push: '$posts'}}},
    {$project: {reporterId:1,
            posts:{$filter: {
                    input:{
                        $sortArray:{
                            input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                            sortBy:{'timestamp':-1}}},
                    cond:{}, limit:pageSize}}}}
    ]).explain('executionStats')

const postByHashtag = () => db.reporters.aggregate([
    {$match:{'posts.hashtags':hashtag}},
    {$project:{
            'reporterId': 1,
            'posts': {
                $filter: {input: '$posts', as: 'posts', cond: {
                        $and: [
                            {$in: [hashtag, {$ifNull: ['$$posts.hashtags', []]}]},
                            {$or: [
                                    {$and: [{
                                            $eq:['$$posts.timestamp', postHashtagsOffset.timestamp]},
                                            {$lt:['$$posts._id', postHashtagsOffset.id]}]},
                                    {$lt: ['$$posts.timestamp', postHashtagsOffset.timestamp]}]}]}}}}},
    {$addFields: {'posts.reporterId': '$reporterId'}},
    {$group:{_id: '', posts: {$push: '$posts'}}},
    {$project: {_id:0, posts:{
                $filter: {
                    input:{
                        $sortArray:{
                            input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                            sortBy:{'timestamp':-1}}},
                    cond:{}, limit:pageSize}}}}]).explain('executionStats')

const commentsByPostId = () => db.comments.find({$and:[
            {'post._id': commentPostId},
            {$or:[
                    {$and:[{timestamp: commentOffset.timestamp }, {'_id':{$gt: commentOffset.id}}]},
                    {timestamp: {$gt: commentOffset.timestamp}}
                ]}
        ]}).sort({timestamp: 1, _id: 1}).limit(pageSize).explain('executionStats')


const QUERIES = [
    {name:'allReaders', collection:'users', query: allReaders},
    {name:'reporterByReporterId', collection:'reporters', query: reporterByReporterId},
    {name:'reportersByFullName', collection:'reporters', query: reportersByFullName},
    {name:'postByPostId', collection: 'reporters', query:postByPostId},
    {name:'postByReporterId', collection:'reporters', query: postsByReporterId},
    {name:'postsByHashtag', collection:'reporters', query: postByHashtag},
    {name:'commentsByPostId', collection:'comments', query: commentsByPostId}
]

// Test function

function indexesTest(){
    let stats = {
        users: {},
        reporters: {},
        comments: {}
    }

    let execStats;
    for (let query of QUERIES) {
        execStats = query['query']()
        if (execStats['ok'] !== 1) {
            console.error(`Error on: ${query['name']}`)
        } else {
            stats[query['collection']][query['name']] =
                execStats['executionStats' in execStats ? 'executionStats' : 'stages']
        }
    }

    fs.writeFileSync('indexes.json', JSON.stringify(stats, null, 1))
    console.log("Indexes test ready")
}



// Build indexes
buildUsersIndexes()
buildReportersIndexes()
buildCommentsIndexes()

// Run tests

indexesTest()
