
function buildUsersIndexes(){
    db.users.dropIndexes()

    db.users.createIndex(
        {'email':  1},
        {name:'emailUnique', unique: true, sparse: false}
    )

    db.users.createIndex(
        {'fullName':  1, '_id': 1},
        {name:'sortByFullNamePagination', unique: false, sparse: false}
    )
}

function buildReportersIndexes(){
    db.reporters.dropIndexes()

    db.reporters.createIndex(
        {'email':  1},
        {name:'emailActiveReporterUnique', unique: true, sparse: true}
    )

    db.reporters.createIndex(
        {'reporterId':  1},
        {name:'filterByReporterId', unique: false, sparse: false}
    )

    db.reporters.createIndex(
        {'fullName':  1, 'reporterId': 1},
        {name:'searchByFullNamePagination', unique: false, sparse: false}
    )


    db.reporters.createIndex(
        {'posts.hashtags':  1},
        {name:'filterByPostHashtag', unique: false, sparse: true}
    )
}

function buildCommentsIndexes(){
    db.comments.dropIndexes()

    db.comments.createIndex(
        {'post._id': 1, 'timestamp':1, '_id':1},
        {name:'searchByPostSortByTimestampPagination', unique: false, sparse: false}
    )
}

// Build indexes
buildUsersIndexes()
buildReportersIndexes()
buildCommentsIndexes()