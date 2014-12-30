package com.rcirka.play.dynamodb.requests

import com.rcirka.play.dynamodb.models.indexes.{LocalSecondaryIndex, AttributeIndex}
import com.rcirka.play.dynamodb.utils.AttributeDefinition
import play.api.libs.json.{JsArray, JsValue, Json}
import com.rcirka.play.dynamodb.utils.OWritesOps._


case class CreateTableRequest(
  tableName: String,
  keySchema: Seq[AttributeIndex],
  attributeDefinitions: Seq[AttributeDefinition],
  localSecondaryIndexes: Option[Seq[LocalSecondaryIndex]] = None,
  provisionedThroughput: ProvisionedThroughput = ProvisionedThroughput()
)

object CreateTableRequest {
  implicit val createTableRequestWrites = Json.writes[CreateTableRequest].toUpper
}
