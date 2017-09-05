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
class WheelSpec extends FlatSpec with Matchers {

  behavior of "apply"
  it should "work for one coin flip" in {
    import Wheel.RandomizableBoolean
    val coinFlip = Wheel(Seq[Boolean](true, false), Seq(1, 1))(0L)
    coinFlip() shouldBe true
  }
  it should "work for bent coin" in {
    import Random._
    val bentCoin = Wheel(Seq[Boolean](true, false), Seq(1, 2))(0L)
    bentCoin.revCumOdds shouldBe List(3L, 1L, 0L)
    bentCoin() shouldBe true
    bentCoin.next() shouldBe true
    bentCoin.next.next() shouldBe false
    bentCoin.next.next.next() shouldBe false
    bentCoin.next.next.next.next() shouldBe true
    bentCoin.next.next.next.next.next() shouldBe false
    bentCoin.next.next.next.next.next.next() shouldBe false
    bentCoin.next.next.next.next.next.next.next() shouldBe true
    bentCoin.next.next.next.next.next.next.next.next() shouldBe false
    bentCoin.next.next.next.next.next.next.next.next.next() shouldBe false
    bentCoin.next.next.next.next.next.next.next.next.next.next() shouldBe false
  }
  it should "work for several fibonacci values" in {
    import Random._
    val fibonacci = Wheel(Seq[Int](0, 1, 2, 3, 4), Seq(1, 1, 2, 5, 8))(0L)
    println(fibonacci)
    fibonacci() shouldBe 0
    fibonacci.next() shouldBe 3
    fibonacci.next.next() shouldBe 4
    fibonacci.next.next.next() shouldBe 4
    fibonacci.next.next.next.next() shouldBe 4
    fibonacci.next.next.next.next.next() shouldBe 3
    fibonacci.next.next.next.next.next.next() shouldBe 2
  }

  behavior of "toStream"
  it should "work for coin flip" in {
    import Wheel.RandomizableBoolean
    val coinFlip = Wheel(Seq[Boolean](true, false), Seq(1, 1))(0L)
    coinFlip.toStream take 5 shouldBe Seq(true, false, false, true, true)
  }
  it should "work for fibonacci" in {
    import Random._
    val fibonacci = Wheel(Seq[Int](0, 1, 2, 3, 4), Seq(1, 1, 2, 5, 8))(0L)
    val values = (fibonacci.toStream take 20).toList
    println(values)
    values count (_ == 0) shouldBe (1 +- 1)
    values count (_ == 1) shouldBe (1 +- 1)
    values count (_ == 2) shouldBe (2 +- 1)
    values count (_ == 3) shouldBe (5 +- 1)
    values count (_ == 4) shouldBe (8 +- 2)
  }

  trait Hand extends Ordered[Hand] {
    val name: String
    val frequency: Int

    override def toString: String = name

    def compare(that: Hand): Int = frequency - that.frequency
  }

  case object RoyalFlush extends Hand { val name = "Royal Flush"; val frequency = 4 }

  case object StraightFlush extends Hand { val name = "Straight Flush"; val frequency = 36 }

  case object Quads extends Hand { val name = "Four of a kind"; val frequency = 624 }

  case object FullHouse extends Hand { val name = "Full House"; val frequency = 3744 }

  case object Flush extends Hand { val name = "Flush"; val frequency = 5108 }

  case object Straight extends Hand { val name = "Straight"; val frequency = 10200 }

  case object Trips extends Hand { val name = "Three of a kind"; val frequency = 54912 }

  case object TwoPair extends Hand { val name = "Two Pair"; val frequency = 123552 }

  case object Pair extends Hand { val name = "Pair"; val frequency = 1098240 }

  case object HighCard extends Hand { val name = "High Card"; val frequency = 1302540 }

  object Hand {
    val hands: Seq[Hand] = Seq(RoyalFlush, StraightFlush, Quads, FullHouse, Flush, Straight, Trips, TwoPair, Pair, HighCard)

    trait RandomizableHand extends Randomizable[Hand] {
      def fromLong(l: Long): Hand = Hand.hands((l % Hand.hands.length).toInt)

      def toLong(x: Hand): Long = Hand.hands.indexOf(x) match {
        case -1 => throw EvolutionException(s"logic error: can't find $x")
        case i => i
      }
    }

    implicit object RandomizableHand extends RandomizableHand

  }

  behavior of "poker game"
  it should "create proper Wheel" in {
    import Hand.RandomizableHand
    val wheel = Wheel(Hand.hands, Hand.hands map (_.frequency.toLong))(0L)
    wheel.revCumOdds shouldBe List(2598960, 1296420, 198180, 74628, 19716, 9516, 4408, 664, 40, 4, 0)
    wheel.ts shouldBe List(RoyalFlush, StraightFlush, Quads, FullHouse, Flush, Straight, Trips, TwoPair, Pair, HighCard)
  }

  it should "give the appropriate frequencies of hands" in {
    import Hand.RandomizableHand
    val wheel = Wheel(Hand.hands, Hand.hands map (_.frequency.toLong))()
    val hands: List[Hand] = (wheel.toStream take 649740).toList
    hands count (_.name == "Royal Flush") shouldBe (1 +- 3)
    hands count (_.name == "Straight Flush") shouldBe (9 +- 10)
    hands count (_.name == "Four of a kind") shouldBe (156 +- 42)
    hands count (_.name == "Full House") shouldBe (936 +- 72)
    hands count (_.name == "Flush") shouldBe (1277 +- 100)
    hands count (_.name == "Straight") shouldBe (2550 +- 150)
    hands count (_.name == "Three of a kind") shouldBe (13728 +- 300)
    hands count (_.name == "Two Pair") shouldBe (30888 +- 400)
    hands count (_.name == "Pair") shouldBe (274560 +- 1100)
    hands count (_.name == "High Card") shouldBe (325635 +- 1250)
  }
}
