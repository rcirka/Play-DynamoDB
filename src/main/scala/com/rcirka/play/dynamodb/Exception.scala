package com.rcirka.play.dynamodb.exception

case class UnknownException(message: String) extends Exception(message)

// DynamoDb Exceptions
case class ConditionalCheckFailedException(message: String) extends Exception(message)
case class InternalServerErrorException(message: String) extends Exception(message)
case class ItemCollectionSizeLimitExceededException(message: String) extends Exception(message)
case class LimitExceededException(message: String) extends Exception(message)
case class ProvisionedThroughputExceededException(message: String) extends Exception(message)
case class ResourceInUseException(message: String) extends Exception(message)
case class ResourceNotFoundException(message: String) extends Exception(message)

// General Exceptions
case class IncompleteSignature(message: String) extends Exception(message)
case class InternalFailure(message: String) extends Exception(message)
case class InvalidAction(message: String) extends Exception(message)
case class InvalidClientTokenId(message: String) extends Exception(message)
case class InvalidParameterCombination(message: String) extends Exception(message)
case class InvalidParameterValue(message: String) extends Exception(message)
case class InvalidQueryParameter(message: String) extends Exception(message)
case class InvalidSignatureException(message: String) extends Exception(message)
case class MalformedQueryString(message: String) extends Exception(message)
case class MissingAction(message: String) extends Exception(message)
case class MissingAuthenticationToken(message: String) extends Exception(message)
case class MissingParameter(message: String) extends Exception(message)
case class OptInRequired(message: String) extends Exception(message)
case class RequestExpired(message: String) extends Exception(message)
case class ServiceUnavailable(message: String) extends Exception(message)
case class Throttling(message: String) extends Exception(message)
case class ValidationException(message: String) extends Exception(message)