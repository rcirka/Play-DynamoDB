package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.util.{BeforeAfterWithApplication, Test, TestModel}
import com.rcirka.play.dynamodb.util._
import org.specs2.mutable._
import play.api.libs.json._

class BaseDynamoDaoSpec extends Specification {
  "BaseDynamoDaoSpec" should {

    "Create table" in new DynamoDAOSpecContext {
      createTable()

//      val future2 = doa.doesTableExist()
//      Await.result(future2, 10 seconds) must beTrue
    } tag "createtable"

    "Delete table" in new DynamoDAOSpecContext {
      createTable()

      awaitResult(doa.deleteTable())

    } tag "deletetable"

    "List tables" in new DynamoDAOSpecContext {

      val response = awaitResult(doa.listTables())


    } tag "listtables"

    "Insert and get item" in new DynamoDAOSpecContext {
      createTable()

      val model = TestModel("1")
      awaitResult(doa.putOne(model))

      val result = awaitResult(doa.findOne(Json.obj("id" -> "1")))

      Some(model) === result

    } tag "insertitem"

    "Delete Item" in new DynamoDAOSpecContext {
      createTable()

      val model = TestModel("1")
      awaitResult(doa.putOne(model))

      awaitResult(doa.deleteOne(Json.obj("id" -> "1")))
    } tag "deleteitem"

    "return None for GetItem if no result found" in new DynamoDAOSpecContext {
      createTable()
      awaitResult(doa.findOne(Json.obj("id" -> "1"))) === None
    } tag "empty result"

    "Get all Items" in new DynamoDAOSpecContext {
      createTable()

      (1 to 10).foreach{i => awaitResult(doa.putOne(TestModel()))}

      awaitResult(doa.findAll()).length === 10
    } tag "findall"
  }
}

sealed trait DynamoDAOSpecContext extends BeforeAfterWithApplication {

  val tableName = "Tests"

  val doa = new BaseDynamoDao[TestModel](tableName, Test.dbClient) {}

  def before() {}

  def createTable() = awaitResult(doa.createTable())
}