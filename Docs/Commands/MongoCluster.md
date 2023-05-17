# MongoDB cluster configuration

## Launch mongod instances
```bash
# All replicas
mkdir -p ~/mongo/{data,logs,auth}

# Create once and copy on each replica nodes
openssl rand -base64 756 > ~/mongo/auth/keyfile.pem
chmod 400 ~/mongo/auth/keyfile.pem

# First replica node: 172.16.5.20
mongod --replSet socialNews --dbpath ~/mongo/data --port 27017 --bind_ip localhost,172.16.5.20 --oplogSize 200 --fork --logpath ~/mongo/logs/mongod.log --auth --keyFile ~/mongo/auth/keyfile.pem

# Second replica node (primary): 172.16.5.21
mongod --replSet socialNews --dbpath ~/mongo/data --port 27017 --bind_ip localhost,172.16.5.21 --oplogSize 200 --fork --logpath ~/mongo/logs/mongod.log --auth --keyFile ~/mongo/auth/keyfile.pem

# Third replica node: 172.16.5.22
mongod --replSet socialNews --dbpath ~/mongo/data --port 27017 --bind_ip localhost,172.16.5.22 --oplogSize 200 --fork --logpath ~/mongo/logs/mongod.log --auth --keyFile ~/mongo/auth/keyfile.pem
```

## Configure cluster
The following commands must be issued only on the primary node via **_mongosh_**

### Init cluster
``` javascript
rsconf = {
    _id: "socialNews",
    members: [
     	{_id: 0, host: "172.16.5.20:27017", priority:2},
     	{_id: 1, host: "172.16.5.21:27017", priority:3},
        {_id: 2, host: "172.16.5.22:27017", priority:1}],
    writeConcernMajorityJournalDefault: false // Determines the behavior of { w: "majority" } write concern if the 
                                                //  write concern does not explicitly specify the journal option j 
};

rs.initiate(rsconf);

db.adminCommand({
    setDefaultRWConcern: 1,
    defaultReadConcern: {level: 'majority'},
    defaultWriteConcern: {w: 'majority', j: false, wtimeout: 10000}
});

rs.status(); // Check cluster status
```

### Create database users
```javascript
use admin

db.createUser(
  {
    user: "admin",
    pwd:"admin", // or cleartext password
    roles: [
      { role: "userAdminAnyDatabase", db: "admin" },
      { role: "readWriteAnyDatabase", db: "admin" }, 
      { role: 'root', db: 'admin' }
    ]
  }
)

db.createUser(
  {
    user: "socialnews",
    pwd: "root",
    roles: [{role: "dbOwner", db:"socialNewsDB"}]
  }
)
```