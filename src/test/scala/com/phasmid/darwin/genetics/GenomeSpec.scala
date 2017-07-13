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
class GenomeSpec extends FlatSpec with Matchers {

  // XXX this is a very simple 1:1 mapping from bases to alleles
  private val transcriber = PlainTranscriber[Base, String] { bs => Some(Allele(bs.head.toString)) }
  val hox = Location("hox", 0, 1)
  // C or A
  val hix = Location("hix", 1, 1)
  // G or G
  val hoxB = Location("hoxB", 1, 1)
  val hoxA = Location("hoxA", 0, 1)
  val hoxC = Location("hoxC", 2, 1)
  val locusMap: (Location) => Locus[String] = Map(
    hox -> UnknownLocus[String](hox),
    hix -> UnknownLocus[String](hix),
    hoxA -> UnknownLocus[String](hoxA),
    hoxB -> UnknownLocus[String](hoxB),
    hoxC -> UnknownLocus[String](hoxC))

  "transcribe" should "give Alleles A and C" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    // NOTE that we must explicitly state the type because the sequences are only of length 1
    val bss: Seq[Sequence[Base]] = Seq(Sequence(Seq(Cytosine)), Sequence(Seq(Adenine)))
    val geneHox: Gene[Boolean, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(false) shouldBe Allele("A")
    geneHox(true) shouldBe Allele("C")
  }

  it should "get multiple loci right" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)), Sequence(Seq(Adenine, Guanine)))
    val geneHox: Gene[Boolean, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(false) shouldBe Allele("A")
    geneHox(true) shouldBe Allele("C")
    val geneHix: Gene[Boolean, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(false) shouldBe Allele("G")
    geneHix(true) shouldBe Allele("G")
  }

  it should "work with haploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, (), transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)))
    val geneHox: Gene[Unit, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(()) shouldBe Allele("C")
    val geneHix: Gene[Unit, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(()) shouldBe Allele("G")
  }

  it should "work with triploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, 3, transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)), Sequence(Seq(Adenine, Thymine)), Sequence(Seq(Guanine, Adenine)))
    val geneHox: Gene[Int, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(0) shouldBe Allele("C")
    geneHox(1) shouldBe Allele("A")
    geneHox(2) shouldBe Allele("G")
    val geneHix: Gene[Int, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(1) shouldBe Allele("T")
  }
}
