package com.rcirka.play.dynamodb.util

import com.rcirka.play.dynamodb.dao.BaseDynamoDao
import com.rcirka.play.dynamodb.models.enums.{AttributeType, KeyType, ProjectionType}
import com.rcirka.play.dynamodb.models.indexes.{AttributeIndex, TableIndex}
import com.rcirka.play.dynamodb.models.{AttributeDefinition, Projection, ProvisionedThroughput}
import scala.concurrent.ExecutionContext.Implicits.global

class TestDao extends BaseDynamoDao[TestModel](
  client = Test.dbClient,
  tableName = "Tests",
  attributeDefinitions = Seq(
    AttributeDefinition("id", AttributeType.String),
    AttributeDefinition("mystring", AttributeType.String),
    AttributeDefinition("mynum", AttributeType.Numeric),
    AttributeDefinition("mynum2", AttributeType.Numeric),
    AttributeDefinition("mydate", AttributeType.Numeric)
  ),
  keySchema = Seq(AttributeIndex("id", KeyType.Hash), AttributeIndex("mydate", KeyType.Range)),
  localSecondaryIndexes = Seq(
    TableIndex(
      indexName = "mydate_local_index",
      keySchema = Seq(
        AttributeIndex("id", KeyType.Hash),
        AttributeIndex("mynum2", KeyType.Range)
      ),
      projection = Some(Projection(ProjectionType.All))
    )
  ),
  globalSecondaryIndexes = Seq(
    TableIndex(
      indexName = "mystring_index",
      keySchema = Seq(
        AttributeIndex("mystring", KeyType.Hash),
        AttributeIndex("mynum", KeyType.Range)
      ),
      provisionedThroughput = Some(ProvisionedThroughput()),
      projection = Some(Projection(ProjectionType.All))
    )
  ),
  blockOnTableCreation = true
)
