package com.rcirka.play.dynamodb.models

import com.rcirka.play.dynamodb.models.enums.ProjectionType.ProjectionType
import play.api.libs.json.Json

case class Projection (
  projectionType: ProjectionType,
  NonKeyAttributes: Option[Seq[String]] = None
)

object Projection {
  implicit val projectionWrites = Json.writes[Projection]
}
