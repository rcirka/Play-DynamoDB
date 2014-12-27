package com.rcirka.play.dynamodb.util

import play.api.libs.json.Json

case class TestModel (
  id: String = "1234",
  optmystring: Option[String] = None,
  mynum: Int = 5678,
  optmynum: Option[Int] = None,
  mybool: Boolean = true,
  optmybool: Option[Boolean] = None,
  myobj: TestModel2 = TestModel2(),
  myarrObj: Seq[TestModel2] = Seq(TestModel2()),
  myarrNum: Seq[Int] = Seq(1, 2, 4, 5)
)

object TestModel {
  implicit val testModelFormat = Json.format[TestModel]
}
