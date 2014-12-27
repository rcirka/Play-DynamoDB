package com.rcirka.play.dynamodb

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

package object util {
  def awaitResult[T](future: Future[T]) : T = Await.result(future, 10 seconds)
}
