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

  def wrapItem(jsObject: JsObject) : JsObject = {
    jsObject.keys.foldLeft(Json.obj())((acc, key) => acc + (key, wrapItemVal((jsObject \ key))))
  }

  def wrapItemVal(jsValue: JsValue) : JsValue = {
    jsValue match {
      case x: JsNumber => Json.obj("N" -> x.toString)
      case x: JsBoolean => Json.obj("BOOL" -> x)
      case x: JsString => Json.obj("S" -> x)
      case x: JsArray => Json.obj("L" -> x.as[Seq[JsValue]].map(wrapItemVal))
      case x: JsObject => Json.obj("M" -> wrapItem(x))
      case _ => JsNull
    }
  }

  def unwrapItem(js: JsObject) : JsObject = {
    js.keys.foldLeft(Json.obj())((acc, key) => acc + (key, unwrapItemObj((js \ key).as[JsObject])))
  }

  // TODO: Add more cases and validation
  def unwrapItemObj(obj: JsObject) : JsValue = {
    val jsObjKey = obj.keys.head
    jsObjKey match {
      case "S" | "BOOL"  => obj \ jsObjKey
      case "N" => JsNumber((obj \ jsObjKey).as[String].toLong)
      case "M" => unwrapItem((obj \ jsObjKey).as[JsObject])
      case "L" => JsArray((obj \ jsObjKey).as[Seq[JsObject]].map(unwrapItemObj))
      case _ => JsNull
    }
  }
}
