package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.models.enums.KeyType
import com.rcirka.play.dynamodb.models.{KeyCondition, AttributeDefinition}
import com.rcirka.play.dynamodb.models.indexes.{TableIndex, AttributeIndex}
import com.rcirka.play.dynamodb.requests.{QueryRequest, CreateTableRequest}
import com.rcirka.play.dynamodb.results.DescribeTableResult
import com.rcirka.play.dynamodb.{DynamoDBClient, DynamoDbWebService}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.rcirka.play.dynamodb.utils.SeqUtils.SeqHelper
import com.rcirka.play.dynamodb.utils.JsonHelper._

abstract class BaseDynamoDao[Model: Format](
  val client: DynamoDBClient,
  val tableName: String,
  val keySchema: Seq[AttributeIndex],
  val globalSecondaryIndexes: Seq[TableIndex] = Nil,
  val localSecondaryIndexes: Seq[TableIndex] = Nil,
  val attributeDefinitions: Seq[AttributeDefinition]
) {

  val webService = DynamoDbWebService(client)
  val tableNameJson = Json.obj("TableName" -> tableName)

  //new GlobalDynamoDao(client).createTableIfMissing(tableName)

  // Block thread for table creation
  val result = Await.result(new GlobalDynamoDao(client).createTableOnComplete(
    CreateTableRequest(
      tableName,
      keySchema = keySchema,
      globalSecondaryIndexes = globalSecondaryIndexes.toOption,
      localSecondaryIndexes = localSecondaryIndexes.toOption,
      attributeDefinitions = attributeDefinitions
    )
  ), 30 seconds)


  private val primaryKey : String =
    keySchema.find(_.keyType == KeyType.Hash).map(_.attributeName)
      .getOrElse(throw new Exception("Primary key must be defined"))

  def tableExists() : Future[Boolean] = {
    webService.post("DynamoDB_20120810.ListTables", tableNameJson).map { result =>
      val json = result.json.as[JsObject]

      (json \ "TableStatus").asOpt[String].exists(_.contains("ACTIVE", "CREATING", "UPDATING"))
    }
  }

  def deleteTable() : Future[Unit] = {
    webService.post("DynamoDB_20120810.DeleteTable", tableNameJson).map(x => ())
  }

  def getAll() : Future[Seq[Model]] = {
    webService.scan(tableNameJson).map { result =>
      result.map(_.as[Model])
    }
  }

  /**
   * Find one by primary key.
   * @param value
   * @tparam T
   * @return
   */
  def findOne[T: Writes](value: T) : Future[Option[Model]] = {
    val json = Json.obj(
      "Key" -> Json.obj(
        primaryKey -> value
      )
    ) ++ tableNameJson

    webService.getItem(json).map { _.map(_.as[Model]) }
  }


  /**
   * Find one by key and range
   * @param hash
   * @param range
   * @tparam A
   * @tparam B
   * @return
   */
  def findOne[A: Writes, B: Writes](hash: A, range: B) : Future[Option[Model]] = ???

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

  def query(keyCondition: KeyCondition) : Future[Seq[Model]] = {
    val request = QueryRequest(tableName, keyConditions = Seq(keyCondition))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { result =>
      val itemsJson = (result.json \ "Items").asOpt[Seq[JsObject]]
      itemsJson.map(_.map(unwrapItem(_).as[Model])).getOrElse(Nil)
    }
  }

  def queryByIndex(index: String, keyCondition: KeyCondition) : Future[Seq[Model]] = {
    val request = QueryRequest(tableName, Some(index), Seq(keyCondition))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { result =>
      val itemsJson = (result.json \ "Items").asOpt[Seq[JsObject]]
      itemsJson.map(_.map(unwrapItem(_).as[Model])).getOrElse(Nil)
    }
  }

}
