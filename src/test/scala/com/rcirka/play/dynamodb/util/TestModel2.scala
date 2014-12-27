package com.rcirka.play.dynamodb.util

import play.api.libs.json.Json

case class TestModel2 (
  id: String = "3333",
  mynum: Int = 2222,
  mybool: Boolean = true
)

object TestModel2 {
  implicit val testModel2Format = Json.format[TestModel2]
}
