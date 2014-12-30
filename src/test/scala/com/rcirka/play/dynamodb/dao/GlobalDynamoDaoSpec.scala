package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.exception.ResourceNotFoundException
import org.specs2.mutable._
import com.rcirka.play.dynamodb.util.{BeforeAfterWithApplication, Test}
import com.rcirka.play.dynamodb.util._

class GlobalDynamoDaoSpec extends Specification {
  "GlobalDynamoDaoSpec" should {

    "throw a ResourceNotFoundException for a non-existent table for describeTable()" in new GlobalDynamoDAOSpecContext {
      awaitResult(dao.describeTable("table1234")) must throwA[ResourceNotFoundException]
    } tag "describetableexception"


    "Describe table" in new GlobalDynamoDAOSpecContext {
      val tableName = "table1234"
      dao.createTable(tableName)

      val result = awaitResult(dao.describeTable(tableName))
      result.Table.TableName === Some(tableName)
    } tag "describetable"

    "Create table" in new GlobalDynamoDAOSpecContext {
      //createTable()

      //      val future2 = dao.doesTableExist()
      //      Await.result(future2, 10 seconds) must beTrue
    } tag "createtable"

    "Delete table" in new GlobalDynamoDAOSpecContext {
      //        createTable()
      //
      //        awaitResult(dao.deleteTable())

    } tag "deletetable"

    "List tables" in new GlobalDynamoDAOSpecContext {

      val response = awaitResult(dao.listTables())


    } tag "listtables"
  }

}

sealed trait GlobalDynamoDAOSpecContext extends BeforeAfterWithApplication {
  lazy val dao = new GlobalDynamoDao(Test.dbClient)

  def before() {}
}
