package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.exception.ResourceNotFoundException
import com.rcirka.play.dynamodb.models.enums.{QuerySelect, KeyType}
import com.rcirka.play.dynamodb.models.{KeyCondition, AttributeDefinition}
import com.rcirka.play.dynamodb.models.indexes.{TableIndex, AttributeIndex}
import com.rcirka.play.dynamodb.requests.{QueryRequest, CreateTableRequest}
import com.rcirka.play.dynamodb.results.DescribeTableResult
import com.rcirka.play.dynamodb.{DynamoDBClient, DynamoDbWebService}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.rcirka.play.dynamodb.utils.SeqUtils.SeqHelper
import com.rcirka.play.dynamodb.utils.JsonHelper._
import com.rcirka.play.dynamodb.Dsl._

abstract class BaseDynamoDao[Model: Format](
  val client: DynamoDBClient,
  val tableName: String,
  val keySchema: Seq[AttributeIndex],
  val globalSecondaryIndexes: Seq[TableIndex] = Nil,
  val localSecondaryIndexes: Seq[TableIndex] = Nil,
  val attributeDefinitions: Seq[AttributeDefinition],
  val blockOnTableCreation : Boolean = false // Primary meant for unit tests
)(implicit ec: ExecutionContext) {

  val webService = DynamoDbWebService(client)
  val tableNameJson = Json.obj("TableName" -> tableName)

  private val primaryKey : String =
    keySchema.find(_.keyType == KeyType.Hash).map(_.attributeName)
      .getOrElse(throw new Exception("Primary key must be defined"))

  if (blockOnTableCreation) createTableIfMissingAndBlock() else createTableIfMissing()

// TODO: This will probably fail in AWS due to the default future timeout of 10 seconds. Need to modify future timeout to make this work.
  private def createTableIfMissingAndBlock(): Unit = {
    // Block thread for table creation
    val exists = Await.result(tableExists(), 30 seconds)

    if (!exists) {
      Await.result(new GlobalDynamoDao(client).createTableOnComplete(
        CreateTableRequest(
          tableName,
          keySchema = keySchema,
          globalSecondaryIndexes = globalSecondaryIndexes.toOption,
          localSecondaryIndexes = localSecondaryIndexes.toOption,
          attributeDefinitions = attributeDefinitions
        )
      ), 30 seconds)
    }
  }

  private def createTableIfMissing(): Unit = {
    tableExists().onComplete{
      case Success(exists) => {
        if (!exists) {
          new GlobalDynamoDao(client).createTable(
            CreateTableRequest(
              tableName,
              keySchema = keySchema,
              globalSecondaryIndexes = globalSecondaryIndexes.toOption,
              localSecondaryIndexes = localSecondaryIndexes.toOption,
              attributeDefinitions = attributeDefinitions
            )
          ).onComplete {
            case Success(e) => ()
            case Failure(e) => ()
          }
        }
      }
      case Failure(e) => ()
    }
  }

  def tableExists() : Future[Boolean] = {
    webService
      .post("DynamoDB_20120810.DescribeTable", tableNameJson)
      .map(_ => true)
      .recover {
        case e: ResourceNotFoundException => false
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
  def get[T: Writes](value: T) : Future[Option[Model]] = {
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
   * @return Future[Option[Model]]
   */
  def findOne[A: Writes, B: Writes](hash: A, range: B) : Future[Option[Model]] = ???

  def put(model: Model) = {
    val json = Json.obj(
      "Item" -> model
    ) ++ tableNameJson

    webService.putItem(json).map(x => ())
  }

  def delete[A: Writes](value: A) : Future[Unit] = {
    val json = Json.obj(
      "Key" -> Json.obj(
        primaryKey -> wrapItemVal(Json.toJson(value))
      )
    ) ++ tableNameJson

    webService.post("DynamoDB_20120810.DeleteItem", json).map(x => ())
  }

  def query[A: Writes](keyValue: A, rangeCondition: KeyCondition) : Future[Seq[Model]] = {
    val request = QueryRequest(tableName, keyConditions = Seq(primaryKey $eq keyValue, rangeCondition))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { result =>
      val itemsJson = (result.json \ "Items").asOpt[Seq[JsObject]]
      itemsJson.map(_.map(unwrapItem(_).as[Model])).getOrElse(Nil)
    }
  }

  def queryByIndex(index: String, hashCondition: KeyCondition) : Future[Seq[Model]] = {
    val request = QueryRequest(tableName, Some(index), Seq(hashCondition))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { result =>
      val itemsJson = (result.json \ "Items").asOpt[Seq[JsObject]]
      itemsJson.map(_.map(unwrapItem(_).as[Model])).getOrElse(Nil)
    }
  }

  def queryByIndex(index: String, hashCondition: KeyCondition, rangeCondition: KeyCondition) : Future[Seq[Model]] = {
    val request = QueryRequest(tableName, Some(index), Seq(hashCondition, rangeCondition))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { result =>
      val itemsJson = (result.json \ "Items").asOpt[Seq[JsObject]]
      itemsJson.map(_.map(unwrapItem(_).as[Model])).getOrElse(Nil)
    }
  }

  def exists(index: String, keyCondition: KeyCondition): Future[Boolean] = {
    val request = QueryRequest(tableName, Some(index), Seq(keyCondition), Some(QuerySelect.Count))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { response =>
      (response.json \ "Count").as[Int] > 0
    }
  }

  def count(index: String, keyCondition: KeyCondition): Future[Int] = {
    val request = QueryRequest(tableName, Some(index), Seq(keyCondition), Some(QuerySelect.Count))

    webService.post("DynamoDB_20120810.Query", Json.toJson(request)).map { response =>
      (response.json \ "Count").as[Int]
    }
  }

}
