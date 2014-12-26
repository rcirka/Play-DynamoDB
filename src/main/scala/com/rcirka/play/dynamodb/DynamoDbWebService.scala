package com.rcirka.play.dynamodb

import java.net.URI

import com.amazonaws.auth.{AWS4Signer, AWSCredentialsProvider}
import com.amazonaws.util.AwsHostNameUtils
import com.amazonaws.util.StringUtils._
import com.amazonaws.{AmazonWebServiceClient, AmazonWebServiceRequest, DefaultRequest}
import com.rcirka.play.dynamodb.utils.DynamoDbException
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws.{WS, WSResponse}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.runtime.{universe => ru}
import play.api.http.Status._

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

    println(Json.prettyPrint(postData))

    WS.url(client.endpoint.toString).withHeaders(headers.toSeq: _*).post(postData).map { response =>
      println(response.json)

      if (response.status == OK) {
        response
      }
      else {
        val errorMessage = (for {
          errorResponse <- response.json.asOpt[JsObject]
          errorType <- (errorResponse \ "__type").asOpt[String]
          errorMessage <- (errorResponse \ "Message").asOpt[String]
          message <- Some(s"$errorType. $errorMessage")
        } yield message).getOrElse("Unknown error")

        throw DynamoDbException(errorMessage)
      }
    }

  }

  def putItem2(json: JsValue) : Future[JsValue] = {
    post("DynamoDB_20120810.PutItem", json).map(_.json)
  }

  def scan(json: JsValue) : Future[Seq[JsObject]] = {
    post("DynamoDB_20120810.Scan", json).map { response =>
      println(Json.prettyPrint(response.json))

      val arr = (response.json.as[JsObject] \ "Items").as[Seq[JsObject]]
      arr.map(unwrapItem)
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


