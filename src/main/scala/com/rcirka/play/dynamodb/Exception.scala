package com.rcirka.play.dynamodb.exception

case class UnknownException(message: String) extends Exception(message)

case class ConditionalCheckFailedException(message: String) extends Exception(message)
case class InternalServerErrorException(message: String) extends Exception(message)
case class ItemCollectionSizeLimitExceededException(message: String) extends Exception(message)
case class LimitExceededException(message: String) extends Exception(message)
case class ProvisionedThroughputExceededException(message: String) extends Exception(message)
case class ResourceInUseException(message: String) extends Exception(message)
case class ResourceNotFoundException(message: String) extends Exception(message)
case class InternalFailure(message: String) extends Exception(message)