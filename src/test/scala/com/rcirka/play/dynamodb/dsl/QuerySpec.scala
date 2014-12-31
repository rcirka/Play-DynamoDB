package com.rcirka.play.dynamodb.dsl

import com.rcirka.play.dynamodb.util.{TestDao, BeforeAfterWithApplication}
import org.joda.time.DateTime
import org.specs2.mutable._
import com.rcirka.play.dynamodb.util._
import com.rcirka.play.dynamodb.Dsl._

class QuerySpec extends Specification {
  "Query" should {

    "query primary key with range with result" in new DslSpecContext {
      val model = TestModel(mydate = DateTime.now)
      awaitResult(dao.put(model))

      val result = awaitResult(dao.query(model.id, "mydate" $lt DateTime.now))
      model mustEqual result(0)
    }

    "query primary key with range with no result" in new DslSpecContext {
      val model = TestModel(mydate = DateTime.now)
      awaitResult(dao.put(model))

      val result = awaitResult(dao.query(model.id, "mydate" $gt DateTime.now))
      result.length === 0
    }

    "query by index" in new DslSpecContext {
      val model = TestModel(mystring = "teststring")
      awaitResult(dao.put(model))

      val result = awaitResult(dao.queryByIndex("mystring_index", "mystring" $eq "teststring"))
      model mustEqual result(0)
    }

    "query using greater than" in new DslSpecContext {
      val model1 = TestModel(mystring = "teststring", mynum = 5)
      awaitResult(dao.put(model1))

      val model2 = TestModel(mystring = "teststring", mynum = 6)
      awaitResult(dao.put(model2))

      val result = awaitResult(dao.queryByIndex("mystring_index", "mystring" $eq "teststring", "mynum" $gt 5))
      result.length mustEqual 1
      model2 mustEqual result(0)
    } tag "gt"

  }
}

sealed trait DslSpecContext extends BeforeAfterWithApplication {
  lazy val dao = new TestDao()

  def before() {}
}
