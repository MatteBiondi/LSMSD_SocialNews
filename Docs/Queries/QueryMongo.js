/****************************
 1) Search posts by hashtags
 ****************************/
let hashtag = 'BREAKING'
let offset = ISODate('2010-02-01T21:57:05.536Z');
db.reporters.aggregate([
    {$match:{'posts.hashtags':hashtag}},
    {$project:{reporterId:1, posts:{$filter:{input:'$posts', as:'posts', cond:{$and:[
                            {$in:[hashtag, {$ifNull:['$$posts.hashtags',[]]}]},
                            {$gte:['$$posts.timestamp',ISODate('2023-01-17T00:00:00.000Z')]}
                        ]}}}}},
    {$unwind:'$posts'},
    {$sort:{'posts.timestamp':-1,'posts._id':-1}},
    {$limit:10},
    {$group:{reporterId: {$first: '$reporterId'}, _id: '$posts._id', posts: { $push: '$posts'}}},
    {$sort:{'posts.timestamp':-1,'posts._id':-1}},
])

/********************************************
 2) Reporter by reporterId, pagination posts 
 ********************************************/
let reporterId = '9604c865-b5cb-4c7c-ae00-bdd5320e7f4a';
let offset = ISODate('2023-02-01T21:57:05.536Z');
let pageSize = 50;
db.reporters.aggregate([
    {$match:{'reporterId':reporterId}},
    {$unwind:'$posts'},
    {$match:{'posts.timestamp':{$lt:offset}}},
    {$sort: {'posts.timestamp': -1}},
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
let regex = "(?=.*\\bM.*\\b)(?=.*\\bR.*\\b)"
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


/**
 * Statistics
 */
/********************************************
 1) Most active reader in the last period
 ********************************************/
let period = 1000 * 60 * 60 //In milliseconds
let threshold = new Date(ISODate().getTime() - period)
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
let period = (1000 * 60) * 60 * 24 //In milliseconds
let threshold = new Date(ISODate().getTime() - period)
let reporterId='1ca0e8d8-3020-4f82-ab0f-27f7cad04cda'

db.reporters.aggregate([
    {$match:{'reporterId':reporterId}},
    {$project:{_id:0, posts:1, reporterId:1}},
    {$unwind:'$posts'},
    {$match: {'posts.timestamp':{$gte:threshold}, 'posts.numOfComment':{$exists:true}}},
    {$sort:{'posts.numOfComment':-1,'posts.timestamp':-1}},
    {$limit:10},
    {$group:{_id:'$reporterId', reporterId:{$first:'$reporterId'}, posts:{$push:'$posts'}}}
])

/********************************************
 5) Hot moment of the day in the last period
 ********************************************/
let period = (1000 * 60) * 60 * 24 //In milliseconds
let threshold = new Date(ISODate().getTime() - period)
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
