package com.rcirka.play.dynamodb.utils

import com.rcirka.play.dynamodb.utils.AttributeType.AttributeType
import play.api.libs.json.Json

case class AttributeDefinition (
  attributeName: String,
  attributeType: AttributeType
)

object AttributeDefinition {
  implicit val attributeDefinitionWrites = Json.writes[AttributeDefinition]
}
