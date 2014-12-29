package com.rcirka.play.dynamodb.result

import play.api.libs.json.Json

case class TableDescription (
  TableName: Option[String],
    TableStatus: Option[String]
)

object TableDescription {
  implicit val tableDescriptionFormat = Json.reads[TableDescription]
}
