package com.rcirka.play.dynamodb.results

import play.api.libs.json.Json

case class DescribeTableResult (
  Table: TableDescription
)

object DescribeTableResult {
  implicit val describeTableResultFormat = Json.reads[DescribeTableResult]
}