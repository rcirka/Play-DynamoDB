package com.rcirka.play.dynamodb.util

import play.api.libs.json.Json

case class TestModel (
  id: String
)

object TestModel {
  implicit val testModelFormat = Json.format[TestModel]
}
