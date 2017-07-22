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
  * Wheel of Fortune Class.
  * This defines a random state for a set of outcomes (ts) which have corresponding odds (revCumOdds).
  * It is expected that applications will create a Wheel by using the apply function in companion object.
  *
  * @param l          the pseudo-random Long value which defines the current state (not available publicly)
  * @param ts         the set of outcomes
  * @param revCumOdds the cumulative probabilities of drawing each value from ts (in reverse order)
  *                   Created by scalaprof on 7/17/17.
  * @tparam T The underlying type of the possible outcomes
  */
case class Wheel[T: Randomizable](private val l: Long, ts: Seq[T], revCumOdds: Seq[Long]) extends RandomMonadJava[T, Wheel[T]](l) {
  require(ts.nonEmpty)
  private val N = ts.length
  require(N + 1 == revCumOdds.length, s"the number of values ($N) should equal the number of revCumOdds less one (${revCumOdds.length - 1})")
  val n: Long = revCumOdds.head
  val m: Long = {
    val i = l % n; if (i >= 0L) i else i + n
  }
  assert(m >= 0 && m < n)

  override def apply(): T = revCumOdds indexWhere (_ <= m) match {
    case -1 => throw EvolutionException(s"logic error in Wheel: $this")
    case i => ts(N - i)
  }

  def build(n: Long): Wheel[T] = new Wheel(n, ts, revCumOdds)

  def unit[U: Randomizable](u: U): Random[U] = {
    val fL = implicitly[Randomizable[T]].toLong _
    val fU = implicitly[Randomizable[U]].fromLong _
    new Wheel[U](l, ts map (fL andThen fU), revCumOdds)
  }

  override def toString(): String = s"Wheel($ts, $revCumOdds, $l)"
}

object Wheel {
  def apply[T: Randomizable](ts: Seq[T], odds: Seq[Long])(l: Long = System.currentTimeMillis()): Wheel[T] = {
    require(ts.length == odds.length, s"the number of values (${ts.length}) should equal the number of odds (${odds.length})")

    def inner(r: Seq[Long], a: Long, ws: Seq[Long]): Seq[Long] = ws match {
      case Nil => r :+ a
      case h :: t => inner(r :+ a, a + h, t)
    }

    new Wheel[T](l, ts, inner(Nil, 0L, odds).reverse)
  }

  trait RandomizableBoolean extends Randomizable[Boolean] {
    def fromLong(l: Long): Boolean = l % 2 == 0

    def toLong(x: Boolean): Long = if (x) 0L else 1L
  }

  implicit object RandomizableBoolean extends RandomizableBoolean

}