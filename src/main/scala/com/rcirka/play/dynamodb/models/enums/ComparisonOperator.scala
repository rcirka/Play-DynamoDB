package com.rcirka.play.dynamodb.models.enums

import play.api.libs.json._

object ComparisonOperator extends Enumeration {
  type ComparisonOperator = Value

  val Equals = Value("EQ")
  val NotEquals = Value("NE")
  val LessThanOrEqual = Value("LE")
  val LessThan = Value("LT")
  val GreaterThanOrEqual = Value("GE")
  val GreaterThan = Value("GT")
  val NotNull = Value("NOT_NULL")
  val Null = Value("NULL")
  val Contains = Value("CONTAINS")
  val DoesNotContain = Value("NOT_CONTAINS")
  val BeginsWith = Value("BEGINS_WITH")
  val In = Value("IN")
  val Between = Value("BETWEEN")

  implicit val comparisonOperatorWrites = new Writes[ComparisonOperator] {
    def writes(value: ComparisonOperator): JsValue = JsString(value.toString)
  }
}
