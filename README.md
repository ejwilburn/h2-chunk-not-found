# H2 Chunk Not Found Bug Reproduction

Before running, edit `src/main/java/com/validity/h2bug/H2ChunkNotFoundBug.java` and change the location of the Database 
in the `H2_DB_DIR` constant.  To more easily reproduce the issue the location should be a spinning disk or network drive
or this should be run in a VM with limited IOPS like an Amazon Workspace.

If it doesn't reproduce the issue with the default settings, edit the `NUMBER_OF_DUPLICATE_GROUPS` and/or `GROUP_SIZE`
constants in `src/main/java/com/validity/h2bug/H2ChunkNotFoundBug.java`, increasing the total number of records being
created until the issue is reproduced.

`WRITE_DELAY` can be adjusted in `src/main/java/com/validity/h2bug/database/DatabaseProvider.java` in the `H2_OPTIONS`
constant.

To run this just run `mvn` in the project root.