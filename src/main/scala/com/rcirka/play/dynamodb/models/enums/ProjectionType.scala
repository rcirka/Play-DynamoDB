package com.rcirka.play.dynamodb.models.enums

import play.api.libs.json._

object ProjectionType extends Enumeration {
  type ProjectionType = Value

  val KeysOnly = Value("KEYS_ONLY")
  val Include = Value("INCLUDE")
  val All = Value("ALL")

  implicit val projectionTypeWrites = new Writes[ProjectionType] {
    def writes(value: ProjectionType): JsValue = JsString(value.toString)
  }
}