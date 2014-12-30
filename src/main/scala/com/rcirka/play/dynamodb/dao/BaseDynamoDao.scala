package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.models.indexes.{LocalSecondaryIndex, AttributeIndex}
import com.rcirka.play.dynamodb.requests.CreateTableRequest
import com.rcirka.play.dynamodb.results.DescribeTableResult
import com.rcirka.play.dynamodb.utils.AttributeDefinition
import com.rcirka.play.dynamodb.{DynamoDBClient, DynamoDbWebService}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.rcirka.play.dynamodb.utils.SeqUtils.SeqHelper

abstract class BaseDynamoDao[Model: Format](
  val client: DynamoDBClient,
  val tableName: String,
  val primaryAttributeIndex: AttributeIndex,
  val localSecondaryIndexes: Seq[LocalSecondaryIndex] = Nil,
  val attributeDefinitions: Seq[AttributeDefinition]
) {

  val webService = DynamoDbWebService(client)
  val tableNameJson = Json.obj("TableName" -> tableName)

  //new GlobalDynamoDao(client).createTableIfMissing(tableName)

  // Block thread for table creation
  val result = Await.result(new GlobalDynamoDao(client).createTableOnComplete(
    CreateTableRequest(
      tableName,
      keySchema = Seq(primaryAttributeIndex),
      localSecondaryIndexes = localSecondaryIndexes.toOption,
      attributeDefinitions = attributeDefinitions
    )
  ), 30 seconds)

  def tableExists() : Future[Boolean] = {
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
