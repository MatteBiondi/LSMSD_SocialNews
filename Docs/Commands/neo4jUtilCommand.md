# Change usual admin password:
*Sources:*
* https://neo4j.com/docs/operations-manual/current/configuration/password-and-user-recovery/

First, disable authentication. Then execute:
```
cypher-shell -d system
```
in a terminal from the bin directory of the DBMS

Then:
```
ALTER USER neo4j SET PASSWORD 'AdminPsw';
```

# Create a new user and grant admin role:
*Sources*
* https://neo4j.com/docs/cypher-manual/current/access-control/manage-users/
* https://neo4j.com/docs/cypher-manual/current/access-control/manage-roles/

```
CREATE USER socialnewsadmin SET PLAINTEXT PASSWORD 'AdminPsw' SET PASSWORD CHANGE NOT REQUIRED SET STATUS ACTIVE SET HOME DATABASE socialnews;
GRANT ROLE admin TO socialnewsadmin;
SHOW USERS;
```