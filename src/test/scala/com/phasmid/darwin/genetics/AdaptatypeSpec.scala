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

import com.phasmid.darwin.base.IdentifierName
import com.phasmid.darwin.eco._
import org.scalatest.{FlatSpec, Matchers}

import scala.util._
import scala.util.matching.Regex

/**
  * Created by scalaprof on 5/6/16.
  */
class AdaptatypeSpec extends FlatSpec with Matchers {

  private val sElephantGrass = "elephant grass"
  private val elephantGrass: Factor = Factor(sElephantGrass)
  private val factorMap = Map("height" -> elephantGrass)
  private val efElephantGrass = EcoFactor(elephantGrass, 1.6)
  private val ecosystem = Map("elephant grass" -> efElephantGrass)

  val adapter: Adapter[Double, Double] = new AbstractAdapter[Double, Double] {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, FunctionShape[Double, Double])] = f match {
      case `elephantGrass` => t.characteristic.name match {
        case "height" => Success(t.value, FunctionShape.shapeDiracInv)
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }

  def fitnessFunction(t: Double, functionType: FunctionShape[Double, Double], x: Double): Fitness = functionType match {
    case FunctionShape(_, f) => f(x)(t)
    case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $functionType")
  }

  behavior of "adaptation"
  it should "render correctly" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(IdentifierName("test"), Seq(Trait(height, 2.0)))
    val ecology: Ecology[Double, Double] = Ecology("test", factorMap, fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    val adaptatypeR: Regex = """(\p{XDigit}{16})""".r
//    adaptatype.render().split(":").toList.last match {
//      case adaptatypeR(_) =>
//      case x => fail(s"$x didn't match")
//    }
     adaptatype.render().split(":").toList.last should matchPattern { case adaptatypeR(_) => }
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
    val fy: Try[Fitness] = adaptatype.fitness(ecosystem)
    fy should matchPattern { case Success(Fitness(_)) => }
    fy.get.x shouldBe 0.0
  }
}
