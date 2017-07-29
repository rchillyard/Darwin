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

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 5/6/16.
  */
class ExpresserSpec extends FlatSpec with Matchers {

  private val height = Characteristic("height")
  private val tall = Trait[Double](height, 2.0)
  private val short = Trait[Double](height, 1.6)

  "apply" should "work" in {
    val gene = new Gene[Boolean, String] {
      override def locus: Locus[String] = UnknownLocus(Location("test", 0, 0))

      override def name: String = "test"

      override def apply(b: Boolean): Allele[String] = if (b) Allele("T") else Allele("S")

      override def distinct: Seq[Allele[String]] = Seq(Allele("T"))

      override def toString = s"gene $name at $locus with alleles: ${apply(true)} and ${apply(false)} and distinct: $distinct"
    }

    val exp = new AbstractExpresser[Boolean, String, Double] {
      // XXX a rather simple non-Mendelian selector which determines the allele simply from one specific alternative of the gene
      override def selectAllele(gene: Gene[Boolean, String]): Allele[String] = gene(true)

      val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
        case (_, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
        case (c, _) => Failure(GeneticsException(s"traitMapper failed for $c"))
      }
    }
    exp(height, gene) shouldBe Success(tall)
  }
  it should "work for Mendelian expression" in {
    val ts = Set(Allele("T"), Allele("S"))
    // TODO use pq
    val pq = Set(Allele("P"), Allele("Q"))
    val locus1 = PlainLocus(Location("height", 0, 0), ts, Some(Allele("S")))
    //    val locus2 = PlainLocus(Location("girth", 0, 0), pq, Some(Allele("P")))
    val gene1 = MendelianGene[Boolean, String](locus1, Seq(Allele("T"), Allele("S")))
    val girth = Characteristic("girth")
    val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
      case (`height`, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
      case (`girth`, Allele(g)) => Success(Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 }))
      case (c, _) => Failure(GeneticsException(s"traitMapper failed for $c"))
    }
    val exp = ExpresserMendelian[Boolean, String, Double](traitMapper)
    exp(height, gene1) shouldBe Success(short)
  }
}

case class UnknownLocus[G](location: Location) extends Locus[G] {
  override def dominant: Option[Allele[G]] = None

  override def apply: Set[Allele[G]] = Set()
}
