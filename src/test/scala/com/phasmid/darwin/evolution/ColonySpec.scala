///*
// * DARWIN Genetic Algorithms Framework Project.
// * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
// *
// * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
// * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
// *
// *      This file is part of Darwin.
// *
// *      Darwin is free software: you can redistribute it and/or modify
// *      it under the terms of the GNU General Public License as published by
// *      the Free Software Foundation, either version 3 of the License, or
// *      (at your option) any later version.
// *
// *      This program is distributed in the hope that it will be useful,
// *      but WITHOUT ANY WARRANTY; without even the implied warranty of
// *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *      GNU General Public License for more details.
// *
// *      You should have received a copy of the GNU General Public License
// *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.phasmid.darwin.evolution
//
//import com.phasmid.darwin.base.IdentifierName
//import com.phasmid.darwin.eco._
//import com.phasmid.darwin.genetics._
//import com.phasmid.darwin.genetics.dna.Base
//import com.phasmid.darwin.plugin.Listener
//import com.phasmid.laScala.Version
//import com.phasmid.laScala.fp.{NamedFunction3, Streamer}
//import org.scalatest.{FlatSpec, Inside, Matchers}
//
//import scala.util.{Failure, Success, Try}
//
///**
//  * Created by scalaprof on 7/25/16.
//  */
//class ColonySpec extends FlatSpec with Matchers with Inside {
//
//  import com.phasmid.darwin.evolution.Random.RandomizableLong
//
//  implicit val random: RNG[Long] = RNG[Long](0)
//
//  private val sElephantGrass = "elephant grass"
//  private val elephantGrass: Factor = Factor(sElephantGrass)
//  private val sHeight = "height"
//  val adapter: Adapter[Double, Int] = new AbstractAdapter[Double, Int]("elephant grass adapter") {
//    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, ShapeFunction[Double, Int])] = f match {
//      case `elephantGrass` => t.characteristic.name match {
//        case `sHeight` => Success((t.value, ShapeFunction.shapeDiracInv_I))
//        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
//      }
//    }
//  }
//
//  import com.phasmid.darwin.evolution.Random.RandomizableBase
//
//  val ff = new NamedFunction3[Double, ShapeFunction[Double, Int], Int, Fitness]("shape-only", { (t, fs, x) => fs(x)(t) })
//  val height: Characteristic = Characteristic(sHeight)
//  val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test phenotype"), Seq(Trait(height, 2.0)))
//  val ecology: Ecology[Double, Int] = Ecology[Double, Int]("test ecology", Map(sHeight -> elephantGrass), ff, adapter)
//  val adaptatype: Adaptatype[Int] = ecology(phenotype)
//  private val adaptations: Seq[Adaptation[Int]] = adaptatype.adaptations
//  val adaptation: Adaptation[Int] = adaptations.head
//  adaptation should matchPattern { case Adaptation(`elephantGrass`, _) => }
//  val ecoFactor: EcoFactor[Int] = EcoFactor(elephantGrass, 1)
//  val habitat: Habitat[Int] = Map(sElephantGrass -> ecoFactor)
//  private val transcriber: PlainTranscriber[Base, String] = PlainTranscriber[Base, String] { bs => Some(Allele(bs.head.toString)) }
//  val hox: Location = Location("hox", 0, 1)
//  // C or A
//  val hix = Location("hix", 1, 1)
//  // G or G
//  val hoxB = Location("hoxB", 1, 1)
//  val hoxA = Location("hoxA", 0, 1)
//  val hoxC = Location("hoxC", 2, 1)
//  val ts = Set(Allele("T"), Allele("S"))
//  val pq = Set(Allele("P"), Allele("Q"))
//  private val locHeight = Location(sHeight, 0, 1)
//  val locusH = PlainLocus(locHeight, ts, Some(Allele("T")))
//  val locusG = PlainLocus(Location("girth", 1, 1), pq, Some(Allele("P")))
//  val locusMap: (Location) => Locus[String] = Map(
//    locHeight -> locusH,
//    hox -> UnknownLocus[String](hox),
//    hix -> UnknownLocus[String](hix),
//    hoxA -> UnknownLocus[String](hoxA),
//    hoxB -> UnknownLocus[String](hoxB),
//    hoxC -> UnknownLocus[String](hoxC))
//  val girth = Characteristic("girth")
//  val karyotype = Seq(Chromosome("test", isSex = false, Seq(locHeight)))
//  val genome: Genome[Base, String, Boolean] = Genome("test", karyotype, true, transcriber, locusMap)
//  val traitMapper: (Characteristic, Allele[String]) => Try[Trait[Double]] = {
//    case (`height`, Allele(h)) => Success(Trait(height, h match { case "T" => 2.0; case "S" => 1.6 }))
//    case (`girth`, Allele(g)) => Success(Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 }))
//    case (c, _) => Failure(GeneticsException(s"traitMapper: no trait for $c"))
//  }
//
//  def attraction(observer: Trait[Double], observed: Trait[Double]): Fitness = Fitness.viable
//
//  val expresser: Expresser[String, Boolean, Double] = ExpresserMendelian[String, Boolean, Double](traitMapper)
//  val phenome: Phenome[String, Boolean, Double] = Phenome("test", Map(locusH -> height, locusG -> girth), expresser, attraction)
//  private val sId = """(\w+)-(\w+)-(\p{XDigit}{16})"""
//  private val idR = sId.r
////  private val avagen = new Avagen[Double, Int] {
////    def apply(v1: Individual[Double, Int]): Avatar = new Avatar {
////      def features: Map[String, Any] = Map()
////
////      def name: String = v1.name
////    }
////  }
//  private val listener = new Listener {
//    def receive(sender: AnyRef, msg: Any): Unit = println(s"test listener: sender=$sender, msg=$msg")
//  }
//  val visualizer = new Visualizer[Double, Int](avagen, listener)
//  val species: Species[Base, String, Boolean, Double, Int] = Species("test species", genome, phenome)(visualizer)
//  val environment: Environment[Double, Int] = Environment("test environment", ecology, habitat)
//
//  implicit object organismBuilder extends OrganismBuilder[SexualAdaptedOrganism[Base, String, Double, Long, Int]] {
//    implicit val idStreamer: Streamer[Long] = Streamer(RNG[Long](0).toStream)
//
////    def build[B, G, P, T, V, X](generation: Version[V], species: Species[B, G, P, T, X], nucleus: Nucleus[B], environment: Environment[T, X]): SexualAdaptedOrganism[Base, String, Double, Long, Int] = {
////      species match {
////        case s: Species[B, G, Boolean, T, X]@unchecked =>
////          val organism = SexualAdaptedOrganism[B, G, T, V, X](generation, s, nucleus, environment.ecology)(idStreamer)
////          organism.asInstanceOf[SexualAdaptedOrganism[Base, String, Double, Long, Int]]
////        case _ => throw new GeneticsException(s"build not defined for species $species", null)
////      }
////    }
//  }
//
//  behavior of "Colony"
//
//  it should "render" in {
//    val random = RNG[Base](3L)
//    val colony = Colony("test colony", species, environment).seedMembers(12, random)
//    val rendered = colony.render()
//    println(rendered)
//    val filtered = rendered.replaceAll(sId, "<ID>")
//    filtered shouldBe
//      """Colony(
//  name:"test colony"
//  organisms:(
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      SexualAdaptedOrganism:<ID>,
//      ...[2 more elements]
//    )
//  generation:0
//  species:Species:test species
//  environment:Environment:test environment
//  )"""
//  }
//
//  it should "create an organism" in {
//    val random = RNG[Base](3L)
//    val colony = Colony("test colony", species, environment)
//    val (bn, _) = genome.recombine(random)
//    val organism = colony.createOrganism(bn)
//    organism.nucleus should matchPattern { case _ => }
//    organism.name match {
//      case idR(b, c, d) => println(s"$b-$c-$d")
//      case x => fail(s"invalid ID: $x")
//    }
//  }
//
//  it should "evolve" in {
//    val random = RNG[Base](3L)
//    val colony = Colony("test colony", species, environment).seedMembers(10, random)
//    println(colony)
//    // TODO rework this test
//    val cy = colony.next()
//    cy should matchPattern { case Success(_) => }
//    for (c <- cy) println(c)
//  }
//}
