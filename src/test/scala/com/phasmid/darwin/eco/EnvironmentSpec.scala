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

package com.phasmid.darwin.eco

import com.phasmid.darwin.genetics._
import com.phasmid.laScala.fp.NamedFunction3
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
  * Created by scalaprof on 5/6/16.
  */
class EnvironmentSpec extends FlatSpec with Matchers {

  private val sElephantGrass = "elephant grass"
  private val elephantGrass = Factor(sElephantGrass)
  private val factorMap = Map("height" -> elephantGrass)

  val adapter: Adapter[Double, Int] = new AbstractAdapter[Double, Int]("elephant grass adapter") {
    def matchFactors(f: Factor, t: Trait[Double]): Try[(Double, ShapeFunction[Double, Int])] = f match {
      case `elephantGrass` => t.characteristic.name match {
        case "height" => Success((t.value, ShapeFunction.shapeDiracInv_I))
        case _ => Failure(GeneticsException(s"no match for factor: ${t.characteristic.name}"))
      }
    }
  }


  "render" should "work" in {
    val ff = new NamedFunction3[Double, ShapeFunction[Double, Int], Int, Fitness]("shape-only", { (t, fs, x) => fs(x)(t) })
    val ecology = Ecology[Double, Int]("test", factorMap, ff, adapter)
    val ecoFactor: EcoFactor[Int] = EcoFactor(elephantGrass, 1)
    val habitat: Habitat[Int] = Map(sElephantGrass -> ecoFactor)
    val environment = Environment("test", ecology, habitat)
    environment.render() shouldBe "Environment(\n  name:\"test\"\n  ecology:Ecology:test\n  habitat:((elephant grass,elephant grass))\n  )"
    environment.render(1) shouldBe "Environment:test"
  }

}
