package com.rcirka.play.dynamodb

import com.rcirka.play.dynamodb.models.KeyCondition
import com.rcirka.play.dynamodb.models.enums.ComparisonOperator
import play.api.libs.json._
import com.rcirka.play.dynamodb.utils.JsonHelper._

object Dsl {
  implicit class StringWithKeyCondition(str: String) {
    def $eq[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.Equals)
  }
}
