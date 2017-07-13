/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.evolution

import com.phasmid.laScala.values.Rational
import com.phasmid.laScala.{LongRNG, RNG, Version}
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.util.Success

/**
  * Created by scalaprof on 7/25/16.
  */
class EvolvableSpec extends FlatSpec with Matchers with Inside {

  implicit val random: RNG[Long] = LongRNG(0)

  case class MockEvolvable(members: Iterable[Int], v: Version[Int]) extends BaseEvolvable[Int, Int, MockEvolvable](members, v) {

    def evaluateFitness(x: Int): Boolean = x % 2 == 0

    def offspring: Iterator[Int] = members.toIterator filter ( _ > 3 ) map ( _ + 100 )

    def build(xs: Iterator[Int], v: Version[Int]): MockEvolvable = MockEvolvable(xs.toSeq, v)

    override def survivors: Iterator[Int] = super.survivors

    override def -(i: Iterator[Int]): Iterator[Int] = super.-(i)

    override def *(fraction: Rational[Long])(implicit random: RNG[Long]): Iterator[Int] = super.*(fraction)
  }

  "MockEvolvable" should "shuffle properly" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Version(0, None))
    evolvable.permute.toSeq shouldBe Stream(1, 1, 3, 2, 13, 5, 8)
  }
  it should "build properly" in {
    val evolvable = MockEvolvable(Seq(), Version(0, None))
    val x = evolvable.build(Seq(1, 1, 3, 2, 13, 5, 8).iterator, Version(1, None))
    x.iterator.toSeq shouldBe Seq(1, 1, 3, 2, 13, 5, 8)
    x() shouldBe 0
  }
  it should "yield 3 offspring" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Version(0, None))
    evolvable.offspring.toSeq shouldBe Seq(105, 108, 113)
  }
  it should "yield 2 survivors -- the even numbers" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Version(0, None))
    evolvable.survivors.toSeq shouldBe Seq(2, 8)
  }
  it should "yield 5 from * 2/3" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13, 21), Version(0, None))
    (evolvable * Rational(2, 3)).toSeq shouldBe Seq(1, 21, 3, 5, 13)
  }
  it should "retain 3 after subtracting result of * 2/3" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13, 21), Version(0, None))
    val x = evolvable * Rational(2, 3)
    (evolvable - x).toSeq shouldBe Seq(2, 8)
  }
  it should "evolve" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Version(0, None))
    evolvable.version shouldBe Version(0, None, isSnapshot = false)
    val next = evolvable.next()
    next should matchPattern { case Success(MockEvolvable(_, _)) => }
    inside(next) {
      case Success(me) =>
        me.version shouldBe Version(1, None, isSnapshot = false)
        me.members shouldBe Stream(2, 8, 108, 113)
    }
  }
}
