package com.rcirka.play.dynamodb

import com.rcirka.play.dynamodb.models.KeyCondition
import com.rcirka.play.dynamodb.models.enums.ComparisonOperator
import play.api.libs.json._
import com.rcirka.play.dynamodb.utils.JsonHelper._

object Dsl {
  implicit class StringWithKeyCondition(str: String) {
    def $eq[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.Equals)
    def $ne[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.NotEquals)
    def $le[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.LessThanOrEqual)
    def $lt[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.LessThan)
    def $ge[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.GreaterThanOrEqual)
    def $gt[A: Writes](value: A) = KeyCondition(str, wrap(value), ComparisonOperator.GreaterThan)
    // TODO: Implement remaining conditions
    //def $notNull[A: Writes] = KeyCondition(str, wrapItemVal(Json.toJson(true)), ComparisonOperator.GreaterThanOrEqual)
    //def $null[A: Writes] = KeyCondition(str, wrapItemVal(Json.toJson(true)), ComparisonOperator.GreaterThanOrEqual)

    private def wrap[A: Writes](value: A) : JsValue = Json.arr(wrapItemVal(Json.toJson(value)))
    private def wrapArr[A: Writes](values: Seq[A]) : JsValue = Json.toJson(values.map(x => wrapItemVal(Json.toJson(x))))

  }
}
