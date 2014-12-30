package com.rcirka.play.dynamodb.requests

import com.rcirka.play.dynamodb.models.{ProvisionedThroughput, AttributeDefinition}
import com.rcirka.play.dynamodb.models.indexes.{TableIndex, AttributeIndex}
import play.api.libs.json._
import com.rcirka.play.dynamodb.utils.OWritesOps._


case class CreateTableRequest(
  tableName: String,
  keySchema: Seq[AttributeIndex],
  attributeDefinitions: Seq[AttributeDefinition],
  globalSecondaryIndexes: Option[Seq[TableIndex]] = None,
  localSecondaryIndexes: Option[Seq[TableIndex]] = None,
  provisionedThroughput: ProvisionedThroughput = ProvisionedThroughput()
)

object CreateTableRequest {
  implicit val createTableRequestWrites = Json.writes[CreateTableRequest].toUpper
}
