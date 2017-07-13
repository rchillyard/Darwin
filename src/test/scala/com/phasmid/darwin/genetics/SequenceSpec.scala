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

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class SequenceSpec extends FlatSpec with Matchers {

  "Sequence" should "work for one base" in {
    val x = Sequence.create(Cytosine)
    x should matchPattern { case Sequence(Seq(Cytosine)) => }
    x.toString shouldBe "C"
  }
  it should "work for two bases" in {
    val x = Sequence.create(Cytosine, Guanine)
    x should matchPattern { case Sequence(Seq(Cytosine, Guanine)) => }
    x.bases.size shouldBe 2
    x.toString shouldBe "CG"
  }
  "locate" should "fail for bad Location" in {
    val x = Sequence.create(Cytosine)
    val r = x.locate(Location("", -1, 0))
    r should matchPattern { case None => }
  }
  "concatenation" should "work with +: base" in {
    val x = Sequence.create(Cytosine)
    val r = x :+ Guanine
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with :+ Seq" in {
    val x = Sequence.create(Cytosine)
    val r = x :+ Seq(Guanine)
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with base +: " in {
    val x = Sequence.create(Cytosine)
    val r = Guanine +: x
    r shouldBe Sequence(Seq(Guanine, Cytosine))
  }
  it should "work with Seq +:" in {
    val x = Sequence.create(Cytosine)
    val r = Seq(Guanine) +: x
    r shouldBe Sequence(Seq(Guanine, Cytosine))
  }
  it should "work with :+ Sequence" in {
    val x = Sequence.create(Cytosine)
    val y = Sequence.create(Guanine)
    val r = x :+ y
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with Sequence +:" in {
    val x = Sequence.create(Cytosine)
    val y = Sequence.create(Guanine)
    val r = x +: y
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
}