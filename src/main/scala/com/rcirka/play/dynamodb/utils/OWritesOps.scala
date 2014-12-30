package com.rcirka.play.dynamodb.utils

import play.api.libs.functional.syntax._
import play.api.libs.json._

// http://kailuowang.blogspot.com/2013/11/addremove-fields-to-plays-default-case.html
class OWritesOps[A](writes: OWrites[A]) {
  def addField[T: Writes](fieldName: String, field: A => T): OWrites[A] =
    (writes ~ (__ \ fieldName).write[T])((a: A) => (a, field(a)))

  def removeField(fieldName: String): OWrites[A] = OWrites { a: A =>
    val transformer = (__ \ fieldName).json.prune
    Json.toJson(a)(writes).validate(transformer).get
  }

  /**
   * Capitalize the first letter of each key
   * @return
   */
  def toUpper() : OWrites[A] = OWrites { a: A => upper(Json.toJson(a)(writes).as[JsObject]) }

  private def upper(js: JsObject) : JsObject = {
    js.keys.foldLeft(Json.obj()) { (acc, key) => acc + (key.capitalize, upperVal(js \ key)) }
  }

  private def upperVal(value: JsValue) : JsValue = {
    value match {
      case obj: JsObject => upper(obj)
      case arr: JsArray => JsArray(arr.as[Seq[JsValue]].map(upperVal))
      case _ => value
    }
  }
}

object OWritesOps {
  implicit def from[A](writes: OWrites[A]): OWritesOps[A] = new OWritesOps(writes)
}