package com.rcirka.play.dynamodb.models.enums

object ProjectionType extends Enumeration {
  type ProjectionType = Value

  val KeysOnly = Value("KEYS_ONLY")
  val Include = Value("INCLUDE")
  val All = Value("ALL")
}