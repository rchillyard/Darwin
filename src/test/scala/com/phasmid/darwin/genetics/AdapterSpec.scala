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

import com.phasmid.darwin.eco._
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class AdapterSpec extends FlatSpec with Matchers {

  private val sElephantGrass = "elephant grass"
  private val elephantGrass: Factor = Factor(sElephantGrass)

  def fitnessFunction(t: Double, functionType: ShapeFunction[Double, Double], x: Double): Fitness = functionType match {
    case ShapeFunction(_, f) => f(x)(t)
    case _ => throw GeneticsException(s"ecoFitness does not implement functionType: $functionType")
  }

  behavior of "adapter"
  it should "implement toString" in {
    val adapter: Adapter[Double, Double] = new AbstractAdapter[Double, Double]("elephant grass adapter") {
      def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, ShapeFunction[Double, Double])] = f match {
        case `elephantGrass` => t.characteristic.name match {
          case "height" => Success(t.value, ShapeFunction.shapeDiracInv)
          case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
        }
      }
    }

    adapter.toString() shouldBe "<function1: elephant grass adapter>"
  }
}
