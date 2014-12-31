package com.rcirka.play.dynamodb.requests

import com.rcirka.play.dynamodb.models.KeyCondition
import com.rcirka.play.dynamodb.models.enums.QuerySelect.QuerySelect
import play.api.libs.json._

case class QueryRequest (
  tableName: String,
  indexName: Option[String] = None,
  keyConditions: Seq[KeyCondition],
  select: Option[QuerySelect] = None
)

object QueryRequest {

  implicit val queryRequestWrites = new Writes[QueryRequest] {
    def writes(request:QueryRequest) : JsValue = {
      Json.obj(
        "TableName" -> request.tableName,
        "IndexName" -> request.indexName,
        "KeyConditions" -> request.keyConditions.foldLeft(Json.obj()) { (acc, cur) =>
          acc ++ Json.toJson(cur).as[JsObject]
        },
        "Select" -> request.select
      )
    }
  }

}