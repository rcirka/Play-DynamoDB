Play-DynamoDB
=============

A DynamoDB API and data access layer for Scala Play. This is not a wrapper around the AWS java sdk, but a native implementation using scala futures and Play Web services. There is no dependency on the official DynamoDb sdk, and only references the core AWS sdk for obtaining credentials and signing the http requests.

The motivations behind this library is to provide a simple, asynchronous data access layer for DynamoDB, and leverage the native json parsing of the Play framework. Although the AWS sdk does have asynchronous methods, it uses java futures, which have performance problems and are not compatible with Play. Also, creating the database requests are quite verbose. This library aims to simplify the database access layer akin to the Mongodb-Play library.

## Dependencies
Currently a minimum of Play 2.3 and Scala 2.11 is required.

```scala
libraryDependencies ++= Seq(
  "com.rcirka" %% "play-dynamodb" % "0.1.0-SNAPSHOT"
)
```

## Configuration

### DynamoDBLocal

To connect to a local instance of DynamoDB, make sure you have DynamoDBLocal install and running. In application.conf, add the following lines. "endpoint" should be "http://localhost:8000". "accesskey" and "secretkey" can be anything

```
dynamodb {
  endpoint = "http://localhost:8000"
  accesskey = "someaccesskey"
  secretkey = "somesecretkey"
}
```
### AWS Instance
To connect to an AWS instance of DynamoDB, you must specify the endpoint and either the accesskey/secret key or the aws credentials profile. 

[http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)


```
dynamodb {
  endpoint = "http://localhost:8000"
  accesskey = "someaccesskey"
  secretkey = "somesecretkey"
}
```



