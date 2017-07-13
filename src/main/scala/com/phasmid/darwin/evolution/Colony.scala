/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.evolution

import com.phasmid.laScala.Version
import com.phasmid.laScala.values.Incrementable

/**
  * Created by scalaprof on 7/27/16.
  */
case class Colony[B, P, G, T, V: Incrementable, X](organisms: Iterable[Organism[B, P, G, T, X]], generation: Version[V]) extends BaseEvolvable[V, Organism[B, P, G, T, X], Colony[B, P, G, T, V, X]](organisms, generation) {

  /**
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param x the member
    * @return true if x is fit enough to survive this generation
    */
  override def evaluateFitness(x: Organism[B, P, G, T, X]): Boolean = ??? // TODO implement me

  /**
    * This method yields a new Evolvable by reproduction.
    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    *
    * @return a new Evolvable
    */
  override def offspring: Iterator[Organism[B, P, G, T, X]] = ??? // TODO implement me

  def build(xs: Iterator[Organism[B, P, G, T, X]], v: Version[V]): Colony[B, P, G, T, V, X] = Colony(xs.toList, v)
}
