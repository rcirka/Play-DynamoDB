package com.rcirka.play.dynamodb.utils

import play.api.libs.json._

object JsonHelper {
  implicit class JsonWithHelper(json: JsObject) {
    def removeNulls =
      json.keys.foldLeft(json)((acc, key) => if (acc \ key == JsNull) acc.transform((__ \ key).json.prune).get else acc)
  }

  implicit class MapWithHelper[T](map: Map[String, T]) {
    def removeNulls =
      map.keys.foldLeft(map)((acc, key) => acc.get(key).map(x => acc).getOrElse(acc - key))
  }
}
