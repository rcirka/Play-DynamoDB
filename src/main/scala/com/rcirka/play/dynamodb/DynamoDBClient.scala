package com.rcirka.play.dynamodb

import java.net.URI

import com.amazonaws.auth.profile.ProfilesConfigFile
import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import com.amazonaws.regions.RegionUtils
import play.api.Play
import scala.collection.JavaConversions._

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
    val conf = Play.current.configuration
    val error = (key: String) => throw new Exception(s"$key must be defined in application.conf!")

    // Endpoint
    // If the region is specified, get the endpoint for the region, otherwise use the endpoint value
    val regionEndpoint = conf.getString("dynamodb.region").map { region =>
      Option(RegionUtils.getRegion(region)).map(region => new URI(s"https://${region.getServiceEndpoint("dynamodb")}"))
        .getOrElse(throw new Exception("Invalid region defined in dynamodb.region"))
    }

    val endpoint = regionEndpoint.getOrElse {
      val endpointString = conf.getString("dynamodb.endpoint").getOrElse(error("dynamodb.endpoint"))
      val endpoint = new URI(endpointString)
      if (endpoint.getHost == null) throw new Exception(s"$endpointString is not a valid URI. Make sure the endpoint starts with http or https")
      endpoint
    }

    // Access Key
    val (accessKey, secretKey) = conf.getString("dynamodb.profile").map { profile =>
      val credentials = new ProfilesConfigFile().getCredentials(profile)
      (credentials.getAWSAccessKeyId, credentials.getAWSSecretKey)
    }.getOrElse {
      val accessKey = conf.getString("dynamodb.accesskey").getOrElse(error("dynamodb.accesskey"))
      val secretKey = conf.getString("dynamodb.secretkey").getOrElse(error("dynamodb.secretkey"))
      (accessKey, secretKey)
    }

    // Log requests
    val logRequest = conf.getBoolean("dynamodb.logrequests").getOrElse(false)


    DynamoDBClient(endpoint, accessKey, secretKey, logRequest)
  }

  def apply(endpoint: URI, accessKey: String, secretKey: String, logRequests: Boolean = false) : DynamoDBClient =
    new DynamoDBClient(endpoint, new BasicAWSCredentials(accessKey, secretKey), logRequests)
}
