package com.phasmid.darwin.evolution

import com.phasmid.laScala.RNG
import com.phasmid.laScala.values.{Incrementable, Rational}

import scala.util.Try

/**
  * Created by scalaprof on 7/27/16.
  */
trait Evolvable[X, Repr] extends Sequential[Repr] {

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

  /**
    * TODO make this an implicit in Evolvable object
    * This is the rate at which non-survivors can yet have offspring
    *
    * @return
    */
  def k: Rational[Long] = Rational.half
}

/**
  * This abstract base class for Evolvable represents a Seq of X objects which, together, are Evolvable.
  *
  * @param members an unsorted collection of objects which, together, are Evolvable
  * @param version a Version (Evolvables which are not complete generations use a snapshot here)
  * @tparam V the version type (defined to be Incrementable)
  * @tparam X the underlying type of the xs
  */
abstract class BaseEvolvable[V: Incrementable, X, Y, Repr](members: Iterable[X], version: Version[V]) extends Evolvable[X, Repr] with Iterable[X] {

  /**
    * @return an Iterator based on the xs of this Evolvable
    */
  def iterator: Iterator[X] = members.iterator

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
  def survivors(xs: Iterator[X]): Iterator[X] = xs filter evaluateFitness

  /**
    * This method yields an iterator of xs which survive this generation.
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
    * @param fraction the fraction of xs that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the xs of this.
    */
  def *(fraction: Rational[Long]): Iterator[X] = shuffle.take((fraction * members.size).floor.toInt).toIterator

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
    * By default, this implementation of next allows k non-survivors to mate (before they are killed, presumably).
    *
    * @return a new generation of this Evolvable
    */
  def next(isSnapshot: Boolean = false): Try[Repr] = for (v <- version.next(isSnapshot)) yield next(v)


  def compare(that: Sequential[Repr]): Int = that match {
    case e: BaseEvolvable[V, X, Y, Repr] => this.version.compare(e.getVersion)
    case _ => throw EvolutionException(s"cannot compare $that with $this")
  }

  /**
    * Method to shuffle the order of this Evolvable
    *
    * @return a randomly-shuffled iterable on the xs
    */
  def shuffle: Iterable[X]

  /**
    * Get a random number generator of Y
    *
    * @return
    */
  def random: RNG[Y]

  private def next(v: Version[V]): Repr = {
    val (s, n) = (buildInternal(survivors, v), buildInternal(nonSurvivors, v))
    // TODO need to fix this because, currently, sexual reproduction will be from pairs
    // chosen from two different populations: survivors and non-survivors.
    val nextGeneration = (s.iterator ++ s.offspring ++ buildInternal(n * k, v).offspring).toSeq.distinct
    build(nextGeneration.iterator, v)
  }

  private def buildInternal(xs: Iterator[X], v: Version[V]): BaseEvolvable[V, X, Y, Repr] = build(xs, v).asInstanceOf[BaseEvolvable[V, X, Y, Repr]]

  private def getVersion: Version[V] = version
}

case class EvolvableException(s: String) extends Exception(s)