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

import com.phasmid.darwin.base.IdentifierName
import com.phasmid.darwin.eco._
import com.phasmid.darwin.genetics._
import com.phasmid.darwin.genetics.dna.{Base, Guanine}
import com.phasmid.laScala.Version
import com.phasmid.laScala.fp.Streamer
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 7/25/16.
  */
class OrganismSpec extends FlatSpec with Matchers with Inside {

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  implicit val random: RNG[Long] = RNG[Long](0)

  private val sElephantGrass = "elephant grass"
  private val elephantGrass: Factor = Factor(sElephantGrass)
  private val factorMap = Map("height" -> elephantGrass)

  val adapter: Adapter[Double, Int] = new AbstractAdapter[Double, Int] {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, ShapeFunction[Double, Int])] = f match {
      case `elephantGrass` => t.characteristic.name match {
        case "height" => Success((t.value, ShapeFunction.shapeDiracInv_I))
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }

  val ff: (Double, ShapeFunction[Double, Int], Int) => Fitness = {
    (t, fs, x) =>
      fs match {
        case ShapeFunction(_, f) => f(x)(t)
        case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $fs")
      }
  }

  import com.phasmid.darwin.evolution.Random.RandomizableBase

  val height: Characteristic = Characteristic("height")
  val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
  val ecology: Ecology[Double, Int] = Ecology[Double, Int]("test", factorMap, ff, adapter)
  val adaptatype: Adaptatype[Int] = ecology(phenotype)
  private val adaptations: Seq[Adaptation[Int]] = adaptatype.adaptations
  val adaptation: Adaptation[Int] = adaptations.head
  adaptation should matchPattern { case Adaptation(`elephantGrass`, _) => }
  val ecoFactor: EcoFactor[Int] = EcoFactor(elephantGrass, 1)
  val ecoFactors: Map[String, EcoFactor[Int]] = Map(sElephantGrass -> ecoFactor)
  private val transcriber: PlainTranscriber[Base, String] = PlainTranscriber[Base, String] { bs => Some(Allele(bs.head.toString)) }
  val hox: Location = Location("hox", 0, 1)
  // C or A
  val hix = Location("hix", 1, 1)
  // G or G
  val hoxB = Location("hoxB", 1, 1)
  val hoxA = Location("hoxA", 0, 1)
  val hoxC = Location("hoxC", 2, 1)
  val ts = Set(Allele("T"), Allele("S"))
  val pq = Set(Allele("P"), Allele("Q"))
  private val locHeight = Location("height", 0, 1)
  val locusH = PlainLocus(locHeight, ts, Some(Allele("T")))
  val locusG = PlainLocus(Location("girth", 1, 1), pq, Some(Allele("P")))
  val locusMap: (Location) => Locus[String] = Map(
    locHeight -> locusH,
    hox -> UnknownLocus[String](hox),
    hix -> UnknownLocus[String](hix),
    hoxA -> UnknownLocus[String](hoxA),
    hoxB -> UnknownLocus[String](hoxB),
    hoxC -> UnknownLocus[String](hoxC))
  val girth = Characteristic("girth")
  val karyotype = Seq(Chromosome("test", isSex = false, Seq(locHeight)))
  val genome: Genome[Base, String, Boolean] = Genome("test", karyotype, true, transcriber, locusMap)
  val traitMapper = TraitMapperMapped(Map(height -> Map("T" -> 2.0, "S" -> 1.6), girth -> Map("Q" -> 3.0, "P" -> 1.2)))
  val geneHGG = genome.PGene(locusH, Seq(Allele("G"), Allele("G")))

  def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable

  val expresser: Expresser[String, Boolean, Double] = ExpresserMendelian[String, Boolean, Double](traitMapper)
  val phenome: Phenome[String, Boolean, Double] = Phenome("test", Map(locusH -> height, locusG -> girth), expresser, attraction)
  val generation = Version(1, None)

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  implicit val idStreamer: Streamer[Long] = Streamer(RNG[Long](0).toStream)

  behavior of "Organism"

  it should "create random Organism correctly" in {
    val random = RNG[Base](0L)
    println(s"ecology: $ecology")
    println(s"ecoFactors: $ecoFactors")
    val (bn, _) = genome.recombine(random)
    val organism = SexualSedentaryOrganism(generation, genome, phenome, bn, ecology)
    val dna = for (a <- organism.nucleus; b <- a) yield b.bases
    dna.flatten shouldBe List(Guanine, Guanine)
  }

  it should "calculate genotype correctly" in {
    val random = RNG[Base](0L)
    println(s"ecology: $ecology")
    println(s"ecoFactors: $ecoFactors")
    val (bn, _) = genome.recombine(random)
    val organism = SexualSedentaryOrganism(generation, genome, phenome, bn, ecology)
    val actual = organism.genotype
    actual should matchPattern { case Genotype(_, Seq(_)) => }
    actual.genes shouldBe Seq(geneHGG)
  }

  it should "calculate phenotype correctly" in {
    val random = RNG[Base](3L)
    println(s"ecology: $ecology")
    println(s"ecoFactors: $ecoFactors")
    val (bn, _) = genome.recombine(random)
    val organism = SexualSedentaryOrganism(generation, genome, phenome, bn, ecology)
    organism.phenotype should matchPattern { case Phenotype(_, List(Trait(`height`, 2.0))) => }
  }

  it should "calculate adaptatype" in {
    val random = RNG[Base](3L)
    println(s"ecology: $ecology")
    println(s"ecoFactors: $ecoFactors")
    val (bn, _) = genome.recombine(random)
    val organism = SexualSedentaryOrganism(generation, genome, phenome, bn, ecology)
    val z: Adaptatype[Int] = ecology(organism.phenotype)
    z should matchPattern { case Adaptatype(_, List(Adaptation(`elephantGrass`, _))) => }
  }

  it should "calculate fitness" in {
    val random = RNG[Base](3L)
    println(s"ecology: $ecology")
    println(s"ecoFactors: $ecoFactors")
    val (bn, _) = genome.recombine(random)
    val organism = SexualSedentaryOrganism(generation, genome, phenome, bn, ecology)
    val fy: Try[Fitness] = organism.fitness(ecology, ecoFactors)
    fy should matchPattern { case Success(_) => }
    // TODO ensure that this is really correct
    fy.get.x shouldBe 0.0 +- 0.001
  }
}
