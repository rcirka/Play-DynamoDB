package com.rcirka.play.dynamodb.dsl

import com.rcirka.play.dynamodb.util.{TestDao, BeforeAfterWithApplication}
import org.specs2.mutable._
import com.rcirka.play.dynamodb.util._
import com.rcirka.play.dynamodb.Dsl._

class DslSpec extends Specification {
  "DslSpec" should {

    "satisfy $eq" in new DslSpecContext {
      val model = TestModel(mystring = "teststring")
      awaitResult(dao.put(model))

      val result = awaitResult(dao.queryByIndex("mystring_index", "mystring" $eq "teststring"))
      model mustEqual result(0)
    }

    "satisfy $gt" in new DslSpecContext {
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