package com.rcirka.play.dynamodb

import java.net.URI

import com.amazonaws.auth.AWS4Signer
import com.amazonaws.util.AwsHostNameUtils
import com.amazonaws.{AmazonWebServiceRequest, DefaultRequest}
import com.rcirka.play.dynamodb.exception._
import com.rcirka.play.dynamodb.utils.JsonHelper._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws.{WS, WSResponse}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.http.Status._

import scala.util.Try

class DynamoDbWebService(
  val client: DynamoDBClient
) {
  val serviceName = "dynamodb" // Static string that identifies this service

  def post(target: String, postData: JsValue) : Future[WSResponse] = {

    val request = new DefaultRequest(new AmazonWebServiceRequest(){}, "AmazonDynamoDBv2")
    request.setEndpoint(client.endpoint)

    val signer = new AWS4Signer()
    signer.setServiceName(serviceName)
    signer.setRegionName(AwsHostNameUtils.parseRegionName(client.endpoint.getHost, serviceName))
    signer.sign(request, client.credentials)

    //val contentLength = postData.toString().getBytes(UTF8).length

    val headers = request.getHeaders ++ Map(
      "x-amz-target" -> target,
      "content-type" -> "application/x-amz-json-1.0"
    )

    //println(headers)

    println(s"--- $target ---")
    println(Json.prettyPrint(postData))

    WS.url(client.endpoint.toString).withHeaders(headers.toSeq: _*).post(postData).map { response =>
      println(response.json)

      response.status match {
        case OK => response
        case _ => {
          val exception = Try {
            // Try to get the exception type from the json response
            val errorResponse = response.json.as[JsObject]
            val errorClass = (errorResponse \ "__type").as[String].split('#')(1)
            val errorMessage = (errorResponse \ "Message").as[String]

            // Dynamically get the exception for the error class
            val constructor = Class.forName(s"com.rcirka.play.dynamodb.exception.$errorClass").getConstructors()(0)
            constructor.newInstance(errorMessage).asInstanceOf[Exception]
          }.getOrElse {
            // Otherwise throw a generic exception
            val errorMessage = (for {
              errorResponse <- response.json.asOpt[JsObject]
              errorMessage <- (errorResponse \ "Message").asOpt[String]
            } yield errorMessage).getOrElse("AWS returned unknown error")

            new UnknownException(errorMessage)
          }

          throw exception
        }
      }
    }
  }

  def getItem(json: JsObject) : Future[Option[JsValue]] = {
    val wrappedItem = wrapItem((json \ "Key").as[JsObject])
    val jsonTransformer = (__ \ 'Key).json.prune
    val newJson = json.transform(jsonTransformer).get ++ Json.obj("Key" -> wrappedItem).removeNulls

    post("DynamoDB_20120810.GetItem", newJson).map { response =>
      for {
        json <- response.json.asOpt[JsObject]
        item <- (json \ "Item").asOpt[JsObject]
      } yield unwrapItem(item)
    }
  }

  def deleteItem(json: JsObject) : Future[JsValue] = {
    val wrappedItem = wrapItem((json \ "Key").as[JsObject])
    val jsonTransformer = (__ \ 'Key).json.prune
    val newJson = json.transform(jsonTransformer).get ++ Json.obj("Key" -> wrappedItem).removeNulls

    post("DynamoDB_20120810.DeleteItem", newJson).map { _.json }
  }

  def putItem(json: JsObject) : Future[JsValue] = {
    val wrappedItem = wrapItem((json \ "Item").as[JsObject])
    val jsonTransformer = (__ \ 'Item).json.prune
    val newJson = json.transform(jsonTransformer).get ++ Json.obj("Item" -> wrappedItem).removeNulls
    post("DynamoDB_20120810.PutItem", newJson).map(_.json)
  }

  def scan(json: JsValue) : Future[Seq[JsObject]] = {
    post("DynamoDB_20120810.Scan", json).map { response =>
      println(Json.prettyPrint(response.json))

      val arr = (response.json.as[JsObject] \ "Items").as[Seq[JsObject]]
      arr.map(unwrapItem)
    }
  }

  def wrapItem(jsObject: JsObject) : JsObject = {
    jsObject.keys.foldLeft(Json.obj())((acc, key) => acc + (key, wrapItemObj((jsObject \ key))))
  }

  def wrapItemObj(jsValue: JsValue) : JsValue = {
    jsValue match {
      case x: JsNumber => Json.obj("N" -> x.toString)
      case x: JsBoolean => Json.obj("BOOL" -> x)
      case x: JsString => Json.obj("S" -> x)
      case x: JsArray => Json.obj("L" -> x.as[Seq[JsValue]].map(wrapItemObj))
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

object DynamoDbWebService {
  def apply(client: DynamoDBClient) = new DynamoDbWebService(client)
}


