package com.phasmid.darwin.evolution

import com.phasmid.laScala.values.Incrementable

import scala.util._

/**
  * This trait defines the concept of something which is part of a sequence, i.e. something which has a next instance.
  *
  * Created by scalaprof on 7/11/17.
  */
trait Sequential[T] {

  /**
    * Method to try to create a next instance.
    *
    * @param isSnapshot (defaults to false) If true, then the next Sequential will be a snapshot.
    *                   The expected behavior is that when isSnapshot changes from false to true, it will be
    *                   accompanied by a change in sequence (where the new Sequential is ordered after this),
    *                   otherwise not.
    * @return Try[T]
    */
  def next(isSnapshot: Boolean = false): Try[T]
}

case class IncrementableSequential[T: Incrementable](x: T, isSnapshot: Boolean = false) extends Sequential[IncrementableSequential[T]] with Ordering[IncrementableSequential[T]] {
  def next(isSnapshot: Boolean = false): Try[IncrementableSequential[T]] = if (this.isSnapshot)
    if (isSnapshot) Success(this)
    else Success(IncrementableSequential(x))
  else for (z <- implicitly[Incrementable[T]].increment(x)) yield IncrementableSequential(z)

  def compare(x: IncrementableSequential[T], y: IncrementableSequential[T]): Int = implicitly[Incrementable[T]].compare(x.x, y.x)
}