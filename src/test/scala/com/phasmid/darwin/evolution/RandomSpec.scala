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

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 7/20/17.
  */
class RandomSpec extends FlatSpec with Matchers {

  case class MockRandom[T: Randomizable](private val l: Long) extends RandomMonadJava[T, MockRandom[T]](l) {
    override def apply(): T = super.apply()

    def build(n: Long): MockRandom[T] = new MockRandom(n)

    def unit[U: Randomizable](u: U): Random[U] = {
      new MockRandom[U](l)
    }
  }

  object MockRandom {
  }

  behavior of "MockRandom.apply"
  it should "work" in {
    import Random.RandomizableBoolean
    val coin = MockRandom(0L)
    coin() shouldBe true
  }

  behavior of "MockRandom.toStream"
  it should "work for Long" in {
    import Random.RandomizableLong
    val coin = MockRandom(0L)
    coin.toStream take 5 shouldBe Seq(-4962768465676381896L, 4804307197456638271L, -1034601897293430941L, 7848011421992302230L, -8929183248358367000L)
  }

  it should "work for Boolean" in {
    import Random.RandomizableBoolean
    val coin = MockRandom(0L)
    coin.toStream take 5 shouldBe Seq(true, false, false, true, true)
  }

  behavior of "MockRandom.map"
  it should "work for String" in {
    import Random.RandomizableString
    val coin = MockRandom(0L)
    val x = coin.map(b => b.toString)
    x() shouldBe "0"
  }

  behavior of "RNG.toStream"
  it should "work for Long" in {
    import Random.RandomizableLong
    val coin = RNG(0L)
    coin.toStream take 5 shouldBe Seq(-4962768465676381896L, 4804307197456638271L, -1034601897293430941L, 7848011421992302230L, -8929183248358367000L)
  }

  it should "work for Boolean" in {
    import Random.RandomizableBoolean
    val coin = RNG(0L)
    coin.toStream take 5 shouldBe Seq(true, false, false, true, true)
  }

  behavior of "RNG.map"
  it should "work for String" in {
    import Random.RandomizableString
    val coin = RNG(0L)
    val x = coin.map(b => b.toString)
    x() shouldBe "0"
  }

}


