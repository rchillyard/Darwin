/*
 * DARWIN Genetic Algorithms Framework Project.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 *
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
 *
 *      This file is part of Darwin.
 *
 *      Darwin is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.phasmid.darwin.evolution

import com.phasmid.darwin.genetics.dna.Base

import scala.annotation.tailrec

/**
  * Created by scalaprof on 7/19/17.
  */
trait Random[X] extends (() => X) {
  def next: Random[X]

  def toStream: Stream[X]
}

//trait RandomStream[X, Repr] extends Random[X] with Randoms[X, Repr]
//
//abstract class BaseRandomStream[X, Repr](private var random: Random[X]) {
//  // TODO need to update the random value
//
//  def take: X = take(1).head
//
//  def take(n: Int): Seq[X] = {
//    val (r: Random[X], xs: Seq[X]) = random.take(n)
//    random = r.asInstanceOf[Random[X] with Randoms[RNG[X]]] // CHECK
//    xs
//  }
//
//}
//
//
//case class ConcreteRandomStream[X : Randomizable](private var random: RNG[X]) extends BaseRandomStream[X, RNG[X]](random){
//  // TODO need to update the random value
//
//  def take: X = take(1).head
//
//  def take(n: Int): Seq[X] = {
//    val (r: Random[X], xs: Seq[X]) = random.take(n)
//    random = r.asInstanceOf[Random[X] with Randoms[RNG[X]]] // CHECK
//    xs
//  }
//
//}
trait Randomizable[X] {
  def fromLong(l: Long): X

  def toLong(x: X): Long
}

abstract class RandomMonad[X: Randomizable, Repr](private[evolution] val seed: Long) extends Random[X] {
  override def toString(): String = s"RandomMonad(${apply()})"

  def build(n: Long): Repr

  def next: Random[X]

  def apply(): X = implicitly[Randomizable[X]].fromLong(seed)

  def unit[U: Randomizable](u: U): Random[U]

  /**
    * Method to map this random state into another random object
    *
    * @param f the function to map a T value into a U value
    * @tparam U the underlying type of the resulting random object
    * @return a new random state
    */
  def map[U: Randomizable](f: X => U): Random[U] = flatMap { x => unit(f(x)) }

  /**
    * Method to flatMap this random state into another random object
    *
    * @param f the function to map a T value into a Random[U] value
    * @tparam U the underlying type of the resulting random object
    * @return a new random state
    */
  def flatMap[U](f: X => Random[U]): Random[U] = f(apply())

  /**
    * CONSIDER implement in terms to streamTake
    *
    * @return a stream of X values
    */
  def toStream: Stream[X] = Stream.cons[X](next.apply(), next.asInstanceOf[RandomMonad[X, Repr]].toStream)

}

//trait Randoms[X, Repr] {
//  /**
//    * Method to return a Seq of X values, together with a Random[X] from which more values can be taken.
//    *
//    * @param n the number of elements to take
//    * @return a tuple of (Repr, Seq[X])
//    */
//  def take(n: Int): (Repr, Seq[X])
//}


abstract class RandomMonadJava[X: Randomizable, Repr](val s: Long) extends RandomMonad[X, Repr](s) {

  def next: Random[X] = build(new java.util.Random(seed).nextLong()).asInstanceOf[Random[X]]

  def streamTake(n: Int): (Repr, Seq[X]) = {
    @tailrec def inner(r: RandomMonad[X, Repr], s: Stream[X], n_ : Int): (RandomMonad[X, Repr], Stream[X]) = if (n_ == 0) (r, s)
    else inner(r.next.asInstanceOf[RandomMonad[X, Repr]], Stream.cons[X](r(), s), n_ - 1)

    val (xr: RandomMonad[X, Repr], xs: Stream[X]) = inner(this, Stream.empty, n)
    (build(xr.seed), xs)
  }

  // CONSIDER why do we need this method as well as streamTake?
  def take(n: Int): (Repr, Seq[X]) = streamTake(n)

}

case class RNG[T: Randomizable](private val l: Long) extends RandomMonadJava[T, RNG[T]](l) {
  override def apply(): T = super.apply()

  def build(n: Long): RNG[T] = RNG(n)

  def unit[U: Randomizable](u: U): Random[U] = new RNG[U](l)
}

object Random {

  trait RandomizableInt extends Randomizable[Int] {
    def fromLong(l: Long): Int = (l % Int.MaxValue).toInt

    def toLong(x: Int): Long = x.toLong
  }

  implicit object RandomizableInt extends RandomizableInt

  trait RandomizableBoolean extends Randomizable[Boolean] {
    def fromLong(l: Long): Boolean = l % 2 == 0

    def toLong(x: Boolean): Long = if (x) 0L else 1L
  }

  implicit object RandomizableBoolean extends RandomizableBoolean

  trait RandomizableLong extends Randomizable[Long] {
    def fromLong(l: Long): Long = l

    def toLong(x: Long): Long = x
  }

  implicit object RandomizableLong extends RandomizableLong

  trait RandomizableString extends Randomizable[String] {
    def fromLong(l: Long): String = l.toString

    def toLong(x: String): Long = x.toLong
  }

  implicit object RandomizableString extends RandomizableString

  trait RandomizableBase extends Randomizable[Base] {
    def fromLong(l: Long): Base = Base(l.toInt)

    def toLong(x: Base): Long = x.i
  }

  implicit object RandomizableBase extends RandomizableBase

}