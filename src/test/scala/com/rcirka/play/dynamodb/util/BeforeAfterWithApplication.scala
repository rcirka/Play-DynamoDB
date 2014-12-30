package com.rcirka.play.dynamodb.util

import com.rcirka.play.dynamodb.dao.{GlobalDynamoDao, BaseDynamoDao}

import scala.concurrent._
import scala.concurrent.duration._
import org.specs2.execute.{AsResult, Result}
import play.api.test.{WithApplication, FakeApplication}

abstract class BeforeAfterWithApplication extends WithApplication(new FakeApplication()) {
  private def cleanDB() {
    val dao = new GlobalDynamoDao(Test.dbClient)

    // Delete all tables
    awaitResult(dao.listTables()).foreach(tableName => awaitResult(dao.deleteTable(tableName)))
  }
  def before()
  def after() {}

  override def around[T](t: => T)(implicit evidence$1: AsResult[T]): Result = {
    super.around {
      cleanDB()
      before()
      try { t } finally {
        after()
        //cleanDB()
      }
    }
  }

  def seedDb() {
    //new Seed().run
  }
}
