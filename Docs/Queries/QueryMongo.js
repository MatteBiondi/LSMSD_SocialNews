/****************************
 1) Search posts by hashtags
 ****************************/
db.reporters.aggregate([
        {$match:{'posts.hashtags':'sport'}},
        {$project:{reporterId:1, posts:{$filter:{input:'$posts', as:'posts', cond:{$in:['sport', '$$posts.hashtags']}}}}},
        {$unwind:'$posts'},
        {$group:{reporterId: {$first: '$reporterId'}, _id: '$reporterId', posts: { $push: '$posts'}}}
])

/********************************************
 2) Reporter by reporterId, pagination posts 
 ********************************************/
let reporterId = '9604c865-b5cb-4c7c-ae00-bdd5320e7f4a';
let offset = ISODate("2023-02-01T21:57:05.536Z");
let pageSize = 50;
db.reporters.aggregate([
    {$match:{'reporterId':reporterId}},
    {$unwind:'$posts'},
    {$match:{'posts.timestamp':{$lt:offset}}},
    {$sort: {'posts.timestamp': -1}},
    {$limit: pageSize},
    {$group:{
        _id: '$_id',
        email: {$first:"$email"},
        reporterId: {$first:"$reporterId"},
        fullName: {$first:"$fullName"},
        gender: {$first:"$gender"},
        location: {$first:"$location"},
        dateOfBirth: {$first:"$dateOfBirth"},
        cell: {$first:"$cell"},
        picture: {$first:"$picture"},
        posts: {$push:'$posts'}
    }},
])