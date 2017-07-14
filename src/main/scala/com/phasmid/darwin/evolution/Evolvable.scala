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

import com.phasmid.laScala.values.{Incrementable, Rational}
import com.phasmid.laScala.{LongRNG, RNG, Sequential, Version}

import scala.language.implicitConversions
import scala.util.Try

/**
  * Trait which defines the behavior of something which can be evolved.
  * Here are its properties:
  * (1) it has a fitness evaluator which will evaluate an individual X and yield a Boolean representing its survivability;
  * (2) it is able to create an iterator of offspring for a subsequent generation;
  * (3) it can be compared with another Evolvable determine which is junior/senior;
  * (4) it can create an iterator which is a randomly permuted copy if its elements.
  *
  * In practice, evolvable objects usually implement a sub-trait called SequentialEvolvable (see below).
  *
  * @tparam X the underlying type of the members of this Evolvable
  *
  * Created by scalaprof on 7/27/16.
  */
trait Evolvable[X] extends Ordered[Evolvable[X]] with Permutable[X] {

  /**
    * TODO make this an implicit function in Evolvable sub-class
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param x the member
    * @return true if x is fit enough to survive this generation
    */
  def evaluateFitness(x: X): Boolean

  /**
    * This method yields a new Evolvable by reproduction.
    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    *
    * @return a new Evolvable
    */
  def offspring: Iterator[X]
}

/**
  * This trait captures the essence of an Evolvable which undergoes a sequence of generations.
  * You might think of this as normal, natural evolution without skipping any generations
  * (i.e. this does not model punctuated equilibrium).
  *
  * In addition to its properties arising from Evolvable (one of its super-traits), it has the following:
  * (1) it implements Iterable (i.e. can create an iterator on its members);
  * (2) it defines a method to build a new value of Repr given an collection of Xs and a Version;
  * (3) it implements Sequential so that it supports the method next(isSnapshot: Boolean);
  * (4) it defines a version.
  *
  * @tparam X    the underlying type of the individuals which make up this evolvable
  * @tparam V    the version type that identifies successive generations
  * @tparam Repr the result type of calling next (defined in Sequential)
  */
trait SequentialEvolvable[X, V, Repr] extends Evolvable[X] with Sequential[Repr] with Iterable[X] {

  /**
    * Method to create a new concrete instance of this BaseEvolvable.
    *
    * CONSIDER using CanBuildFrom
    *
    * @param xs the xs to be included in this BaseEvolvable
    * @param v  a Version
    * @return a concrete instance of BaseEvolvable corresponding to the same type as this
    */
  def build(xs: Iterator[X], v: Version[V]): Repr

  /**
    * Method to get this object's version.
    *
    * @return the version.
    */
  def version: Version[V]
}

/**
  * This abstract base class for Evolvable represents a Seq of X objects which, together, are Evolvable.
  *
  * @param members an unsorted collection of objects which, together, are Evolvable
  * @param vv      a Version (Evolvables which are not complete generations use a snapshot here)
  * @tparam V the version type (defined to be Incrementable)
  * @tparam X the underlying type of the xs
  */
abstract class BaseEvolvable[V: Incrementable, X, Repr](members: Iterable[X], vv: Version[V]) extends SequentialEvolvable[X, V, Repr] {

  /**
    * Method to get the version
    *
    * @return vv.
    */
  def version: Version[V] = vv

  /**
    * @return an Iterator based on the individual members of this Evolvable
    */
  def iterator: Iterator[X] = members.iterator

  /**
    * By default, this implementation of next allows k non-survivors to mate (before they are killed, presumably).
    *
    * @return a new generation of this Evolvable
    */
  def next(isSnapshot: Boolean = false): Try[Repr] = for (v <- vv.next(isSnapshot)) yield next(v)

  /**
    * Method to compare this Evolvable with that Evolvable.
    *
    * @param that the Evolvable we want to compare with (must extend BaseEvolvable).
    * @return the result of comparing this version with that version. All other attributes are ignored.
    */
  def compare(that: Evolvable[X]): Int = that match {
    case e: BaseEvolvable[V, X, Repr]@unchecked => this.vv.compare(e.version)
    case _ => throw EvolutionException(s"cannot compare $that with $this")
  }

  /**
    * This method yields an iterator from the elements of xs which survive this generation.
    * Note that, although the default implementation simply culls all the unfit xs
    * and keeps all of the fit xs, sub-classes may redefine this method to allow a random choice
    * of survivors which is only partially guided by fitness.
    *
    * CONSIDER do we really need this method, as opposed to survivors()?
    *
    * @return an Iterator containing the elements of xs who survive this generation.
    */
  protected def survivors(xs: Iterator[X]): Iterator[X] = xs filter evaluateFitness

  /**
    * This method yields an iterator of xs which survive this generation.
    * It invokes survivors(iterator) to do its work
    *
    * @return an Iterator containing the elements of xs who survive this generation.
    */
  protected def survivors: Iterator[X] = survivors(iterator)

  /**
    * This method yields the complement of survivors such that survivors + nonSurvivors = this
    *
    * @return
    */
  protected def nonSurvivors: Iterator[X] = this - survivors(iterator)

  /**
    * @param i the iterator whose elements are to be removed
    * @return an Iterator composed from this but without any of the elements of i
    */
  protected def -(i: Iterator[X]): Iterator[X] = iterator.filterNot(i.toSet)

  /**
    * This method randomly selects a fraction of this Evolvable
    *
    * @param fraction the fraction of xs that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the xs of this.
    */
  protected def *(fraction: Rational[Long])(implicit random: RNG[Long]): Iterator[X] = permute.take((fraction * members.size).floor.toInt)

  private def next(v: Version[V])(implicit k: Evolvable[X] => Rational[Long], r: Evolvable[X] => RNG[Long]): Repr = {
    val (s, n) = (buildInternal(survivors, v), buildInternal(nonSurvivors, v))
    implicit val random = r(this)
    // TODO need to fix this because, currently, sexual reproduction will be from pairs
    // chosen from two different populations: survivors and non-survivors.
    val nextGeneration = (s.iterator ++ s.offspring ++ buildInternal(n * k(this), v).offspring).toSeq.distinct
    build(nextGeneration.iterator, v)
  }

  private def buildInternal(xs: Iterator[X], v: Version[V]): BaseEvolvable[V, X, Repr] = build(xs, v).asInstanceOf[BaseEvolvable[V, X, Repr]]
}

object Evolvable {
  /**
    * This is the rate at which non-survivors can yet have offspring
    */
  implicit def k[X](e: Evolvable[X]): Rational[Long] = Rational.half

  /**
    * This is the rate at which non-survivors can yet have offspring
    */
  implicit def random[X](e: Evolvable[X]): RNG[Long] = LongRNG(0L)
}

case class EvolvableException(s: String) extends Exception(s)