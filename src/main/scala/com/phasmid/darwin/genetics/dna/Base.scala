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

package com.phasmid.darwin.genetics.dna

import com.phasmid.darwin.Identifier

import scala.language.implicitConversions

/**
  * This module defines the DNA base set.
  * Thus Base can be used as the type in Sequence[B] if you want DNA-based genetics.
  * Of course, you could equally well just define Sequence[Int] or Sequence[Boolean] or whatever.
  *
  * @author scalaprof
  */
trait Base extends Identifier {
  def pair: Base
  val i: Int
}

case object Guanine extends Base {
  def pair = Cytosine

  val name = "G"
  val i = 0
}

case object Cytosine extends Base {
  def pair = Guanine

  val name = "C"
  val i = 1
}

case object Adenine extends Base {
  def pair = Thymine

  val name = "A"
  val i = 2
}

case object Thymine extends Base {
  def pair = Adenine

  val name = "T"
  val i = 3
}

case class Invalid(x: Char) extends Base {
  def pair = Invalid(x)

  val name = s"<Invalid: $x>"
  val i: Int = -1
}

object Base {
  def apply(x: Char): Base = x match {
    case 'G' => Guanine
    case 'C' => Cytosine
    case 'A' => Adenine
    case 'T' => Thymine
    case _ => Invalid(x)
  }

  def apply(x: Int): Base = x % 4 match {
    case 0 => Guanine
    case 1 => Cytosine
    case 2 => Adenine
    case 3 => Thymine
  }

  implicit def convert(c: Char): Base = apply(c)

  implicit val render: Seq[Base] => String = { bs => bs.mkString("", "", "") }
}
