package com.rcirka.play.dynamodb.utils

case class DynamoDbException(message: String) extends Exception(message)
