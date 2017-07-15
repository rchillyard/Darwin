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

import com.phasmid.darwin.eco.Fitness
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class PhenomeSpec extends FlatSpec with Matchers {

  val ts = Set(Allele("T"), Allele("S"))
  val pq = Set(Allele("P"), Allele("Q"))
  val locus1 = PlainLocus(Location("height", 0, 0), ts, Some(Allele("S")))
  val locus2 = PlainLocus(Location("girth", 1, 0), pq, Some(Allele("P")))
  private val gene1 = MendelianGene[Boolean, String](locus1, Seq(Allele("T"), Allele("S")))
  private val gene2 = MendelianGene[Boolean, String](locus2, Seq(Allele("P"), Allele("Q")))
  val height = Characteristic("height")
  val girth = Characteristic("girth")
  val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
    case (`height`, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
    case (`girth`, Allele(g)) => Success(Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 }))
    case (c, _) => Failure(GeneticsException(s"no trait traitMapper for $c"))
  }

  val expresser: Expresser[Boolean, String, Double] = new ExpresserMendelian[Boolean, String, Double](traitMapper)

  def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable

  behavior of "apply"
  it should "work" in {
    val genotype = Genotype(Seq(gene1, gene2))
    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locus1 -> height, locus2 -> girth), expresser, attraction)
    val phenotype = phenome(genotype)
    phenotype.traits.length shouldBe 2
    phenotype.traits.head shouldBe Trait[Double](height, 1.6)
    phenotype.traits.tail.head shouldBe Trait[Double](height, 1.2)
  }

  behavior of ""
  it should "work" in {
    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locus1 -> height, locus2 -> girth), expresser, attraction)
    val genotype1 = Genotype(Seq(gene1, gene2))
    val genotype2 = Genotype(Seq(gene2, gene1))
    val phenotype1 = phenome(genotype1)
    val phenotype2 = phenome(genotype2)
    phenome.attractiveness(phenotype1, phenotype2) shouldBe Fitness.viable
  }
}
