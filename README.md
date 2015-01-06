Play-DynamoDB
=============

A DynamoDB API and data access layer for Scala Play. This is not a wrapper around the AWS java sdk, but a native implementation using Scala Futures and Play Web services. There is no dependency on the official DynamoDb sdk, and only references the core AWS sdk for obtaining credentials and signing the http requests.

The motivations behind this library is to provide a simple, asynchronous data access layer for DynamoDB, and leverage the native json parsing of the Play framework. Although the AWS sdk does have asynchronous methods, it uses java futures, which have performance problems and are not compatible with Play. Also, creating the database requests are quite verbose, which this library aims to simplify.

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
To connect to an AWS instance of DynamoDB, you must specify the endpoint or region and either the accesskey/secret key or the aws settings specified in ~/.aws/credentials  

[http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)


```
dynamodb {
  region = "us-east-1"
  accesskey = "your_access_key"
  secretkey = "your_secret_key"
}
```

or 

```
dynamodb {
  region = "us-east-1"
  profile = "default"
}
```

## Creating a Model and Database Access Layer

A case class with Format works well as a model, although any class that conforms to Play JSON reads/writes will work. Let's take for example a message model.

```scala
import java.util.UUID

case class Message (
  id: String = UUID.randomUUID().toString,
  date: DateTime = DateTime.now,
  body: String
)

object Message {
  implicit val messageFormat = Json.format[Message]
}
```
DynamoDB doesn't auto-generate keys, a common convention is to generate a UUID.

To create the data access object, you will need to inherit from 
BaseDynamoDao.

```scala
import com.rcirka.play.dynamodb.DynamoDBClient
import com.rcirka.play.dynamodb.dao.BaseDynamoDao
import com.rcirka.play.dynamodb.models._
import com.rcirka.play.dynamodb.models.enums._
import com.rcirka.play.dynamodb.models.indexes._

object MessageDAO extends BaseDynamoDao[Message] (
  client = DynamoDBClient.db,
  tableName = "Messages",
  keySchema = Seq(AttributeIndex("id", KeyType.Hash)),
  attributeDefinitions = Seq(
    AttributeDefinition("id", AttributeType.String)
  )
) {

  }
```

client, tableName, keySchema, and attributeDefinitions are required parameters. DynamoDBClient.db for client will automatically use the settings defined in application.conf.

keySchema is the definition for the primary key. attributeDefinitions contains a list of all keys and their data types. If you define keys for global or local indexes, they will need to be listed here.

The table will automatically be created when the doa is first initialized. Note, when connecting with AWS, a table often takes time to create (usually between 30-60 seconds). You will need to wait before making queries, otherwise you will get an exception.

Also note that indexes must be specified on table creation. You can not add/remove indexes afterwards, the table must be dropped and re-created.

## Basic DAO Operations

get/delete/put are standard operations of the base dao, you don't need to define them. 

Here is a basic example of a play controller. Note that since the operations are asynchronous, you need to define the actions as async and map the results as demonstrated below.

```scala
object MessageController extends Controller {
  def get(id: String) = Action.async { request =>
    MessageDAO.get(id).map(message => Ok(Json.toJson(message)))
  }
  
  def delete(id: String) = Action.async { request =>
    MessageDAO.delete(id).map(_ => Ok(""))
  }

  def put() = Action.async(parse.json) { request =>
    request.body.validate[Message].map {
      case message: Message => MessageDAO.put(message).map(_ => Ok(""))
    }.recoverTotal {
      e => Future(BadRequest(Json.obj("error" -> JsError.toFlatJson(e))))
    }
  }
}
```

## Querying

## Indexes

## Unit testing



