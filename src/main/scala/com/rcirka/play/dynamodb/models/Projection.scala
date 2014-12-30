package com.rcirka.play.dynamodb.models

import com.rcirka.play.dynamodb.models.enums.ProjectionType.ProjectionType

case class Projection (
  projectionType: ProjectionType,
  NonKeyAttributes: Seq[String] = Nil
)
