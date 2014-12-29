package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.exception._
import com.rcirka.play.dynamodb.util.{BeforeAfterWithApplication, Test, TestModel}
import com.rcirka.play.dynamodb.util._
import org.specs2.mutable._
import play.api.libs.json._
import scala.concurrent.duration._

import scala.concurrent.Await

class BaseDynamoDaoSpec extends Specification {
  "BaseDynamoDaoSpec" should {
    "Insert and get item" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.putOne(model))

      val result = awaitResult(dao.findOne(Json.obj("id" -> "1")))

      Some(model) === result

    } tag "insertitem"

    "Delete Item" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.putOne(model))

      awaitResult(dao.deleteOne(Json.obj("id" -> "1")))
    } tag "deleteitem"

    "return None for GetItem if no result found" in new DynamoDAOSpecContext {
      awaitResult(dao.findOne(Json.obj("id" -> "1"))) === None
    } tag "empty result"

    "Get all Items" in new DynamoDAOSpecContext {
      (1 to 10).foreach{i => awaitResult(dao.putOne(TestModel()))}

      awaitResult(dao.findAll()).length === 10
    } tag "findall"
  }
}

class TestDao extends BaseDynamoDao[TestModel]("Tests", Test.dbClient) {

}


sealed trait DynamoDAOSpecContext extends BeforeAfterWithApplication {

  val tableName = "Tests"

  lazy val dao = new TestDao()

  def before() {}

  //def createTable() = awaitResult(dao.createTable())

  def testDao() = {

  }
}

