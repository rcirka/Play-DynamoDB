package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.result.DescribeTableResult
import com.rcirka.play.dynamodb.{DynamoDbWebService, DynamoDBClient}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class GlobalDynamoDao(val client: DynamoDBClient) {

  val webService = DynamoDbWebService(client)

  // TODO: Move to central location
  def listTables() : Future[Seq[String]] = {
    webService.post("DynamoDB_20120810.ListTables", Json.obj()).map{ result =>
      (result.json \ "TableNames").as[Seq[String]]
    }
  }

  def deleteTable(tableName: String) : Future[Unit] = {
    webService.post("DynamoDB_20120810.DeleteTable", Json.obj("TableName" -> tableName)).map(x => ())
  }

  def createTable(tableName: String) : Future[Unit] = {
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
      ),
      "TableName" -> tableName
    )

    webService.post("DynamoDB_20120810.CreateTable", json).map(x => ())
  }

  def describeTable(tableName: String) : Future[DescribeTableResult] = {
    webService.post("DynamoDB_20120810.DescribeTable", Json.obj("TableName" -> tableName)).map(_.json.as[DescribeTableResult])
  }
}
