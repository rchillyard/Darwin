package com.phasmid.darwin.evolution

import com.phasmid.laScala.values.Rational
import com.phasmid.laScala.{Incrementable, RNG, Shuffle}

/**
  * Created by scalaprof on 7/27/16.
  */
trait Evolvable[X, Y] extends Generation[X] {

  /**
    * Get a random number generator of Y
    *
    * @return
    */
  def random: RNG[Y]

  /**
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

  /**
    * This is the rate at which
    *
    * @return
    */
  def k: Rational = Rational.half
}

/**
  * This abstract base class for Evolvable represents a Seq of X objects which, together, are Evolvable.
  *
  * @param members an unsorted collection of objects which, together, are Evolvable
  * @param go      an optional Generation (Evolvables which are not complete generations use None here)
  * @tparam X the underlying type of the members
  * @tparam Q the type of Generation which this Evolvable object supports
  * @tparam Y the type of the Random number generator to be used
  */
abstract class BaseEvolvable[Q: Incrementable, X, Y](members: Iterable[X], go: Option[Generation[Q]]) extends Evolvable[X, Y] with Iterable[X] {

  /**
    * @return an Iterator based on the members of this Evolvable
    */
  def iterator: Iterator[X] = members.iterator

  /**
    * This method yields an iterator from the elements of xs which survive this generation.
    * Note that, although the default implementation simply culls all the unfit members
    * and keeps all of the fit members, sub-classes may redefine this method to allow a random choice
    * of survivors which is only partially guided by fitness.
    *
    * CONSIDER do we really need this method, as opposed to survivors()?
    *
    * @return an Iterator containing the elements of xs who survive this generation.
    */
  def survivors(xs: Iterator[X]): Iterator[X] = xs filter (evaluateFitness(_))

  /**
    * This method yields an iterator of members which survive this generation.
    * It invokes survivors(iterator) to do its work
    *
    * @return an Iterator containing the elements of xs who survive this generation.
    */
  def survivors: Iterator[X] = survivors(iterator)

  /**
    * This method yields the complement of survivors such that survivors + nonSurvivors = this
    *
    * @return
    */
  def nonSurvivors: Iterator[X] = this - survivors(iterator)

  /**
    * @param i the iterator whose elements are to be removed
    * @return an Iterator composed from this but without any of the elements of i
    */
  def -(i: Iterator[X]): Iterator[X] = iterator.filterNot(i.toSet)

  /**
    * This method randomly selects a fraction of this Evolvable
    *
    * @param fraction the fraction of members that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the members of this.
    */
  def *(fraction: Rational): Iterator[X] = shuffle.take((fraction * members.size).floor.toInt).toIterator

  /**
    * Method to create a new concrete instance of this BaseEvolvable.
    *
    * CONSIDER using CanBuildFrom
    *
    * @param members the members to be included in this BaseEvolvable
    * @param go an optional Generation: complete generations have Some(g) but incomplete generations have None
    * @return a concrete instance of BaseEvolvable corresponding to the same type as this
    */
  def build(members: Iterator[X], go: Option[Generation[Q]]): BaseEvolvable[Q, X, Y]

  /**
    * By default, this implementation of next allows k non-survivors to reproduce (before they are killed, presumably).
    *
    * @return a new Generation of X
    */
  def next: Generation[X] = {
    val (s, n) = (build(survivors, None), build(nonSurvivors, None))
    val nextGeneration = (s.iterator ++ s.offspring ++ build(n * k, None).offspring).toSeq.distinct
    build(nextGeneration.iterator, for (g <- go) yield g.next)
  }

  /**
    * Method to shuffle the order of this Evolvable
    * @return a randomly-shuffled iterable on the members
    */
  def shuffle: Iterable[X]

}

case class EvolvableException(s: String) extends Exception(s)