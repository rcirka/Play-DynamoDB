package com.rcirka.play.dynamodb.util

import com.rcirka.play.dynamodb.dao.BaseDynamoDao
import com.rcirka.play.dynamodb.models.enums.{AttributeType, KeyType, ProjectionType}
import com.rcirka.play.dynamodb.models.indexes.{AttributeIndex, TableIndex}
import com.rcirka.play.dynamodb.models.{AttributeDefinition, Projection, ProvisionedThroughput}
import scala.concurrent.ExecutionContext.Implicits.global

class TestDao extends BaseDynamoDao[TestModel](
  client = Test.dbClient,
  tableName = "Tests",
  keySchema = Seq(AttributeIndex("id", KeyType.Hash)),
  attributeDefinitions = Seq(
    AttributeDefinition("id", AttributeType.String),
    AttributeDefinition("mystring", AttributeType.String)
  ),
  globalSecondaryIndexes = Seq(
    TableIndex(
      "mystring_index",
      Seq(
        AttributeIndex("mystring", KeyType.Hash)
      ),
      provisionedThroughput = Some(ProvisionedThroughput()),
      projection = Some(Projection(ProjectionType.All))
    )
  ),
  blockOnTableCreation = true
)
