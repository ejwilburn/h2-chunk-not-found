# H2 Chunk Not Found Bug Reproduction

### Requirements

Java 17 & Maven 3.9.6+

### Before Running

Before running, edit `src/main/java/com/validity/h2bug/H2ChunkNotFoundBug.java` and change the location of the database
in the `H2_DB_DIR` constant.  To more easily reproduce the issue the location should be a spinning disk or network drive
or this should be run in a VM with limited IOPS like an Amazon Workspaces.

I'd recommend at least 50GB free on the target drive, but 30GB would probably work.  If you increase the number of
records generated that will go up of course.

### Running

To run this just run `mvn` in the project root.

### Data Model

The database is created by Liquibase using the configuration in `src/main/resources/config/liquibase/changelog/initial_schema.yml`.

### Notes

If it doesn't reproduce the issue with the default settings, edit the `NUMBER_OF_DUPLICATE_GROUPS` and/or `GROUP_SIZE`
constants in `src/main/java/com/validity/h2bug/H2ChunkNotFoundBug.java`, increasing the total number of records being
created until the issue is reproduced.

`WRITE_DELAY` can be adjusted in `src/main/java/com/validity/h2bug/database/DatabaseProvider.java` in the `H2_OPTIONS`
constant.  Increasing the write delay increases the amount of data required to reproduce this issue.  Using faster
storage will also increase the amount of data required to reproduce the issue.

The error occurs after all records have been inserted into the `file_row_key` and `generated_key` tables when
executing an `INSERT INTO import_data SELECT FROM file_row_key INNER JOIN generated_key` followed by a
`DELETE file_row_key` statement in the same JDBC execute call.  If those two queries are separated into two
separate JDBC executes then the error does not occur, or does not occur with the same amount of data anyway.
The statement is in `ImportDataDaoSerivce.addMatches()`.

If you want to keep the database after running this, edit `H2ChunkNotFoundBug.init()` and comment out the lines
that mark the DB files for deletion on JVM exit.