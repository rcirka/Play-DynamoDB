package com.rcirka.play.dynamodb.models

import com.rcirka.play.dynamodb.models.enums.ComparisonOperator.ComparisonOperator
import play.api.libs.json._

case class KeyCondition (
  attributeName: String,
  attributeValueList: JsValue,
  comparisonOperator: ComparisonOperator
)

object KeyCondition {

  implicit val keyConditionWrites = new Writes[KeyCondition] {
    def writes(keyCondition: KeyCondition) : JsValue = {
      Json.obj(
        keyCondition.attributeName -> Json.obj(
          "AttributeValueList" -> Json.arr(keyCondition.attributeValueList),
          "ComparisonOperator" -> keyCondition.comparisonOperator
        )
      )
    }
  }

}