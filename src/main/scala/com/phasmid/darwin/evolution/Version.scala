package com.phasmid.darwin.evolution

import com.phasmid.laScala.fp.FP
import com.phasmid.laScala.values.Incrementable

import scala.util.{Success, Try}

/**
  * Creating a new version of Version (eventually to replace the one in LaScala).
  *
  * Created by scalaprof on 7/11/17.
  */
case class Version[V: Incrementable](v: V, subversion: Option[Subversioned[V]]) extends BaseVersion(v) with Sequential[Version[V]] with Subversionable[Version[V]] {
  def next: Try[Version[V]] = for (x <- implicitly[Incrementable[V]].increment(v)) yield Version(x, None)

  def subversions: Stream[Version[V]] = {
    def s(v: V): Stream[V] = implicitly[Incrementable[V]].increment(v) match {
      case Success(w) => Stream.cons(v, s(w))
      case _ => Stream.cons(v, Stream.empty)
    }

    s(implicitly[Incrementable[V]].zero) map { z => Version(v, Some(Version(z, None))) }
  }
}

/**
  * Trait which defines a Stream of subversions based on this instance of Subversionable.
  *
  * @tparam T the underlying type
  */
trait Subversionable[T] {
  /**
    * Method to create a Stream of Ts based on this instance of Subversionable.
    *
    * @return a Stream of Ts.
    */
  def subversions: Stream[T]
}

/**
  * Trait which represents a version value (V) which can be sub-versioned.
  * Instances of this trait can be ordered so that they can be compared.
  *
  * @tparam V the underlying type of this Subversioned object.
  *           Typically, this will be a String or an integral value such as Long.
  */
trait Subversioned[V] extends (() => V) with Ordered[Subversioned[V]] {

  /**
    * Method to get the subversion of this Subversioned, if any.
    *
    * @return Some(vv) or None
    */
  def subversion: Option[Subversioned[V]]

  /**
    * Method to get this Subversioned and all its subversions, as a Stream of V values.
    *
    * @return a Stream of V
    */
  def asStream: Stream[V] = {
    Stream.cons(apply(),
      subversion match {
        case Some(vv) => vv.asStream
        case None => Stream.empty[V]
      })
  }
}

/**
  * Abstract class which extends Subversioned with Ordering.
  * It defines the following behavior:
  * (1) the apply method yields the value of v;
  * (2) the compare method yields the comparison of this and that.
  *
  * @param v the value of this Subversioned
  * @tparam V the underlying type of the Subversioned, which is defined to implement Ordering
  */
abstract class BaseVersion[V: Ordering](v: V) extends Subversioned[V] with Ordering[Subversioned[V]] {
  def apply(): V = v

  def compare(x: Subversioned[V], y: Subversioned[V]): Int = implicitly[Ordering[V]].compare(x(), y())

  protected val cf: (Subversioned[V], Subversioned[V]) => Int = compare

  def compare(that: Subversioned[V]): Int = FP.map2(this.subversion, that.subversion)(cf).getOrElse(if (subversion.isDefined) -1 else 1)
}
