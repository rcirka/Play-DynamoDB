package com.rcirka.play.dynamodb.dao

import com.rcirka.play.dynamodb.exception._
import com.rcirka.play.dynamodb.models.{Projection, ProvisionedThroughput, AttributeDefinition}
import com.rcirka.play.dynamodb.models.enums.{ProjectionType, AttributeType, KeyType}
import com.rcirka.play.dynamodb.models.indexes.{TableIndex, AttributeIndex}
import com.rcirka.play.dynamodb.util.{BeforeAfterWithApplication, Test, TestModel}
import com.rcirka.play.dynamodb.util._
import org.specs2.mutable._
import play.api.libs.json._
import com.rcirka.play.dynamodb.Dsl._

class BaseDynamoDaoSpec extends Specification {
  "BaseDynamoDaoSpec" should {
    "Insert and get item" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.put(model))

      val result = awaitResult(dao.get("1"))

      Some(model) === result
    } tag "insertitem"

    "Delete Item" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.put(model))

      awaitResult(dao.deleteOne("1"))

      awaitResult(dao.get("1")) === None
    } tag "deleteitem"

    "return None for GetItem if no result found" in new DynamoDAOSpecContext {
      awaitResult(dao.get("1")) === None
    } tag "empty result"

    "Get all Items" in new DynamoDAOSpecContext {
      (1 to 10).foreach { i => awaitResult(dao.put(TestModel()))}

      awaitResult(dao.getAll()).length === 10
    } tag "findall"

    "Query by global index" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.put(model))

      awaitResult(dao.queryByIndex("myindex", "mystring" $eq "stringval")).length === 1
    } tag "query"

    "Exists global index" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.put(model))

      awaitResult(dao.exists("myindex", "mystring" $eq "stringval")) === true
    } tag "existsglobalindex"

    "Count global index" in new DynamoDAOSpecContext {
      val model = TestModel("1")
      awaitResult(dao.put(model))

      awaitResult(dao.count("myindex", "mystring" $eq "stringval")) === 1
    } tag "countglobalindex"
  }
}

class TestDao extends BaseDynamoDao[TestModel](
  client = Test.dbClient,
  tableName = "Tests",
  keySchema = Seq(AttributeIndex("id", KeyType.Hash)),
  attributeDefinitions = Seq(AttributeDefinition("id", AttributeType.String), AttributeDefinition("mystring", AttributeType.String)),
  globalSecondaryIndexes = Seq(
    TableIndex(
      "myindex",
      Seq(
        AttributeIndex("mystring", KeyType.Hash)
      ),
      provisionedThroughput = Some(ProvisionedThroughput()),
      projection = Some(Projection(ProjectionType.All))
    )
  )
)


sealed trait DynamoDAOSpecContext extends BeforeAfterWithApplication {

  val tableName = "Tests"

  lazy val dao = new TestDao()

  def before() {}

  //def createTable() = awaitResult(dao.createTable())

  def testDao() = {

  }
}

