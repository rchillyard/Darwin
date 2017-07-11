package com.phasmid.darwin.evolution

import scala.util.Try

/**
  * This trait defines the concept of something which is part of a sequence, i.e. something which has a next instance.
  *
  * Created by scalaprof on 7/11/17.
  */
trait Sequential[T] {

  /**
    * Method to try to create a next instance.
    *
    * @return Try[T]
    */
  def next: Try[T]
}
