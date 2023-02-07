# INDEXES SETUP:
```

```

# CONSTRAINTS SETUP:
*To use this commands, CREATE_CONSTRAINT privilege is needed* 

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
CREATE CONSTRAINT report_text_existence_constraints IF NOT EXISTS
FOR ()-[r:REPORT]-()
REQUIRE r.text IS NOT NULL

CREATE CONSTRAINT report_timestamp_existence_constraints IF NOT EXISTS
FOR ()-[r:REPORT]-()
REQUIRE r.timestamp IS NOT NULL
```

To ensure that everything is accepted:
```
SHOW ALL CONSTRAINTS
```

To delete in case of error:
```
DROP CONSTRAINT constraint_name [IF EXISTS]
```