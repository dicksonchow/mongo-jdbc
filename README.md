Mongo JDBC Driver
===========

This is a fork of http://github.com/erh/mongo-jdbc, merged some changes from:
- https://github.com/davemc/mongo-jdbc
- https://github.com/ccdaisy/mongo-jdbc

__EXPERIMENTAL__

This is an experimental JDBC driver for MongoDB.  It attempts to map some basic SQL to MongoDB syntax.  


### Project Scope

- [x] support __SELECT__ on single table
- [x] support __WHERE__
- [x] support __ORDER BY__ and __LIMIT__
- [ ] upgrade Mongo Java Driver to 3.x

### Build

```
$ mvn package -Dmaven.test.skip=true
```

Or, if you have a testing MongoDB instance running on localhost and listening to port 27017, you can
```
$ mvn package
```

License: Apache License, Version 2.0