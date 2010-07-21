
Mongo JDBC Driver
===========

This is a fork of http://github.com/erh/mongo-jdbc
- Changed build to maven
- added DatabaseMetaData and ResultSetMetaData
- added further functionality to allow the driver to be used in Eclipse and squirrel-sql.
- added a few more tests

Check out the tests for examples of what the driver can currently perform.

Known issues:
- jsqlparser doesn't seem to parse SELECT * FROM 'tablename';

Original README follows:

__EXPERIMENTAL__

This is an experimental JDBC driver for MongoDB.  It attempts to map some basic SQL to MongoDB syntax.  
One of the interesting things is that if you use prepared statements, you can actually use embedded objects, etc... quite nicely. 
See examples/ for more info, ideas.

Home: http://github.com/erh/mongo-jdbc/

License: Apache 2

### Supported
 - SELECT
   - field selector
   - order by
 - INSERT
 - UPDATE
   - basics
 - DROP

### TODO
 - create index
 - insert & getLastError
 - embedded objects  (foo.bar)
 - prepared statements
 - (s|g)etObject( 0 )
