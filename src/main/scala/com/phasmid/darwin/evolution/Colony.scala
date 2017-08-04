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

import com.phasmid.darwin.{Ecological, Identifier}
import com.phasmid.darwin.eco.{Ecology, Environment, Environmental}
import com.phasmid.darwin.genetics._
import com.phasmid.laScala.Version
import com.phasmid.laScala.values.Incrementable

import scala.annotation.tailrec

/**
  * Created by scalaprof on 7/27/16.
  */
abstract class AbstractColony[B, P, G, T, V: Incrementable, X, OrganismType, Repr](organisms: Iterable[OrganismType], generation: Version[V], ecology: Ecology[T, X], genome: Genome[B, P, G], phenome: Phenome[P, G, T]) extends BaseEvolvable[V, OrganismType, Repr](organisms, generation) with Ecological[T, X] with Theocratic[B, Repr] with Identifier {

  def createOrganism(nucleus: Nucleus[B]): OrganismType

  /**
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param x the member
    * @return true if x is fit enough to survive this generation
    */
  override def evaluateFitness(x: OrganismType): Boolean = ??? // TODO implement me

  /**
    * This method yields a new Evolvable by reproduction.
    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    *
    * @return a new Evolvable
    */
  override def offspring: Iterator[OrganismType] = ??? // TODO implement me

//  def build(xs: Iterator[OrganismType], v: Version[V]): AbstractColony[B, P, G, T, V, X, OrganismType]

  def cullMembers(): Repr = (for (v <- generation.next()) yield build(Nil, v)).get

  def seedMembers(size: Int, genome: Genome[B, P, G], p_ : Int, random: RNG[B]): Repr = {
    @tailrec def inner(bns: Seq[Nucleus[B]], br: Random[B], n: Int): (Seq[Nucleus[B]], Random[B]) =
      if (n == 0) (bns, br)
      else {
        val (bn, br_) = genome.recombine(random)
        inner(bns :+ bn, br_, n-1)
      }
    val (bns, _) = inner(Nil, random, size)
    build(bns map createOrganism, generation)
  }
}

/**
  * Created by scalaprof on 7/27/16.
  */
case class Colony[B, G, T, V: Incrementable, X](name: String, organisms: Iterable[SexualSedentaryOrganism[B, G, T, X]], generation: Version[V], ecology: Ecology[T, X], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]) extends AbstractColony[B, Boolean, G, T, V, X, SexualSedentaryOrganism[B, G, T, X], Colony[B, G, T, V, X]](organisms, generation, ecology, genome, phenome) {

  def seedMembers(size: Int, random: RNG[B]): Colony[B, G, T, V, X] = seedMembers(size, genome, 2, random)

  def build(xs: Iterable[SexualSedentaryOrganism[B, G, T, X]], v: Version[V]): Colony[B, G, T, V, X] = new Colony(name, xs, v, ecology, genome, phenome)

  def createOrganism(nucleus: Nucleus[B]): SexualSedentaryOrganism[B, G, T, X] = SexualSedentaryOrganism(genome, phenome, nucleus, ecology)

  override def apply(v1: Phenotype[T]): Adaptatype[X] = ??? // TODO implement me (??)

  override def toString: String = s"$name generation $generation with ${organisms.size} organisms"
}

object Colony {

  def apply[B, P, G, T, V: Incrementable, X](name: String, generation: Version[V], ecology: Ecology[T, X], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]): Colony[B, G, T, V, X] = Colony(name, Nil, generation, ecology, genome, phenome)

  def apply[B, P, G, T, X](name: String, ecology: Ecology[T, X], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]): Colony[B, G, T, Long, X] = apply(name, Version.longVersion("0"), ecology, genome, phenome)

}