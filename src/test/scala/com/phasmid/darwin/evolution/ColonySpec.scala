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

import com.phasmid.darwin.eco._
import com.phasmid.darwin.genetics._
import com.phasmid.darwin.genetics.dna.Base
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 7/25/16.
  */
class ColonySpec extends FlatSpec with Matchers with Inside {

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  implicit val random: RNG[Long] = RNG[Long](0)

  private val sElephantGrass = "elephant grass"
  private val elephantGrass = Factor(sElephantGrass)
  private val factorMap = Map("height" -> elephantGrass)

  val adapter: Adapter[Double, Int] = new AbstractAdapter[Double, Int] {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, FunctionShape[Int, Double])] = f match {
      case `elephantGrass` => t.characteristic.name match {
        case "height" => Success((t.value, FunctionShape.shapeDiracInv_I))
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }

  val ff: (Double, FunctionShape[Int, Double], Int) => Fitness = {
    (t, fs, x) =>
      fs match {
        case FunctionShape(_, f) => f(x)(t)
        case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $fs")
      }
  }

  import com.phasmid.darwin.evolution.Random.RandomizableBase

  val height = Characteristic("height")
  val phenotype: Phenotype[Double] = Phenotype(Seq(Trait(height, 2.0)))
  val ecology: Ecology[Double, Int] = Ecology[Double, Int]("test", factorMap, ff, adapter)
  val adaptatype: Adaptatype[Int] = ecology(phenotype)
  private val adaptations = adaptatype.adaptations
  val adaptation: Adaptation[Int] = adaptations.head
  adaptation should matchPattern { case Adaptation(`elephantGrass`, _) => }
  val ecoFactor = EcoFactor(elephantGrass, 1.9)
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
  val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
  val genome = Genome("test", karyotype, true, transcriber, locusMap)
  val ts = Set(Allele("T"), Allele("S"))
  val pq = Set(Allele("P"), Allele("Q"))
  val locusH = PlainLocus(Location("height", 0, 0), ts, Some(Allele("T")))
  val locusG = PlainLocus(Location("girth", 1, 0), pq, Some(Allele("P")))
  val girth = Characteristic("girth")
  val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
    case (`height`, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
    case (`girth`, Allele(g)) => Success(Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 }))
    case (c, _) => Failure(GeneticsException(s"traitMapper: no trait for $c"))
  }

  def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable

  val expresser: Expresser[Boolean, String, Double] = ExpresserMendelian[Boolean, String, Double](traitMapper)
  val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locusH -> height, locusG -> girth), expresser, attraction)

  behavior of "Colony"

  it should "evolve" in {
    val random = RNG[Base](0L)
    val colony: Colony[Base, String, Double, Long, Int] = Colony("test colony", ecology, genome, phenome).seedMembers(10, random)
    println(colony)
    val cy = colony.next()
    for (c <- cy) println(c)
  }
}
