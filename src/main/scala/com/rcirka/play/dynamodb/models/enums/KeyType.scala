package com.rcirka.play.dynamodb.models.enums

import play.api.libs.json._

object KeyType extends Enumeration {
  type KeyType = Value

  val Hash = Value("HASH")
  val Range = Value("RANGE")

  implicit val keyTypeWrites = new Writes[KeyType] {
    def writes(keyType: KeyType): JsValue = JsString(keyType.toString)
  }
}



