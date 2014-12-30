package com.rcirka.play.dynamodb.utils

import play.api.libs.json._

object AttributeType extends Enumeration {
  type AttributeType = Value

  val Numeric = Value("N")
  val String = Value("S")

  implicit val attributeTypeWrites = new Writes[AttributeType] {
    def writes(value: AttributeType): JsValue = JsString(value.toString)
  }
}
