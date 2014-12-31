package com.rcirka.play.dynamodb.models.enums

import com.rcirka.play.dynamodb.models.enums.ProjectionType._
import play.api.libs.json.{JsString, JsValue, Writes}

object QuerySelect extends Enumeration {
  type QuerySelect = Value

  val AllAttributes = Value("ALL_ATTRIBUTES")
  val AllProjectedAttributes = Value("ALL_PROJECTED_ATTRIBUTES")
  val Count = Value("COUNT")
  val SpecificAttributes = Value("SPECIFIC_ATTRIBUTES")

  implicit val projectionTypeWrites = new Writes[QuerySelect] {
    def writes(value: QuerySelect): JsValue = JsString(value.toString)
  }
}
