/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

import com.phasmid.laScala.RNG

/**
  * Created by scalaprof on 7/31/16.
  */
trait Replicator[B] {

  /**
    * Replicate a sequence of B elements
    *
    * @param bs a base sequence
    * @return a Seq of B which may be an imperfect copy of s
    */
  def replicate(bs: Seq[B]): Seq[B]

  /**
    * Replicate a Sequence of B elements
    *
    * @param bs a base sequence
    * @return a Sequence of B which may be an imperfect copy of s
    */
  def replicate(bs: Sequence[B]): Sequence[B] = Sequence(replicate(bs.bases))
}

/**
  * An imperfect replicator that has a finite probability of mis-copying a given B element
  *
  * @param mnopc the mean number of perfect copies before an error is made
  * @param r     a random number generator (of Int)
  * @tparam B the base type
  */
case class ImperfectReplicator[B: Ordinal](mnopc: Int, r: RNG[Int]) extends Replicator[B] {
  // NOTE: a variable.
  var i = 0
  private val rmnopc = RNG.values(r) map ( _ % mnopc )

  def random: Int = {i = i + 1; rmnopc(i)}

  // CONSIDER doing this more efficiently, while using fewer random numbers
  def replicate(bs: Seq[B]): Seq[B] =
    for (b <- bs) yield
      if (random == 0) implicitly[Ordinal[B]].fromInt(random)
      else b
}

/**
  * An perfect replicator that has a zero probability of mis-copying a given B element
  *
  * @tparam B the base type
  */
case class PerfectReplicator[B]() extends Replicator[B] {
  def replicate(bs: Seq[B]): Seq[B] = bs
}
