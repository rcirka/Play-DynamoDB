package com.rcirka.play.dynamodb.models.indexes

import play.api.libs.json.Json

case class LocalSecondaryIndex (
  indexName: String,
  keySchema: Seq[AttributeIndex]
)

object LocalSecondaryIndex {
  implicit val localSecondaryIndexWrites = Json.writes[LocalSecondaryIndex]
}
