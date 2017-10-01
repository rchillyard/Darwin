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

import com.phasmid.darwin.base.{Auditable, Identifier}
import com.phasmid.darwin.eco.{EcoFactor, Ecology, Fitness}
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
trait Evolvable[Z <: Individual[_, _]] extends Ordered[Evolvable[Z]] with Permutable[Z] {

  /**
    * TODO make this an implicit function in Evolvable sub-class
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param z the member
    * @return true if z is fit enough to survive this generation
    */
  def evaluateFitness(z: Z): Boolean

  /**
    * This method yields a new set of Z objects, by reproduction.
    * If the ploidy of Z is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    * All members of this Evolvable take part in reproduction, as deceased organisms have already been eliminated from this.
    *
    * @return a new Evolvable
    */
  def offspring: Iterator[Z]
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

  /**
    * By default, this implementation of next allows k non-survivors to mate (before they are killed, presumably).
    *
    * @return a new generation of this Evolvable
    */
  def next(isSnapshot: Boolean = false): Try[Repr] = for (v <- vv.next(isSnapshot)) yield next(v)

  /**
    * Method to create the next generation
    *
    * @param v the Version for the next generation
    * @return the next generation as a Repr
    */
  def next(v: Version[V]): Repr
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
  * (4) it defines a generation.
  *
  * @tparam Z    the underlying type of the individuals which make up this evolvable
  * @tparam V    the generation type that identifies successive generations
  * @tparam Repr the result type of calling next (defined in Sequential)
  */
trait SequentialEvolvable[Z <: Individual[_, _], V, Repr] extends Evolvable[Z] with Sequential[Repr] with Iterable[Z] with Generational[V] {

  /**
    * Method to create a new concrete instance of this BaseEvolvable.
    *
    * CONSIDER using CanBuildFrom
    *
    * @param zs the zs to be included in this BaseEvolvable
    * @param v  a Version
    * @return a concrete instance of BaseEvolvable corresponding to the same type as this
    */
  def build(zs: Iterable[Z], v: Version[V]): Repr
}

/**
  * This abstract base class for Evolvable represents a Seq of Z objects which, together, are Evolvable.
  *
  * @param zs      an unsorted collection of objects which, together, are Evolvable
  * @param vv      a Version (Evolvables which are not complete generations use a snapshot here)
  * @tparam V the generation type (defined to be Incrementable)
  * @tparam Z the underlying type of the zs (must implement Individual)
  */
abstract class BaseEvolvable[V: Incrementable, Z <: Individual[_, _], Repr](zs: Iterable[Z], vv: Version[V]) extends BaseGenerational[V, Repr](vv) with SequentialEvolvable[Z, V, Repr] with Auditable {

  def next(v: Version[V]): Repr = doNext(v)

  /**
    * Method which determines if a particular Fitness value will be considered sufficiently fit to survive this generation
    *
    * @param f a Fitness value
    * @return true if f represents a sufficiently fit value to survive to the next generation
    */
  def isFit(f: Fitness): Boolean

  /**
    * @return an Iterator based on the individual members of this Evolvable
    */
  def iterator: Iterator[Z] = zs.iterator

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

  /**
    * This method yields an iterator from the elements of xs which survive this generation.
    * Note that, although the default implementation simply culls all the unfit xs
    * and keeps all of the fit xs, sub-classes may redefine this method to allow a random choice
    * of survivors which is only partially guided by fitness.
    *
    * CONSIDER do we really need this method, as opposed to survivors()?
    *
    * @return an Iterable containing the elements of xs who survive this generation.
    */
  protected def survivors(xs: Iterable[Z]): Iterable[Z] = xs filter evaluateFitness

  /**
    * This method yields an iterator of xs which survive this generation.
    * It invokes survivors(iterator) to do its work
    *
    * @return an Iterator containing the elements of xs who survive this generation.
    */
  protected def survivors: Iterable[Z] = survivors(this)

  /**
    * This method yields the complement of survivors such that survivors + nonSurvivors = this
    *
    * @return
    */
  protected def nonSurvivors: Iterable[Z] = this - survivors(this)

  /**
    * @param i the iterator whose elements are to be removed
    * @return an Iterator composed from this but without any of the elements of i
    */
  protected def -(i: Iterable[Z]): Iterable[Z] = this.filterNot(i.toSet)

  /**
    * This method randomly selects a fraction of this Evolvable
    *
    * @param fraction the fraction of xs that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the xs of this.
    */
  protected def *(fraction: Rational[Long])(implicit random: RNG[Long]): Iterable[Z] = permute.take((fraction * zs.size).floor.toInt).toSeq

  private def doNext(v: Version[V])(implicit k: Evolvable[Z] => Rational[Long], r: Evolvable[Z] => RNG[Long]): Repr = {
    // TODO in sexual reproduction, we need to introduce to s a small number (possibly zero) of members of a distinct Evolvable
    // such as a rival Colony. Typically, these additions, if any, will be males.
    val (s, n) = (buildInternal(survivors, v), buildInternal(nonSurvivors, v))
    implicit val random: RNG[Long] = r(this)
    // TODO need to fix this because, currently, sexual reproduction will be from pairs
    // chosen from two different populations: survivors and non-survivors.
    val nextGeneration = (s.iterator ++ s.offspring ++ buildInternal(n * k(this), v).offspring).toSeq.distinct
    build(nextGeneration, v)
  }

  private def buildInternal(xs: Iterable[Z], v: Version[V]): BaseEvolvable[V, Z, Repr] = build(xs, v).asInstanceOf[BaseEvolvable[V, Z, Repr]]

}

object Evolvable {
  /**
    * This is the rate at which non-survivors can yet have offspring
    */
  implicit def k[Z <: Individual[_, _]](e: Evolvable[Z]): Rational[Long] = Rational.half

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  /**
    * This is the rate at which non-survivors can yet have offspring
    *
    * TODO Huh??
    *
    */
  implicit def random[Z <: Individual[_, _]](e: Evolvable[Z]): RNG[Long] = RNG[Long](0L)
}

case class EvolvableException(s: String) extends Exception(s)

/**
  * Trait which defines the properties of an Individual, the kind of thing that makes up the members of a Colony, for instance.
  */
trait Individual[T, X] extends Identifier {
  /**
    * CONSIDER changing the parameters to this method if we can find them more simply
    *
    * @param ecology    the Ecology
    * @param ecoFactors the local ecology
    * @return the Fitness of this Organism in the ecology, wrapped in Try
    */
  def fitness(ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]]): Try[Fitness]


}