package com.rcirka.play.dynamodb.util

import org.joda.time.DateTime
import play.api.libs.json.Json

case class TestModel (
  id: String = newKey(),
  mystring: String = "stringval",
  optmystring: Option[String] = None,
  mynum: Int = 5678,
  mynum2: Int = 56781,
  optmynum: Option[Int] = None,
  mybool: Boolean = true,
  optmybool: Option[Boolean] = None,
  myobj: TestModel2 = TestModel2(),
  myarrObj: Seq[TestModel2] = Seq(TestModel2()),
  myarrNum: Seq[Int] = Seq(1, 2, 4, 5),
  mydate: DateTime = DateTime.now
)

object TestModel {
  implicit val testModelFormat = Json.format[TestModel]
}
