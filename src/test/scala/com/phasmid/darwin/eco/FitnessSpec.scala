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

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 7/14/17.
  */
class FitnessSpec extends FlatSpec with Matchers {

  import Fitness.{nonViable, tossup, viable}

  behavior of "apply"
  it should "work for Fitness(1), etc." in {
    Fitness(1)() shouldBe 1
    Fitness(0)() shouldBe 0
  }

  behavior of "Fitness.apply"
  it should "work for numbers 0 through 1" in {
    val fs = Seq(Fitness(0), Fitness(0.5), Fitness(1))
    fs.length shouldBe 3
  }
  it should "not work for numbers outside range" in {
    a[IllegalArgumentException] shouldBe thrownBy(Fitness(-0.1))
    a[IllegalArgumentException] shouldBe thrownBy(Fitness(1.1))
  }

  behavior of "&"
  it should "work" in {
    viable & viable shouldBe viable
    viable & nonViable shouldBe nonViable
    tossup & viable shouldBe tossup
    tossup & tossup shouldBe viable / 4
  }

  behavior of "/"
  it should "work" in {
    (viable / 2) () shouldBe 1.0 / 2
    a[IllegalArgumentException] shouldBe thrownBy(viable / 0.5)
  }

  behavior of "-"
  it should "work" in {
    viable.-() shouldBe 0
    Fitness(0).-() shouldBe 1
  }

  behavior of "Viability.create"
  it should "work" in {
    Viability.create(viable, viable) shouldBe Viability(Seq(viable, viable))
    Viability.create(viable, nonViable) shouldBe Viability(Seq(viable, nonViable))
    Viability.create(tossup, viable) shouldBe Viability(Seq(tossup, viable))
    Viability.create(tossup, tossup) shouldBe Viability(Seq(tossup, tossup))
  }

  behavior of "Viability"
  it should "work" in {
    Viability(Seq(viable, viable))()() shouldBe 1
    Viability(Seq(viable, nonViable))()() shouldBe 0
    Viability(Seq(tossup, viable))()() should ===(0.7071067811865476 +- 1E-10)
    Viability(Seq(tossup, tossup))()() should ===(0.5 +- 1E-10)
  }

  behavior of "FunctionShape"
  private val logistic1 = 0.7310585786300049
  private val logistic_1 = 0.2689414213699951
  it should "have correct shape names" in {
    FunctionShape.shapeDirac.name shouldBe "shapeDirac"
    FunctionShape.shapeDiracInv.name shouldBe "shapeDirac-i"
    FunctionShape.shapeLogistic.name shouldBe "shapeLogistic"
    FunctionShape.shapeLogisticInv.name shouldBe "shapeLogistic-i"
  }
  it should "implement Dirac properly" in {
    FunctionShape.dirac(1, 0) shouldBe viable
    FunctionShape.dirac(0, 0) shouldBe viable
    FunctionShape.dirac(0, 1) shouldBe nonViable
  }
  it should "implement Logistic properly with unit k" in {
    val k = 1.0
    FunctionShape.logistic(k)(100, 0) shouldBe viable
    FunctionShape.logistic(k)(10, 0)() should ===(0.9999546021312976 +- 1E-13)
    FunctionShape.logistic(k)(2, 0)() should ===(0.8807970779778823 +- 1E-13)
    FunctionShape.logistic(k)(1, 0)() should ===(logistic1 +- 1E-13)
    FunctionShape.logistic(k)(0, 1)() should ===(logistic_1 +- 1E-13)
    FunctionShape.logistic(k)(0, 2)() should ===(0.11920292202211755 +- 1E-13)
    FunctionShape.logistic(k)(0, 10)() should ===(4.5397868702434395E-5 +- 1E-20)
    FunctionShape.logistic(k)(0, 100) shouldBe nonViable
    FunctionShape.logistic(k)(0, 0) shouldBe tossup
  }
  it should "implement Logistic properly with k" in {
    val k = 2.0
    FunctionShape.logistic(k)(100, 0) shouldBe viable
    FunctionShape.logistic(k)(10, 0)() should ===(0.9933071490757153 +- 1E-13)
    FunctionShape.logistic(k)(2, 0)() should ===(logistic1 +- 1E-13)
    FunctionShape.logistic(k)(1, 0)() should ===(0.6224593312018546 +- 1E-13)
    FunctionShape.logistic(k)(0, 1)() should ===(0.3775406687981454 +- 1E-13)
    FunctionShape.logistic(k)(0, 2)() should ===(logistic_1 +- 1E-13)
    FunctionShape.logistic(k)(0, 10)() should ===(0.0066928509242848554 +- 1E-20)
    FunctionShape.logistic(k)(0, 100) shouldBe nonViable
    FunctionShape.logistic(k)(0, 0) shouldBe tossup
  }
  it should "have correct function operation" in {
    val x = 1
    val t = 2.0
    FunctionShape.shapeDirac_I.f(x)(t) shouldBe viable
    FunctionShape.shapeDiracInv_I.f(x)(t) shouldBe nonViable
    FunctionShape.shapeLogistic_I.f(x)(t)() should ===(logistic1 +- 1E-13)
    FunctionShape.shapeLogisticInv_I.f(x)(t)() should ===(logistic_1 +- 1E-13)
  }
}
