package com.rcirka.play.dynamodb.requests

import play.api.libs.json.Json

case class ProvisionedThroughput (
  ReadCapacityUnits: Int = 5,
  WriteCapacityUnits: Int = 5
)

object ProvisionedThroughput {
  implicit val provisionedThroughputWrites = Json.writes[ProvisionedThroughput]
}