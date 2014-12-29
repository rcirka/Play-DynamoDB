package com.rcirka.play.dynamodb.result

import play.api.libs.json.Json

case class DescribeTableResult (
  Table: TableDescription
)

object DescribeTableResult {
  implicit val describeTableResultFormat = Json.reads[DescribeTableResult]
}