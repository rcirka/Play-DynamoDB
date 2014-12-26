package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.util.{Test, TestModel, BeforeAfterWithApplication}
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import scala.concurrent.Await
import scala.concurrent.duration._

class BaseDynamoDaoSpec extends Specification with NoTimeConversions {
  "BaseDynamoDaoSpec" should {

    "Create table" in new DynamoDAOContext {
      val future = doa.createTable()
      val response = Await.result(future, 10 seconds)

//      val future2 = doa.doesTableExist()
//      Await.result(future2, 10 seconds) must beTrue
    } tag "createtable"

    "Delete table" in new DynamoDAOContext {
      val future = doa.deleteTable()
      val response = Await.result(future, 10 seconds)

//      val future2 = doa.doesTableExist()
//      Await.result(future2, 10 seconds) must beTrue
    } tag "deletetable"

    "List tables" in new DynamoDAOContext {
      val future = doa.listTables()
      val response = Await.result(future, 10 seconds)

    } tag "listtables"
  }
}

sealed trait DynamoDAOContext extends BeforeAfterWithApplication {

  val tableName = "Tests"

  val doa = new BaseDynamoDao[TestModel](tableName, Test.dbClient) {}

  def before() {}
}