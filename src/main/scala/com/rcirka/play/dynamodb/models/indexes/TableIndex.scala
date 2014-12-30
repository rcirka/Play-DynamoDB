package com.rcirka.play.dynamodb.models.indexes

import com.rcirka.play.dynamodb.models.{ProvisionedThroughput, Projection}
import play.api.libs.json.Json

case class TableIndex (
  indexName: String,
  keySchema: Seq[AttributeIndex],
  projection: Option[Projection] = None,
  provisionedThroughput: Option[ProvisionedThroughput] = None
)

object TableIndex {
  implicit val tableIndexWrites = Json.writes[TableIndex]
}
