const fs = require('fs')

let pageSize = 5
let reporterId = 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44';
let hashtag = 'BREAKING'
let regex = "(?=.*\\bM.*\\b)(?=.*\\bR.*\\b)"
let postId = '7fd548de-f3fb-495e-901f-f6857d4e73c9'
let postOffsetNext={ timestamp:ISODate("2023-02-23T11:16:00.890Z"), id:'b9e670ad-958a-4842-8ada-e6ae76c0ee1f'}
let postHashtagsOffset = ISODate();

/*** QUERIES ***/

// Login
const authenticateReporter = () => db.reporters.find(
        {$and:[{email:'adrian.herrero@example.com'}, {password:'3fc409bf40a364b43fa6d11b0e4610bccbc0ca2707e5976ba219155a92dcb9cb'}]}).hint(indexes ? {}:{$natural: 1})
    .explain('executionStats')


// Reporter by reporter id
const reportersByReporterId = () =>
db.reporters.aggregate([
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
                        cond:{}, limit:25}}}}])
    .explain('executionStats')

    
// Reporters by full name 
const reportersByFullName = () =>
db.reporters.find({$and:[
        {fullName: {$regex: regex , $options:'i'}},
        {$or:[
                {$and:[
                        {fullName: 'Mario Rossi'},
                        {reporterId:{$gt: 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44'}}
                    ]},
                {fullName: {$gt:'Mario Rossi'}}
            ]}
    ]}, {fullName:1}).sort({fullName:1}).limit(25)
    .explain('executionStats')

// Post by reporter id
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
                                    {"$and": [{"$eq": ["$$posts.timestamp", ISODate("2023-02-23T11:16:00.89Z")]},
                                            {"$lt": ["$$posts._id", "b9e670ad-958a-4842-8ada-e6ae76c0ee1f"]}]},
                                    {"$lt": ["$$posts.timestamp", ISODate("2023-02-23T11:16:00.89Z")]}]}}}}},
    {$group:{_id:'$reporterId', posts:{$push: '$posts'}}},
    {$project: {reporterId:1,
            posts:{$filter: {
                    input:{
                        $sortArray:{
                            input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                            sortBy:{'timestamp':-1}}},
                    cond:{}, limit:pageSize}}}}
]).explain('executionStats')


// Post by hashtag (next)
const postByHashtag = () => db.reporters.aggregate([
    {$match:{'posts.hashtags':hashtag}},
    {$project:{
            'reporterId': 1,
            'posts': {
                $filter: {input: '$posts', as: 'posts', cond: {
                        $and: [
                            {$in: ['BREAKING', {$ifNull: ['$$posts.hashtags', []]}]},
                            {$or: [ // Condition only for next page
                                    {$and: [{
                                            $eq:['$$posts.timestamp', postHashtagsOffset]},
                                            {$lt:['$$posts._id', postId]}]},
                                    {$lt: ['$$posts.timestamp', postHashtagsOffset]}]}]}}}}},
    {$addFields: {'posts.reporterId': '$reporterId'}},
    {$group:{_id: '', posts: {$push: '$posts'}}},
    {$project: {_id:0, posts:{
                $filter: {
                    input:{
                        $sortArray:{
                            input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                            sortBy:{'timestamp':-1}}},
                    cond:{}, limit:pageSize}}}}
]).explain('executionStats')


// All readers
const allReaders = () =>
    db.users.find({$and:[
            {isAdmin: {$exists:false}},
            {$or:[
                    {$and:[{fullName: 'Lucas French'}, {'_id':{$gt: '84ee74bc-f0b1-4ded-bce5-42eb6ed5911e'}}]},
                    {fullName: {$gt:'Lucas French'}}
                ]}
        ]}, {password:-1}).sort({fullName: 1}).limit(25)
    .explain('executionStats')

// Comments by post id (prev)
const commentsByPostId = () =>
    db.comments.find({$and:[
            {'post._id': '327ba9a1-f476-4469-8127-acda3c4096f2'},
            {$or:[
                    {$and:[{timestamp: new Date('2017-07-14T14:47:11.000+00:00')}, {'_id':{$gt: '58397250-cd1d-4f24-8356-64cdfa3bb56f'}}]},
                    {timestamp: {$gt: new Date('2017-07-14T14:47:11.000+00:00')}}
                ]}
        ]}).sort({timestamp: 1, _id: 1}).limit(25)
    .explain('executionStats')


const QUERIES = [
    {name:'reporterAuthentication', collection:'reporters', query: authenticateReporter},
    {name:'reportersByFullName', collection:'reporters', query: reportersByFullName},
    {name:'reportersByReporterId', collection:'reporters', query: reportersByReporterId},
    {name:'allReaders', collection:'users', query: allReaders},
    {name:'commentsByPostId', collection:'comments', query: commentsByPostId},
    {name:'postsByHashtag', collection:'reporters', query: postByHashtag},
    {name:'postByReporterId', collection:'reporters', query: postsByReporterId}
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


// Run tests

indexesTest()
