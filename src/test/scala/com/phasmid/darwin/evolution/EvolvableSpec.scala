///*
// * DARWIN Genetic Algorithms Framework Project.
// * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
// *
// * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
// * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
// *
// *      This file is part of Darwin.
// *
// *      Darwin is free software: you can redistribute it and/or modify
// *      it under the terms of the GNU General Public License as published by
// *      the Free Software Foundation, either version 3 of the License, or
// *      (at your option) any later version.
// *
// *      This program is distributed in the hope that it will be useful,
// *      but WITHOUT ANY WARRANTY; without even the implied warranty of
// *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *      GNU General Public License for more details.
// *
// *      You should have received a copy of the GNU General Public License
// *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.phasmid.darwin.evolution
//
//import com.phasmid.darwin.base.Auditable
//import com.phasmid.darwin.eco.{Environment, Fit, Fitness}
//import com.phasmid.laScala.values.Rational
//import com.phasmid.laScala.{Prefix, Renderable, Version}
//import org.scalatest.{FlatSpec, Inside, Matchers}
//
//import scala.util.{Success, Try}
//
///**
//  * Created by scalaprof on 7/25/16.
//  */
//class EvolvableSpec extends FlatSpec with Matchers with Inside {
//
//  case class Member(x: Int) {
////    def name = s""""${x.toString}""""
//
//    // NOTE: this is very arbitrary
//    def fitness[T,X](environment: Environment[T, X]) = Success(Fitness(1 - (x % 2)))
//  }
//
//  implicit object IndividualMember extends Fit[Member] {
//    /**
//      * CONSIDER changing the parameters to this method if we can find them more simply
//      *
//      * @param individual  the organism whose fitness we are interested in
//      * @param environment the Environment in which this Individual thrives
//      * @return the Fitness of the Individual in the ecology, wrapped in Try
//      */
//    override def fitness[T, X](individual: Member, environment: Environment[T, X]): Try[Fitness] = individual.fitness(environment)
//
//    /**
//      * Method which determines if a particular Fitness value will be considered sufficiently fit to survive this generation
//      *
//      * @param f a Fitness value
//      * @return true if f represents a sufficiently fit value to survive to the next generation
//      */
//    def isFit(f: Fitness): Boolean = f.x >= 0.5
//  }
//
//  import com.phasmid.darwin.evolution.Random.RandomizableLong
//
//  implicit val random: RNG[Long] = RNG[Long](0)
//
//  /**
//    * CONSIDER use CaseIdentifiable
//    *
//    * @param members the members of this evolvable
//    * @param v       the version (generation) of this evolvable
//    */
//  case class MockEvolvable(members: Iterable[Member], v: Version[Int]) extends BaseEvolvable[Int, Member, MockEvolvable](members, v) with Auditable {
//
//    def evaluateFitness(x: Member): Boolean = x.fitness(null) match {
//      case Success(f) => f() > 0.5
//      case _ => false
//    }
//
//    def offspring: Iterator[Member] = members.toIterator filter (_.x > 3) map (_.x + 100) map Member.apply
//
//    def build(xs: Iterable[Member], v: Version[Int]): MockEvolvable = MockEvolvable(xs.toSeq, v)
//
//    protected[EvolvableSpec] override def survivors: Iterable[Member] = super.survivors
//
//    protected[EvolvableSpec] override def -(i: Iterable[Member]): Iterable[Member] = super.-(i)
//
//    protected[EvolvableSpec] override def *(fraction: Rational[Long])(implicit random: RNG[Long]): Iterable[Member] = super.*(fraction)
//
//
//    /**
//      * This method should normally be overridden
//      *
//      * @param indent the indentation
//      * @param tab    the tabulator
//      * @return the result
//      */
//    override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = {
//      val sb = new StringBuilder(s"MockEvolvable(")
//      sb.append(nl(indent + 1) + "generation:" + Renderable.renderElem(generation, indent + 2))
//      sb.append(nl(indent + 1) + "members:" + Renderable.renderElem(members, indent + 2))
//      sb.append(")")
//      sb.toString()
//    }
//
//
//  }
//
//  val fibonacci = Seq(Member(1), Member(1), Member(2), Member(3), Member(5), Member(8), Member(13))
//  val permutedMembers = Stream(Member(1), Member(1), Member(3), Member(2), Member(13), Member(5), Member(8))
//  "MockEvolvable" should "shuffle properly" in {
//    val evolvable = MockEvolvable(fibonacci, Version(0, None))
//    evolvable.permute.toSeq shouldBe permutedMembers
//  }
//  it should "render" in {
//    val evolvable = MockEvolvable(fibonacci, Version(0, None))
//    evolvable.render() shouldBe
//      """MockEvolvable(
//  generation:0
//  members:(
//        "1",
//        "1",
//        "2",
//        "3",
//        "5",
//        "8",
//        "13"
//      ))"""
//  }
//
//  it should "build properly" in {
//    val evolvable = MockEvolvable(Seq(), Version(0, None))
//    val x = evolvable.build(Seq(Member(1), Member(1), Member(3), Member(2), Member(13), Member(5), Member(8)), Version(1, None))
//    x.iterator.toSeq shouldBe permutedMembers
//    x.generation() shouldBe 1
//  }
////  it should "yield 3 offspring" in {
////    val evolvable = MockEvolvable(fibonacci, Version(0, None))
////    evolvable.offspring.toSeq shouldBe Seq(Member(105), Member(108), Member(113))
////  }
////  it should "yield 2 survivors -- the even numbers" in {
////    val evolvable = MockEvolvable(fibonacci, Version(0, None))
////    evolvable.survivors shouldBe Seq(Member(2), Member(8))
////  }
//  it should "yield 5 from * 2/3" in {
//    val evolvable = MockEvolvable(fibonacci :+ Member(21), Version(0, None))
//    (evolvable * Rational(2, 3)).toSeq shouldBe Seq(Member(1), Member(21), Member(3), Member(5), Member(13))
//  }
//  it should "retain 3 after subtracting result of * 2/3" in {
//    val evolvable = MockEvolvable(fibonacci :+ Member(21), Version(0, None))
//    val x: Iterable[Member] = evolvable * Rational(2, 3)
//    (evolvable - x).toSeq shouldBe Seq(Member(2), Member(8))
//  }
//  it should "evolve" in {
//    val evolvable = MockEvolvable(fibonacci, Version(0, None))
//    evolvable.generation shouldBe Version(0, None, isSnapshot = false)
//    val next = evolvable.next()
//    next should matchPattern { case Success(MockEvolvable(_, _)) => }
//    inside(next) {
//      case Success(me) =>
//        me.generation shouldBe Version(1, None, isSnapshot = false)
//        me.members shouldBe Stream(Member(2), Member(8), Member(108), Member(113))
//    }
//  }
//}
