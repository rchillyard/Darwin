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

/**
  * Created by scalaprof on 7/19/17.
  */
trait Random[X] extends (() => X) {
  def next: Random[X]
}

trait Randomizable[X] {
  def fromLong(l: Long): X

  def toLong(x: X): Long
}

abstract class RandomMonad[X: Randomizable, Repr](private val seed: Long) extends Random[X] {
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
    * @return a stream of X values
    */
  def toStream: Stream[X] = Stream.cons[X](next.apply(), next.asInstanceOf[RandomMonad[X, Repr]].toStream)
}

abstract class RandomMonadJava[X: Randomizable, Repr](private val seed: Long) extends RandomMonad[X, Repr](seed) {

  def next: Random[X] = build(new java.util.Random(seed).nextLong()).asInstanceOf[Random[X]]

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

}