package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.requests.CreateTableRequest
import com.rcirka.play.dynamodb.results.DescribeTableResult
import com.rcirka.play.dynamodb.{DynamoDbWebService, DynamoDBClient}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import retry._
import CreateTableRequest._

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

  def tableExists(tableName: String) : Future[Boolean] = {
    webService.post("DynamoDB_20120810.ListTables", Json.obj("TableName" -> tableName)).map { result =>
      val json = result.json.as[JsObject]

      (json \ "TableStatus").asOpt[String].exists(_.contains("ACTIVE", "CREATING", "UPDATING"))
    }
  }

  def createTableIfMissing(tableName: String) : Unit = {
    tableExists(tableName).map { result =>
      println(s"RESULT - $result")
      if (!result) createTable(tableName) else ()
    }.onComplete {
      case _ => ()
    }
  }

  def createTable(request: CreateTableRequest) : Future[Unit] = {
    webService.post("DynamoDB_20120810.CreateTable", Json.toJson(request)).map(x => ())
  }

  /**
   * Future is completed when table status has turned active
   * @param tableName
   * @return
   */
  def createTableOnComplete(request: CreateTableRequest) : Future[Unit] = {

    Await.result(createTable(request), 10 seconds)

    implicit val success = Success[DescribeTableResult](_.Table.TableStatus.exists(_ == "ACTIVE"))

    val policy = retry.Pause(60, 1 second)

    val future = policy { () =>
      println("--- Waiting for create table ---")
      describeTable(request.tableName)
    }

    val result = Await.result(future, 10 seconds)
    Future.successful()
  }

  def describeTable(tableName: String) : Future[DescribeTableResult] = {
    webService.post("DynamoDB_20120810.DescribeTable", Json.obj("TableName" -> tableName)).map(_.json.as[DescribeTableResult])
  }
}
