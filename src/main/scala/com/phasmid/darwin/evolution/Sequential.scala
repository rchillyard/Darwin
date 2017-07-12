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

/**
  * This is a simple implementation of the Sequential trait which does not allow for sub-versions.
  *
  * @param t          the value of this IncrementalSequential
  * @param isSnapshot (defaults to false) true if this is a snapshot
  * @param by         the unit of the increment
  * @tparam T the underlying type of this Sequential: it is defined that there exist an implicit value of Incrementable[T] available --
  *           in practice, this typically means an integer of some sort, as String or a LocalDate.
  */
case class IncrementableSequential[T: Incrementable](t: T, isSnapshot: Boolean = false, by: String = "") extends Sequential[IncrementableSequential[T]] with Ordering[IncrementableSequential[T]] {
  /**
    *
    * @param isSnapshot (defaults to false) If true, then the next Sequential will be a snapshot.
    *                   The expected behavior is that when isSnapshot changes from false to true, it will be
    *                   accompanied by a change in sequence (where the new Sequential is ordered after this),
    *                   otherwise not.
    * @return Try[T]
    */
  def next(isSnapshot: Boolean = false): Try[IncrementableSequential[T]] =
    if (this.isSnapshot)
      if (isSnapshot) Success(this)
      else Success(IncrementableSequential(t, isSnapshot = false, by))
    else for (_t <- implicitly[Incrementable[T]].increment(t, 1, by)) yield IncrementableSequential(_t, isSnapshot, by)

  /**
    * Method to compare two IncrementalSequential instances
    *
    * @param i1 the first
    * @param i2 the second
    * @return the comparison of their underlying values
    */
  def compare(i1: IncrementableSequential[T], i2: IncrementableSequential[T]): Int = implicitly[Incrementable[T]].compare(i1.t, i2.t)

  //  def compare(that: Sequential[IncrementableSequential[T]]): Int = that match {
  //    case i: IncrementableSequential[T] => compare(this, i)
  //    case _ => throw EvolutionException(s"cannot compare $that with $this")
  //  }
  //
}