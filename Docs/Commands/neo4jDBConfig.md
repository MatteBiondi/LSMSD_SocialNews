# INDEXES SETUP:
Reporter:
```
CREATE RANGE INDEX reporter_id_index IF NOT EXISTS
FOR (r:Reporter)
ON (r.reporterId)
```
Reader:
```
CREATE RANGE INDEX reader_id_index IF NOT EXISTS
FOR (r:Reader)
ON (r.readerId)
```
Post:
```
CREATE RANGE INDEX post_id_index IF NOT EXISTS
FOR (p:Post)
ON (p.postId)
```
Report:
```
CREATE RANGE INDEX report_id_index IF NOT EXISTS
FOR ()-[r:REPORT]-()
ON (r.reportId)
```
___
To ensure that everything is successfully completed:
```
SHOW ALL INDEXES
```
To delete in case of error:
```
DROP INDEX index_name [IF EXISTS]
```
___
**Notes:**
* Creating an index requires the *CREATE INDEX* privilege
* Dropping an index requires the *DROP INDEX* privilege
* Listing indexes require the *SHOW INDEX* privilege.

# CONSTRAINTS SETUP:
Reporter node:
```
CREATE CONSTRAINT reporter_id_existence_constraints IF NOT EXISTS
FOR (r:Reporter)
REQUIRE r.reporterId IS NOT NULL

CREATE CONSTRAINT reporter_name_existence_constraints IF NOT EXISTS
FOR (r:Reporter)
REQUIRE r.fullName IS NOT NULL

CREATE CONSTRAINT reporter_picture_existence_constraints IF NOT EXISTS
FOR (r:Reporter)
REQUIRE r.picture IS NOT NULL

CREATE CONSTRAINT reporter_id_uniqueness_constraints IF NOT EXISTS
FOR (r:Reporter)
REQUIRE r.reporterId IS UNIQUE
```
Reader node:
```
CREATE CONSTRAINT reader_id_existence_constraints IF NOT EXISTS
FOR (r:Reader)
REQUIRE r.readerId IS NOT NULL

CREATE CONSTRAINT reader_id_uniqueness_constraints IF NOT EXISTS
FOR (r:Reader)
REQUIRE r.readerId IS UNIQUE
```
Post node:
```
CREATE CONSTRAINT post_id_existence_constraints IF NOT EXISTS
FOR (p:Post)
REQUIRE p.postId IS NOT NULL

CREATE CONSTRAINT post_id_uniqueness_constraints IF NOT EXISTS
FOR (p:Post)
REQUIRE p.postId IS UNIQUE
```
Report relationship:
```
CREATE CONSTRAINT report_id_existence_constraints IF NOT EXISTS
FOR ()-[r:REPORT]-()
REQUIRE r.reportId IS NOT NULL

CREATE CONSTRAINT report_text_existence_constraints IF NOT EXISTS
FOR ()-[r:REPORT]-()
REQUIRE r.text IS NOT NULL

CREATE CONSTRAINT report_timestamp_existence_constraints IF NOT EXISTS
FOR ()-[r:REPORT]-()
REQUIRE r.timestamp IS NOT NULL
```
___
To ensure that everything is successfully completed:
```
SHOW ALL CONSTRAINTS
```

To delete in case of error:
```
DROP CONSTRAINT constraint_name [IF EXISTS]
```
___
**Notes:**
* Creating a constraint requires the *CREATE CONSTRAINT* privilege.
* Dropping a constraint requires the *DROP CONSTRAINT* privilege.
* Listing constraints requires the *SHOW CONSTRAINTS* privilege.
* 'IS UNIQUE' does not allow relationship patterns in current version
* Property existence constraint requires Neo4j Enterprise Edition