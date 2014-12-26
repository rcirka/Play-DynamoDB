package com.rcirka.play.dynamodb.util

import com.rcirka.play.dynamodb.dao.BaseDynamoDao

import scala.concurrent._
import scala.concurrent.duration._
import org.specs2.execute.{AsResult, Result}
import play.api.test.{WithApplication, FakeApplication}

abstract class BeforeAfterWithApplication extends WithApplication(new FakeApplication()) {
  private def cleanDB() {
//    Seq(UserDAO, EventDAO, MessageDAO).foreach { dao =>
//      // Drop collections and ignore missing collection errors
//      val future = dao.drop().recover { case _ => false }
//      Await.result(future, 30 seconds)
//
//      // Recreate indexes
//      val future2 = dao.ensureIndexes()
//      Await.result(future2, 30 seconds)
//    }

//    val doa = new BaseDynamoDao[TestModel]("", Test.dbClient) {}
//
//    val future = doa.listTables()
//    val tableNames = Await.result(future, 10 seconds)
//
//    tableNames.foreach{tableName =>
//      val future = doa.deleteTable(tableName)
//      Await.result(future, 10 seconds)
//    }


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
