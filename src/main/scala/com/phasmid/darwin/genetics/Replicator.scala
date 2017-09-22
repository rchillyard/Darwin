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

package com.phasmid.darwin.genetics

import com.phasmid.darwin.base.Auditable
import com.phasmid.laScala.{Prefix, RNG, RenderableCaseClass}

/**
  * Created by scalaprof on 7/31/16.
  */
trait Replicator[B] {

  /**
    * Replicate a sequence of B elements
    *
    * @param bs a base sequence
    * @return a Seq of B which may be an imperfect copy of s
    */
  def replicate(bs: Seq[B]): Seq[B]

  /**
    * Replicate a Sequence of B elements
    *
    * @param bs a base sequence
    * @return a Sequence of B which may be an imperfect copy of s
    */
  def replicate(bs: Sequence[B]): Sequence[B] = Sequence(replicate(bs.bases))
}

/**
  * An imperfect replicator that has a finite probability of mis-copying a given B element
  *
  * @param mnopc the mean number of perfect copies before an error is made
  * @param r     a random number generator (of Int)
  * @tparam B the base type
  */
case class ImperfectReplicator[B: Ordinal](mnopc: Int, r: RNG[Int]) extends Replicator[B] with Auditable {
  // NOTE: a variable.
  var i = 0
  private val rmnopc = RNG.values(r) map (_ % mnopc)

  def random: Int = {i = i + 1; rmnopc(i)}

  // CONSIDER doing this more efficiently, while using fewer random numbers
  def replicate(bs: Seq[B]): Seq[B] =
    for (b <- bs) yield
      if (random == 0) implicitly[Ordinal[B]].fromInt(random)
      else b

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[ImperfectReplicator[Any]]).render(indent)(tab)

}

/**
  * An perfect replicator that has a zero probability of mis-copying a given B element
  *
  * @tparam B the base type
  */
case class PerfectReplicator[B]() extends Replicator[B] with Auditable {
  def replicate(bs: Seq[B]): Seq[B] = bs

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[PerfectReplicator[Any]]).render(indent)(tab)
}
