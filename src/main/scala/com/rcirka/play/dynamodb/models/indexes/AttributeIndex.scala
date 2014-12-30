package com.rcirka.play.dynamodb.models.indexes

import com.rcirka.play.dynamodb.models.enums.KeyType
import com.rcirka.play.dynamodb.models.enums.KeyType
import KeyType.KeyType
import com.rcirka.play.dynamodb.models.enums.KeyType
import play.api.libs.json.Json

case class AttributeIndex (
  val attributeName: String,
  val keyType: KeyType
)

object AttributeIndex {
  implicit val attributeIndexWrites = Json.writes[AttributeIndex]
}
