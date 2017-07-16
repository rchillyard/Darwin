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

import com.phasmid.darwin.eco.FunctionShape.logistic
import com.phasmid.darwin.eco.{Fitness, FunctionShape}
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class PhenomeSpec extends FlatSpec with Matchers {

  val ts = Set(Allele("T"), Allele("S"))
  val pq = Set(Allele("P"), Allele("Q"))
  val locusH = PlainLocus(Location("height", 0, 0), ts, Some(Allele("T")))
  val locusG = PlainLocus(Location("girth", 1, 0), pq, Some(Allele("P")))
  private val geneH1 = MendelianGene[Boolean, String](locusH, Seq(Allele("T"), Allele("S")))
  private val geneH2 = MendelianGene[Boolean, String](locusH, Seq(Allele("S"), Allele("S")))
  private val geneG1 = MendelianGene[Boolean, String](locusG, Seq(Allele("Q"), Allele("Q")))
  private val geneG2 = MendelianGene[Boolean, String](locusG, Seq(Allele("P"), Allele("Q")))
  val height = Characteristic("height")
  val girth = Characteristic("girth")
  val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
    case (`height`, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
    case (`girth`, Allele(g)) => Success(Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 }))
    case (c, _) => Failure(GeneticsException(s"traitMapper: no trait for $c"))
  }

  val expresser: Expresser[Boolean, String, Double] = ExpresserMendelian[Boolean, String, Double](traitMapper)


  behavior of "apply"
  it should "work" in {
    val genotype = Genotype(Seq(geneH1, geneG2))

    def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable

    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locusH -> height, locusG -> girth), expresser, attraction)
    val phenotype = phenome(genotype)
    phenotype.traits.length shouldBe 2
    phenotype.traits.head shouldBe Trait[Double](height, 2.0)
    phenotype.traits.tail.head shouldBe Trait[Double](height, 1.2)
  }

  behavior of "attractiveness"
  it should "work where there is no sexual selection" in {
    def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable

    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locusH -> height, locusG -> girth), expresser, attraction)
    val genotype1 = Genotype(Seq(geneH1, geneG2))
    val genotype2 = Genotype(Seq(geneG2, geneH1))
    val phenotype1 = phenome(genotype1)
    val phenotype2 = phenome(genotype2)
    phenome.attractiveness(phenotype1, phenotype2) shouldBe Fitness.viable
  }

  it should "work where there is sexual selection" in {
    val mockHeight = Characteristic("height", isSexuallySelective = true)
    val mockTraitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
      case (Characteristic("height", _), Allele(h)) => println(s"mockHeight with allele $h"); Success(Trait(mockHeight, h match { case "T" => 2.0; case "S" => 1.6 }))
      case (Characteristic("girth", _), Allele(g)) => println(s"girth with allele $g"); Success(Trait(girth, g match { case "Q" => 3.0; case "P" => 1.2 }))
      case (c, _) => Failure(GeneticsException(s"traitMapper: no trait for $c"))
    }
    val mockExpresser: Expresser[Boolean, String, Double] = new ExpresserMendelian[Boolean, String, Double](mockTraitMapper)
    val mockFunctionShape: FunctionShape[Double, Double] = FunctionShape(logistic, identity, 0.1, "shapeLogistic")

    def mockAttraction(observer: Trait[Double], observed: Trait[Double]): Fitness = {
      (observer, observed) match {
        case (Trait(ch1, x1), Trait(ch2, x2)) =>
          if (ch1 == ch2) mockFunctionShape.f(x1)(x2) else Fitness.nonViable
        case _ => Fitness.viable
      }
    }

    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locusH -> mockHeight, locusG -> girth), mockExpresser, mockAttraction)
    phenome.attractiveness(phenome(Genotype(Seq(geneG1, geneH2))), phenome(Genotype(Seq(geneG2, geneH1))))() should ===(0.9820137900379085 +- 1E-10)
    phenome.attractiveness(phenome(Genotype(Seq(geneG2, geneH1))), phenome(Genotype(Seq(geneG1, geneH1))))() should ===(0.5 +- 1E-10)
  }
}
