package com.rcirka.play.dynamodb

import java.net.URI

import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import play.api.Play

class DynamoDBClient(
  val endpoint: URI,
  val credentials: AWSCredentials,
  val logRequests: Boolean = false
) {
  play.api.Logger.info(s"Initializing DynamoDBClient with endpoint $endpoint")
}

object DynamoDBClient {
  /**
   * Initialize client using application.conf values
   */
  lazy val db = {
    val error = (endpoint:String) => throw new Exception(s"$endpoint must be defined in application.conf!")
    val conf = Play.current.configuration

    val endpoint = conf.getString("dynamodb.endpoint").getOrElse(error("dynamodb.endpoint"))
    val accessKey = conf.getString("dynamodb.accesskey").getOrElse(error("dynamodb.accesskey"))
    val secretKey = conf.getString("dynamodb.secretkey").getOrElse(error("dynamodb.secretkey"))
    val logRequest = conf.getBoolean("dynamodb.logrequests").getOrElse(false)

    DynamoDBClient(endpoint, accessKey, secretKey, logRequest)
  }

  def apply(endpoint: String, accessKey: String, secretKey: String, logRequests: Boolean = false) : DynamoDBClient =
    new DynamoDBClient(new URI(endpoint), new BasicAWSCredentials(accessKey, secretKey), logRequests)
}
