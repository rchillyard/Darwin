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
  * Created by scalaprof on 5/6/16.
  */
class TranscriberSpec extends FlatSpec with Matchers {

  "apply" should "work with Base objects" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Base, String] { bs => Some(Allele(bs.head.toString)) }
    val hox = Location("hox", 0, 1) // C or A
    val bs = Sequence(Seq(Cytosine, Guanine))
    val allele = transcriber(bs, hox)
    allele shouldBe Some(Allele("C"))
  }
  it should "work with Int objects" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Int, String] { bs => Some(Allele(bs.head.toString)) }
    val hox = Location("hox", 0, 1)
    val bs = Sequence(Seq(1, 2))
    val allele = transcriber(bs, hox)
    allele shouldBe Some(Allele("1"))
  }
  it should "work with Int objects and Int alleles" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Int, Int] { bs => Some(Allele(bs.head)) }
    val hox = Location("hox", 0, 1)
    val bs = Sequence(Seq(1, 2))
    val allele = transcriber(bs, hox)
    allele shouldBe Some(Allele(1))
  }
}
