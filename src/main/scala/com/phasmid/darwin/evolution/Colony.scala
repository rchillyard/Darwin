package com.phasmid.darwin.evolution

import com.phasmid.laScala.RNG
import com.phasmid.laScala.values.{Incrementable, Rational}

/**
  * Created by scalaprof on 7/27/16.
  */
case class Colony[B, P, G, T, V: Incrementable, X](organisms: Iterable[Organism[B, P, G, T, X]], generation: Version[V]) extends BaseEvolvable[V, Organism[B, P, G, T, X], Long](organisms, Some(generation)) {
  /**
    * This method randomly selects a fraction of this Evolvable
    *
    * @param fraction the fraction of members that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the members of this.
    */
  override def *(fraction: Rational[Long]): Iterator[Organism[B, P, G, T, X]] = ???

  /**
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param x the member
    * @return true if x is fit enough to survive this generation
    */
  override def evaluateFitness(x: Organism[B, P, G, T, X]): Boolean = ???

  /**
    * Get a random number generator of Y
    *
    * @return
    */
  override def random: RNG[Long] = ???

  /**
    * This method yields a new Evolvable by reproduction.
    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    *
    * @return a new Evolvable
    */
  override def offspring: Iterator[Organism[B, P, G, T, X]] = ???

  override def shuffle: Iterable[Organism[B, P, G, T, X]] = ???

  // CONSIDER using CanBuildFrom
  def build(members: Iterator[Organism[B, P, G, T, X]], vo: Option[Subversioned[V]]): BaseEvolvable[V, Organism[B, P, G, T, X], Long] = ???
}
