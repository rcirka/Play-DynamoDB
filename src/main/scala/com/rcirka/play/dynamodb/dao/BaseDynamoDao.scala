package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.{DynamoDBClient, DynamoDbWebService}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseDynamoDao[Model: Format](tableName: String, client: DynamoDBClient) {

  val webService = DynamoDbWebService(client)
  val tableNameJson = Json.obj("TableName" -> tableName)

  def listTables() : Future[Seq[String]] = {
    webService.post("DynamoDB_20120810.ListTables", Json.obj()).map{ result =>
      (result.json \ "TableNames").as[Seq[String]]
    }
  }

  def deleteTable(tableName: String) : Future[Unit] = {
    webService.post("DynamoDB_20120810.DeleteTable", Json.obj("TableName" -> tableName)).map(x => ())
  }

  def createTable() : Future[Unit] = {
    val json = Json.obj(
      "KeySchema" -> Json.arr(
        Json.obj(
          "AttributeName" -> "id",
          "KeyType" -> "HASH"
        )
      ),
      "AttributeDefinitions" -> Json.arr(
        Json.obj(
          "AttributeName" -> "id",
          "AttributeType" -> "S"
        )
      ),
      "ProvisionedThroughput" -> Json.obj(
        "ReadCapacityUnits" -> 5,
        "WriteCapacityUnits" -> 5
      )
    ) ++ tableNameJson

    //println(Json.prettyPrint(json))

    webService.post("DynamoDB_20120810.CreateTable", json).map(x => ())
  }

  def doesTableExist() : Future[Boolean] = {
    webService.post("DynamoDB_20120810.ListTables", tableNameJson).map { result =>
      val json = result.json.as[JsObject]

      (json \ "TableStatus").asOpt[String].exists(_.contains("ACTIVE", "CREATING", "UPDATING"))
    }
  }

  def deleteTable() : Future[Unit] = {
    webService.post("DynamoDB_20120810.DeleteTable", tableNameJson).map(x => ())
  }

  def findAll() : Future[Seq[Model]] = {
    webService.scan(tableNameJson).map { result =>
      result.map(_.as[Model])
    }
  }

  def findOne(key: JsObject) : Future[Option[Model]] = {
    val json = Json.obj(
      "Key" -> key
    ) ++ tableNameJson

    webService.getItem(json).map { _.map(_.as[Model]) }
  }

  def putOne(model: Model) = {
    val json = Json.obj(
      "Item" -> model
    ) ++ tableNameJson

    webService.putItem(json).map(x => ())
  }


  def deleteOne(key: JsObject) : Future[Unit] = {
    val json = Json.obj(
      "Key" -> key
    ) ++ tableNameJson

    webService.getItem(json).map { x => () }
  }

}
