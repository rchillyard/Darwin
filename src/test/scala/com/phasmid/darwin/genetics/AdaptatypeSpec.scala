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

import com.phasmid.darwin.base.{Audit, IdentifierName, Identifying}
import com.phasmid.darwin.eco._
import com.phasmid.laScala.MockLogger
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class AdaptatypeSpec extends FlatSpec with Matchers {

  private val sElephantGrass = "elephant grass"
  private val elephantGrass: Factor = Factor(sElephantGrass)
  private val factorMap = Map("height" -> elephantGrass)
  private val efElephantGrass = EcoFactor(elephantGrass, 1.6)
  private val habitat = Map("elephant grass" -> efElephantGrass)

  val adapter: Adapter[Double, Double] = new AbstractAdapter[Double, Double]("elephant grass adapter") {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, ShapeFunction[Double, Double])] = f match {
      case `elephantGrass` => t.characteristic.name match {
        case "height" => Success(t.value, ShapeFunction.shapeDiracInv)
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }

  def fitnessFunction(t: Double, functionType: ShapeFunction[Double, Double], x: Double): Fitness = functionType match {
    case ShapeFunction(_, f) => f(x)(t)
    case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $functionType")
  }

  behavior of "adaptation"
  // TODO try to get this working such that the order of firing the constructors doesn't matter
  ignore should "log itself" in {
    Audit.auditing = true
    val sb = new StringBuilder
    val logger = MockLogger("adaptationLogger", "DEBUG", sb)
    Identifying.setLogger(logger)
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
    val ecology: Ecology[Double, Double] = Ecology("test", factorMap, fitnessFunction, adapter)
    val _: Adaptatype[Double] = ecology(phenotype)
    // TODO we should see something about Adaptatype too!
    sb.toString() shouldBe "adaptationLogger: DEBUG: Ecology(\n  name:\"test\"\n  factors:((height,elephant grass))\n  fitnessFunc:<function3>\n  adapter:<function3: elephant grass adapter>\n  )\n"
  }
  it should "render correctly" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
    val ecology: Ecology[Double, Double] = Ecology("test", factorMap, fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    // TODO for some reason the compiler doesn't like this --
    // but, as Galileo might have said: "eppur se muove"
    val sAdaptatype = adaptatype.render()
    println(s"adaptatype: $sAdaptatype")
    val sId = """(\p{XDigit}{16})"""
    val filtered = sAdaptatype.replaceAll(sId, "<ID>")
    filtered shouldEqual "Adaptatype(\n  id:at:<ID>\n  adaptations:(<function1: adaptation for elephant grass>)\n  )"
  }
  it should "yield appropriate fitness" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
    val ecology: Ecology[Double, Double] = Ecology("test", factorMap, fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    val adaptations = adaptatype.adaptations
    val adaptation: Adaptation[Double] = adaptations.head
    adaptation should matchPattern { case Adaptation(`elephantGrass`, _) => }
    val ff: EcoFactor[Double] => Try[Fitness] = adaptation.ecoFitness
    val fy = ff(efElephantGrass)
    fy should matchPattern { case Success(Fitness(_)) => }
    fy.get.x shouldBe 0.0
  }
  behavior of "adaptatype"
  it should "yield appropriate fitness" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
    val ecology: Ecology[Double, Double] = Ecology("test", factorMap, fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    val fy: Try[Fitness] = adaptatype.fitness(habitat)
    fy should matchPattern { case Success(Fitness(_)) => }
    fy.get.x shouldBe 0.0
  }
}
