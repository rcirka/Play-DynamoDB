package com.rcirka.play.dynamodb

import com.rcirka.play.dynamodb.models.KeyCondition
import com.rcirka.play.dynamodb.models.enums.ComparisonOperator
import play.api.libs.json._
import com.rcirka.play.dynamodb.utils.JsonHelper._

object Dsl {
  implicit class StringWithKeyCondition(str: String) {
    def $eq[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.Equals)
    def $ne[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.NotEquals)
    def $le[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.LessThanOrEqual)
    def $lt[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.LessThan)
    def $ge[A: Writes](value: A) = KeyCondition(str, wrapItemVal(Json.toJson(value)), ComparisonOperator.GreaterThanOrEqual)
    //def $notNull[A: Writes] = KeyCondition(str, wrapItemVal(Json.toJson(true)), ComparisonOperator.GreaterThanOrEqual)
    //def $null[A: Writes] = KeyCondition(str, wrapItemVal(Json.toJson(true)), ComparisonOperator.GreaterThanOrEqual)
  }
}
