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

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class EcologySpec extends FlatSpec with Matchers {

  val adapter: Adapter[Double, Double] = new AbstractAdapter[Double, Double] {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, FunctionShape[Double, Double])] = f match {
      case Factor("elephant grass") => t.characteristic.name match {
        case "height" => Success(t.value, Fitness.inverseDelta)
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }

  def fitnessFunction(t: Double, functionType: FunctionShape[Double, Double], x: Double): Fitness = functionType match {
    case FunctionShape(_, f) => f(t, x)
    case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $functionType")
  }

  "apply" should "work" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(Seq(Trait(height, 2.0)))
    val elephantGrass = Factor("elephant grass")
    val ecology: Ecology[Double, Double] = Ecology("test", Map("height" -> elephantGrass), fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    val adaptations = adaptatype.adaptations
    adaptations.size shouldBe 1
    adaptations.head should matchPattern { case Adaptation(Factor("elephant grass"), _) => }
    val fitness = adaptations.head.ecoFitness(EcoFactor(elephantGrass, 1.6))
    fitness should matchPattern { case Success(Fitness(_)) => }
    fitness.get.x shouldBe 0.0
  }
}
