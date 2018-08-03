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

import com.phasmid.darwin.eco.{Environment, Fit, Fitness, Survival}
import com.phasmid.darwin.genetics.Reproduction
import com.phasmid.laScala.fp.{FP, Named}
import com.phasmid.laScala.values.{Incrementable, Rational}
import com.phasmid.laScala.{Sequential, Version}

import scala.language.implicitConversions
import scala.util.Try

/**
  * Trait which defines the behavior of something which can be evolved.
  * Here are its properties:
  * (1) it has a fitness evaluator which will evaluate an individual Z and yield a Boolean representing its survivability;
  * (2) it is able to create an iterator of offspring for a subsequent generation;
  * (3) it can be compared with another Evolvable determine which is junior/senior;
  * (4) it can create an iterator which is a randomly permuted copy if its elements.
  *
  * In practice, evolvable objects usually implement a sub-trait called SequentialEvolvable (see below).
  *
  * @tparam Z the underlying type of the members of this Evolvable -- this type must implement Individual
  *
  *           Created by scalaprof on 7/27/16.
  */
trait Evolvable[Z] extends Permutable[Z] {

//  /**
//    * This method yields a new set of Z objects, by reproduction.
//    * If the ploidy of Z is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
//    * All members of this Evolvable take part in reproduction, as deceased organisms have already been eliminated from this.
//    *
//    * @return a new Evolvable
//    */
//  def offspring: Iterable[Z]
//
//  /**
//    * This method yields an iterable of members which will die this generation.
//    * @return
//    */
//  def terminal: Iterable[Z]
}

/**
  * This trait captures the essence of something which undergoes successive generations
  *
  * @tparam V the generation type that identifies successive generations
  */
trait Generational[V] {

  /**
    * Method to get this object's generation, as a Version.
    *
    * @return the version for this generation.
    */
  def generation: Version[V]
}

abstract class BaseGenerational[V, Repr](vv: Version[V]) extends Generational[V] with Sequential[Repr] {
  /**
    * Method to get the generation
    *
    * @return vv.
    */
  def generation: Version[V] = vv

//  /**
//    * By default, this implementation of next allows k non-survivors to mate (before they are killed, presumably).
//    *
//    * @return a new generation of this Evolvable
//    */
//  def next(isSnapshot: Boolean = false): Try[Repr] = for (vy: Try[Repr] <- vv.next(isSnapshot); v: Repr <- vy) yield next(v)

  /**
    * Method to create the next generation
    *
    * @param v the Version for the next generation
    * @return the next generation as a Repr, wrapped in Try
    */
  def next(v: Version[V]): Try[Repr]
}

/**
  * TODO remove Evolvable (replace with Permutable)
  *
  * This trait captures the essence of an Evolvable which undergoes a sequence of generations.
  * You might think of this as normal, natural evolution without skipping any generations
  * (i.e. this does not model punctuated equilibrium).
  *
  * In addition to its properties arising from Evolvable (one of its super-traits), it has the following:
  * (1) it implements Iterable (i.e. can create an iterator on its members);
  * (2) it defines a method to build a new value of Repr given an collection of Xs and a Version;
  * (3) it implements Sequential so that it supports the method next(isSnapshot: Boolean);
  * (4) it defines a generation.
  *
  * @tparam Z    the underlying type of the individuals which make up this evolvable
  * @tparam V    the generation type that identifies successive generations
  * @tparam Repr the result type of calling next (defined in Sequential)
  */
trait SequentialEvolvable[Z, V, Repr] extends Evolvable[Z] with Sequential[Repr] with Iterable[Z] with Generational[V] {

  /**
    * Method to create a new concrete instance of this BaseEvolvable.
    *
    * CONSIDER using CanBuildFrom
    *
    * @param zs the sequence of Zs to be included in this SequentialEvolvable
    * @param v  a Version
    * @return a concrete instance of SequentialEvolvable corresponding to the same type as this
    */
  def build(zs: Evolute[Z])(v: Version[V]): Repr
}

/**
  * This abstract base class for Evolvable represents a Seq of Z objects which, together, are Evolvable.
  *
  * When migrating to Scala 3, rewrite this as a trait.
  *
  * @param ze      an unsorted collection of objects which, together, are Evolvable
  * @param vv      a Version (Evolvables which are not complete generations use a snapshot here)
  * @tparam V the generation type (defined to be Incrementable)
  * @tparam Z the underlying type of the zs (must implement Individual)
  */
abstract class BaseEvolvable[V: Incrementable, Z : Fit : Reproduction, Repr](ze: Evolute[Z], vv: Version[V]) extends BaseGenerational[V, Repr](vv) with SequentialEvolvable[Z, V, Repr] {

  def next(v: Version[V]): Try[Repr] = doNext(v)

  /**
    * @return an Iterator based on the individual members of this Evolvable
    */
  def iterator: Iterator[Z] = ze.iterator

  /**
    * Method to compare this Evolvable with that Evolvable.
    *
    * @param that the Evolvable we want to compare with (must extend BaseEvolvable).
    * @return the result of comparing this generation with that generation. All other attributes are ignored.
    */
  def compare(that: Evolvable[Z]): Int = that match {
    case e: BaseEvolvable[V, Z, Repr]@unchecked => this.vv.compare(e.generation)
    case _ => throw EvolutionException(s"cannot compare $that with $this")
  }

  private def doNext(v: Version[V]): Try[Repr] = {
    val f: Try[Evolute[Z]] => Try[Repr] = FP.lift(build(_)(v))
    f(ze.next())
  }

//  private def buildInternal(zet: Try[Evolute[Z]], v: Version[V]): BaseEvolvable[V, Z, Repr] = build(xs).asInstanceOf[BaseEvolvable[V, Z, Repr]]

  /**
    * Evaluate the fitness of a member of this Evolvable in the given environment
    *
    * @param z the member
    *          @param environment the environment in which z's fitness will be measured.
    * @return true if z is fit enough to survive this generation
    */
  def evaluateFitness[T,X](z: Z, environment: Environment[T, X]): Boolean = implicitly[Fit[Z]].isFit(z, environment)

//  private def nextGeneration: Evolute[Z] =
//  {
//    val next: Try[Evolute[Z]] = ze.next()
//    buildInternal(next)
////    // TODO in sexual reproduction, we need to introduce to s a small number (possibly zero) of members of a distinct Evolvable
////    // such as a rival Colony. Typically, these additions, if any, will be males.
////    val (s, n) = (buildInternal(survivors, v), buildInternal(nonSurvivors, v))
////    implicit val random: RNG[Long] = r(this)
////    // TODO need to fix this because, currently, sexual reproduction will be from pairs
////    // chosen from two different populations: survivors and non-survivors.
////    (s.iterator ++ s.offspring ++ buildInternal(n * k(this), v).offspring).toSeq.distinct
//  }


}

object Evolvable {
  /**
    * This is the rate at which non-survivors can yet have offspring
    */
  implicit def k[Z](e: Evolvable[Z]): Rational[Long] = Rational.half

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  /**
    * This is the rate at which non-survivors can yet have offspring
    *
    * TODO Huh??
    *
    */
  implicit def random[Z](e: Evolvable[Z]): RNG[Long] = RNG[Long](0L)
}

case class EvolvableException(s: String) extends Exception(s)

