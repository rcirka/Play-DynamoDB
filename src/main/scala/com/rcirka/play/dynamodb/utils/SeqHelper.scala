package com.rcirka.play.dynamodb.utils

object SeqUtils {

  implicit class SeqHelper[T](val seq: Seq[T]) {
    final def toOption: Option[Seq[T]] = if (seq.isEmpty) None else Some(seq)
  }

}
