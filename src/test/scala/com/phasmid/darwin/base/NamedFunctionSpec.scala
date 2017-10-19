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

package com.phasmid.darwin.base

import org.scalatest.{FlatSpec, Matchers}

class NamedFunctionSpec extends FlatSpec with Matchers {

  behavior of "NamedFunction"
  it should "yield correct name" in {
    val f: NamedFunction[Int, Int] = new NamedFunction("test", identity)
    f.toString shouldBe "<function1: test>"
  }
  it should "apply correctly" in {
    def double(x: Int): Int = 2 * x

    val f: NamedFunction[Int, Int] = new NamedFunction("double", double)
    f(1) shouldBe 2
  }
  it should "unapply correctly" in {
    def double(x: Int): Int = 2 * x

    val f: NamedFunction[Int, Int] = new NamedFunction("double", double)
    f match {
      case NamedFunction("double", g) => g(2) shouldBe 4
      case _ => fail
    }
  }
  it should "compose with fDouble" in {
    def fDouble(x: Int): Int = 2 * x

    val f = new NamedFunction("fDouble", fDouble)
    val g = f.compose(fDouble)
    g(1) shouldBe 4
    g.toString shouldBe "<function1: fDouble&&&<function1>>"
  }
  it should "compose with itself" in {
    def fDouble(x: Int): Int = 2 * x

    val f = new NamedFunction("fDouble", fDouble)
    val g = f.compose(f)
    g(1) shouldBe 4
    g.toString shouldBe "<function1: fDouble&&&fDouble>"
  }

  behavior of "NamedFunction0"
  it should "yield correct name" in {
    def x(): Int = 1

    val f: NamedFunction0[Int] = new NamedFunction0("test", x)
    f.toString shouldBe "<function0: test>"
  }
  it should "apply correctly" in {
    def x(): Int = 1

    val f: NamedFunction0[Int] = new NamedFunction0("test", x)
    f() shouldBe 1
  }
  it should "unapply correctly" in {
    def x(): Int = 1

    val f: NamedFunction0[Int] = new NamedFunction0("test", x)
    f match {
      case NamedFunction0("test", g) => g() shouldBe 1
      case _ => fail
    }
  }

  behavior of "NamedFunction2"
  it should "yield correct name" in {
    def x(v: Int, y: Int): Double = Math.pow(v, y)

    val f: NamedFunction2[Int, Int, Double] = new NamedFunction2("test", x)
    f.toString shouldBe "<function2: test>"
  }
  it should "apply correctly" in {
    def x(v: Int, y: Int): Double = Math.pow(v, y)

    val f: NamedFunction2[Int, Int, Double] = new NamedFunction2("test", x)
    f(2, 3) shouldBe 8.0 +- 0.001
  }
  it should "unapply correctly" in {
    def x(v: Int, y: Int): Double = Math.pow(v, y)

    val f: NamedFunction2[Int, Int, Double] = new NamedFunction2("test", x)
    f match {
      case NamedFunction2("test", g) => g(2, 3) shouldBe 8.0 +- 0.001
      case _ => fail
    }
  }
  it should "curry correctly" in {
    def x(v: Int, y: Int): Double = Math.pow(v, y)

    val f: NamedFunction2[Int, Int, Double] = new NamedFunction2("test", x)
    val g = f.curried
    g.toString shouldBe "<function1: test!!!>"
    g match {
      case _: Function1[_, _] =>
      case _ => fail
    }
    g(2)(3) shouldBe 8.0 +- 0.001
    g match {
      case NamedFunction(w, h) =>
        w shouldBe "test!!!"
        h(2)(3) shouldBe 8.0 +- 0.001
      case _ => fail
    }
  }
  it should "tuple correctly" in {
    def x(v: Int, y: Int): Double = Math.pow(v, y)

    val f: NamedFunction2[Int, Int, Double] = new NamedFunction2("test", x)
    val g = f.tupled
    g.toString shouldBe "<function1: test###>"
    g match {
      case _: Function1[_, _] =>
      case _ => fail
    }
    g((2, 3)) shouldBe 8.0 +- 0.001
    g match {
      case NamedFunction(w, h) =>
        w shouldBe "test###"
        h((2, 3)) shouldBe 8.0 +- 0.001
      case _ => fail
    }
  }

  behavior of "NamedFunction3"
  it should "yield correct name" in {
    def x(s: String, v: Int, y: Int): Double = s.toDouble * Math.pow(v, y)

    val f = new NamedFunction3("test", x)
    f.toString shouldBe "<function3: test>"
  }
  it should "apply correctly" in {
    def x(s: String, v: Int, y: Int): Double = s.toDouble * Math.pow(v, y)

    val f = new NamedFunction3("test", x)
    f("10", 2, 3) shouldBe 80.0 +- 0.001
  }
  it should "unapply correctly" in {
    def x(s: String, v: Int, y: Int): Double = s.toDouble * Math.pow(v, y)

    val f = new NamedFunction3("test", x)
    f match {
      case NamedFunction3("test", g) => g("10", 2, 3) shouldBe 80.0 +- 0.001
      case _ => fail
    }
  }
  it should "curry correctly" in {
    def x(s: String, v: Int, y: Int): Double = s.toDouble * Math.pow(v, y)

    val f = new NamedFunction3("test", x)
    val g = f.curried
    g.toString shouldBe "<function1: test!!!>"
    g match {
      case _: Function1[_, _] =>
      case _ => fail
    }
    g("10")(2)(3) shouldBe 80.0 +- 0.001
    g match {
      case NamedFunction(w, h) =>
        w shouldBe "test!!!"
        h("10")(2)(3) shouldBe 80.0 +- 0.001
      case _ => fail
    }
  }
  it should "tuple correctly" in {
    def x(s: String, v: Int, y: Int): Double = s.toDouble * Math.pow(v, y)

    val f = new NamedFunction3("test", x)
    val g = f.tupled
    g.toString shouldBe "<function1: test###>"
    g match {
      case _: Function1[_, _] =>
      case _ => fail
    }
    g(("10", 2, 3)) shouldBe 80.0 +- 0.001
    g match {
      case NamedFunction(w, h) =>
        w shouldBe "test###"
        h(("10", 2, 3)) shouldBe 80.0 +- 0.001
      case _ => fail
    }
  }
}
