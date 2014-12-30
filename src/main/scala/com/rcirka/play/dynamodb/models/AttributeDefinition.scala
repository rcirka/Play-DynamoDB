package com.rcirka.play.dynamodb.models

import com.rcirka.play.dynamodb.models.enums.AttributeType.AttributeType
import play.api.libs.json.Json

case class AttributeDefinition (
  attributeName: String,
  attributeType: AttributeType
)

object AttributeDefinition {
  implicit val attributeDefinitionWrites = Json.writes[AttributeDefinition]
}
