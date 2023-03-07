let pageSize = 5
let reporterId = 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44';
let hashtag = 'BREAKING'
let regex = "(?=.*\\bM.*\\b)(?=.*\\bR.*\\b)"
let threshold = new Date(0)
let postId = '7fd548de-f3fb-495e-901f-f6857d4e73c9'

/*******************************************
 1) Search posts by hashtags
 *******************************************/
let postHashtagsOffset = ISODate();

// Unwind, no pagination version
db.reporters.aggregate([
    {$match:{'posts.hashtags':hashtag}},
    {$project:{_id:0, reporterId:1, posts:{$filter:{
                    input:'$posts',
                    as:'posts',
                    cond:{$in:[hashtag, {$ifNull:['$$posts.hashtags',[]]}]}}}}},
    {$unwind:'$posts'},
    {$sort:{'posts.timestamp':-1,'posts._id':-1}},
    {$limit:pageSize},
    {$sort:{'posts.timestamp':-1,'posts._id':-1}},
])

// Unwind free, pagination version
db.reporters.aggregate([
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
])

/***********************************************************
 2) Reporter by reporterId
 ***********************************************************/
//Unwind version, no pagination
db.reporters.aggregate([
    {$match:{'reporterId':reporterId}},
    {$unwind:{path:'$posts', preserveNullAndEmptyArrays: true}},
    {$sort: {'email':-1, 'posts.timestamp': -1}},
    {$limit: pageSize},
    {$group:{
            _id: '$_id',
            email: {$first:'$email'},
            reporterId: {$first:'$reporterId'},
            fullName: {$first:'$fullName'},
            gender: {$first:'$gender'},
            location: {$first:'$location'},
            dateOfBirth: {$first:'$dateOfBirth'},
            cell: {$first:'$cell'},
            picture: {$first:'$picture'},
            posts: {$push:'$posts'}
        }},
])

/********************************************
 3) Reporters by full name
 ********************************************/
db.reporters.find({$and:[
        {fullName: {$regex: regex ,$options:'i'}},
        {$or:[
                {$and:[
                        {fullName: {$gte: 'Mario Rossi'}},
                        {reporterId:{$gt: 'b3c022e7-8ecd-429e-b2c8-3b86fb0a8c44'}}
                    ]},
                {fullName: {$gt:'Mario Rossi'}}
            ]}
    ]}, {fullName:1}).limit(5).sort({fullName:1})


/********************************************
 4) Posts by reporterId
 ********************************************/
//Unwind free, pagination next
let postOffsetNext={ timestamp:ISODate("2023-02-23T11:16:00.890Z"), id:'b9e670ad-958a-4842-8ada-e6ae76c0ee1f'}

db.reporters.aggregate([
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
])

//Unwind free, pagination prev
let postOffsetPrev={ timestamp:ISODate("2023-02-22T22:10:01.639Z"), id:'32b61d64-a6f4-4500-8a84-261a46be6363'}

db.reporters.aggregate([
    {$match:
            {$and:[
                    {reporterId: reporterId},
                    {'posts.timestamp': {$lt: postOffsetPrev.timestamp}}]}
    },
    {$project:{
            reporterId:1,
            posts: {
                $filter: {input: '$posts', as: 'posts', cond: {
                        $or: [ // Condition only for next page
                            {$and: [
                                    {$eq:['$$posts.timestamp', postOffsetPrev.timestamp]},
                                    {$gt:['$$posts._id', postOffsetPrev.id]}]},
                            {$gt: ['$$posts.timestamp', postOffsetPrev.timestamp]}]}}}}},
    {$group:{_id:'$reporterId', posts:{$push: '$posts'}}},
    {$project: {reporterId:1,
            posts:{$sortArray:{input:{$filter: {
                            input:{
                                $sortArray:{
                                    input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                                    sortBy:{'timestamp':1}}},
                            cond:{}, limit:pageSize}}, sortBy:{timestamp:-1}}}}}
])

/********************************************
 5) Reporter by reporterId
 ********************************************/
// Unwind free
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
                    cond:{}, limit:pageSize}}}}
])

/**
 * Statistics
 */
/********************************************
 1) Most active reader in the last period
 ********************************************/

db.comments.aggregate([
    {$match:{'timestamp':{$gte:threshold}}},
    {$group:{_id:'$reader._id', fullName:{$first:'$reader.fullName'},numOfComment:{$count:{}}}},
    {$sort:{numOfComment:-1}},
    {$limit:10}
])

/********************************************
 2) Gender statistic
 ********************************************/
db.users.aggregate([
    {$match:{'isAdmin':{$exists:false}}},
    {$bucket: {
            groupBy: {'$switch': {branches: [
                        {case: {'$eq':[{$toLower: '$gender'}, 'male']}, then: 0 },
                        {case: {'$eq':[{$toLower: '$gender'}, 'female']}, then: 1 }],
                    default: -1}},
            boundaries: [0, 1, 2],
            default:-1,
            output: {
                'count': { $sum: 1 },
                'gender':{$first: {$cond: {if:{$in:[{$toLower:'$gender'}, ['male','female']]}, then:'$gender', else:'Other'}}}}}},
    {$project:{_id:0}}
])

/********************************************
 3) Nationality statistic
 ********************************************/
db.users.aggregate([
    {$match:{'isAdmin':{$exists:false}}},
    {$group: {_id: {$toLower:'$country'}, 'country':{$first:'$country'},'count': { $sum: 1 }}},
    {$project:{_id:0}},
    {$sort:{'count':-1}}
])

/********************************************
 4) Hot posts in the last period
 ********************************************/
db.reporters.aggregate([
    {$match:{$and:[{reporterId:reporterId},{posts:{$exists: true}}]}},
    {$project:{
            _id: 0,
            reporterId:1,
            posts: {"$filter":
                    {input: "$posts",
                        as: 'posts',
                        cond: {$and:[{$gte: ['$$posts.timestamp', threshold]}, {$gte: ["$$posts.numOfComment", 1]}]}}}}},
    {$group:{_id:'$reporterId', reporterId:{$first:'$reporterId'}, posts:{$push:'$posts'}}},
    {$project: {
            reporterId:1,
            posts:{$filter: {
                    input:{
                        $sortArray:{
                            input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}},
                            sortBy:{'numOfComment':-1, timestamp:-1, _id:-1}}},
                    cond:{}, limit:pageSize}}}}
])

/********************************************
 5) Hot moment of the day in the last period
 ********************************************/
db.comments.aggregate([
    {$match:{'timestamp':{$gte:threshold}}},
    {$bucket: {
            groupBy: {$hour:'$timestamp'},
            boundaries: [0,6,12,18,24],
            default:-1,
            output: {'count': { $sum: 1 }}}
    },
    {$densify:{field:'_id', range:{step:6, bounds:[0,24]}}},
    {$set: {count: {$cond: [{$not: ['$count']}, 0, '$count']}}},
    {$project: {_id: 0, 'count': 1, 'lowerBound': {$mod:['$_id',24]}, 'upperBound':{$mod:[{$add:['$_id',6]}, 24]}}}
])
