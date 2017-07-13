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

import com.phasmid.darwin.genetics.dna.Base

import scala.util.Try

/**
  * This class models the physical genetic material from which a genotype is derived.
  *
  * CONSIDER another possible name for this type is Chromatid. Geneticists please advise.
  *
  * Created by scalaprof on 5/5/16.
  */
case class Sequence[+B](bases: Seq[B]) {
  /**
    * Method to locate a Location on this Sequence.
    *
    * @param location the desired Location
    * @return Some(sequence) if location found, otherwise None
    */
  def locate(location: Location): Option[Seq[B]] =
    if (location.offset >= 0 && location.length + location.offset <= bases.length)
      Try(bases.slice(location.offset, location.offset + location.length)).toOption
    else None

  def :+[Z >: B](other: Sequence[Z]): Sequence[Z] = :+(other.bases)

  def +:[Z >: B](other: Sequence[Z]): Sequence[Z] = other.bases +: this

  def :+[Z >: B](other: Seq[Z]): Sequence[Z] = Sequence(bases ++ other)

  def +:[Z >: B](other: Seq[Z]): Sequence[Z] = Sequence(other ++: bases)

  def :+[Z >: B](other: Z): Sequence[Z] = :+(Seq(other))

  def +:[Z >: B](other: Z): Sequence[Z] = +:(Seq(other))

  // TODO bring back the implicit renderer: but be aware that can
  // mess up the concatenation methods above.
  override def toString: String = bases.mkString("", "", "")
}

trait Renderer[B] extends (Seq[B] => String)

/**
  * @author scalaprof
  *
  */
object Sequence {
  /**
    * Method to construct a Sequence from a String of Char
    *
    * XXX at one point, we converted the result of the for comprehension into a List. I'm not sure why but it doesn't seem to be needed
    *
    * @param w    the String
    * @param conv the method to convert from a Char to a B
    * @tparam B The Base type
    * @return a Sequence[B]
    */
  def apply[B](w: String)(implicit conv: Char => B): Sequence[B] = new Sequence(for (c <- w) yield conv(c))

  /**
    * Method to construct a Sequence from a variable number of bases
    *
    * @param bases the bases
    * @tparam B the Base type
    * @return a Sequence[B]
    */
  def create[B](bases: B*) = Sequence(bases)

  implicit def renderer[B] = new Renderer[B] {
    def apply(bs: Seq[B]): String = bs.mkString("", "", "")
  }
}

trait Ordinal[X] {
  def fromInt(i: Int): X
}

object Ordinal {

  implicit object OrdinalBase extends Ordinal[Base] {
    override def fromInt(i: Int): Base = Base(i)
  }

}