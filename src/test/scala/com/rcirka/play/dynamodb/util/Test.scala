package com.rcirka.play.dynamodb.util

import java.net.URI

import com.amazonaws.auth.BasicAWSCredentials
import com.rcirka.play.dynamodb.DynamoDBClient

object Test {
  val dbClient = DynamoDBClient("http://localhost:8000", "testaccesskey1", "testsecretkey1", true)
}
