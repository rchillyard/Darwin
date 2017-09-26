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

import com.phasmid.darwin.Ecological
import com.phasmid.darwin.base.Identifiable
import com.phasmid.darwin.eco._
import com.phasmid.darwin.genetics._
import com.phasmid.laScala.fp.Streamer
import com.phasmid.laScala.values.Incrementable
import com.phasmid.laScala.{Prefix, RenderableCaseClass, Version}

import scala.annotation.tailrec

/**
  * Created by scalaprof on 7/27/16.
  *
  * @param organisms  a collection of organisms, each of type OrganismType
  * @param generation a version representing this generation
  * @param ecology    an Ecology type for this Colony
  * @param ecoFactors the actual ecology in which this Colony flourishes
  * @param genome     the Genome of the organisms represented in this Colony
  * @param phenome    the Phenome of the organisms represented in this Colony
  * @tparam B            the Base type
  * @tparam P            the Ploidy type
  * @tparam G            the Gene type
  * @tparam T            the Trait type
  * @tparam V            the version type (defined to be Incrementable)
  * @tparam X            the underlying type of the xs
  * @tparam OrganismType the Organism type
  * @tparam Repr         the Representation type for this Colony
  */
abstract class AbstractColony[B, P, G, T, V: Incrementable, X, OrganismType <: Organism[B, P, G, T, X], Repr](organisms: Iterable[OrganismType], generation: Version[V], ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]], genome: Genome[B, P, G], phenome: Phenome[P, G, T]) extends BaseEvolvable[V, OrganismType, Repr](organisms, generation) with Ecological[T, X] with Theocratic[B, Repr] with Identifiable {

  /**
    * Default implementation of isFit for any AbstractColony
    *
    * @param f a Fitness value
    * @return true if f represents a sufficiently fit value to survive to the next generation
    */
  def isFit(f: Fitness): Boolean = f.x >= 0.5

  /**
    * Method to create an Organism of type OrganismType from a Nucleus
    *
    * @param nucleus the Nucleus from which to create an Organism
    * @return an instance of OrganismType
    */
  def createOrganism(nucleus: Nucleus[B]): OrganismType

  /**
    * Evaluate the fitness of a member of this Evolvable
    *
    * @param x the member
    * @return true if x is fit enough to survive this generation
    * @throws Exception if the logic to evaluate the fitness of x fails in some unexpected way
    */
  override def evaluateFitness(x: OrganismType): Boolean = (x.fitness(ecology, ecoFactors) map isFit).get

  /**
    * This method yields a new Evolvable by reproduction.
    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
    *
    * @return a new Evolvable
    */
  override def offspring: Iterator[OrganismType] =
  //    if (genome.sexual)
  //    throw GeneticsException("offspring not implemented") // FIXME implement me
  //  else // TODO implement me properly
    (organisms filter { o: OrganismType => o.fitness(ecology, ecoFactors).get.x >= 0 }).toIterator

  //  def build(xs: Iterator[OrganismType], v: Version[V]): AbstractColony[B, P, G, T, V, X, OrganismType]

  def cullMembers(): Repr = (for (v <- generation.next()) yield build(Nil, v)).get

  def seedMembers(size: Int, genome: Genome[B, P, G], p_ : Int, random: RNG[B]): Repr = {
    @tailrec def inner(bns: Seq[Nucleus[B]], br: Random[B], n: Int): (Seq[Nucleus[B]], Random[B]) =
      if (n == 0) (bns, br)
      else {
        val (bn, br_) = genome.recombine(random)
        inner(bns :+ bn, br_, n - 1)
      }

    val (bns, _) = inner(Nil, random, size)
    build(bns map createOrganism, generation)
  }
}

/**
  * Created by scalaprof on 7/27/16.
  */
case class Colony[B, G, T, V: Incrementable, X](name: String, organisms: Iterable[SexualSedentaryOrganism[B, G, T, X]], generation: Version[V], ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]) extends AbstractColony[B, Boolean, G, T, V, X, SexualSedentaryOrganism[B, G, T, X], Colony[B, G, T, V, X]](organisms, generation, ecology, ecoFactors, genome, phenome) {

  import com.phasmid.darwin.evolution.Random.RandomizableLong

  implicit val idStreamer: Streamer[Long] = Streamer(RNG[Long](0).toStream)

  def seedMembers(size: Int, random: RNG[B]): Colony[B, G, T, V, X] = seedMembers(size, genome, 2, random)

  def build(xs: Iterable[SexualSedentaryOrganism[B, G, T, X]], v: Version[V]): Colony[B, G, T, V, X] = new Colony(name, xs, v, ecology, ecoFactors, genome, phenome)

  def createOrganism(nucleus: Nucleus[B]): SexualSedentaryOrganism[B, G, T, X] = SexualSedentaryOrganism(generation, genome, phenome, nucleus, ecology)

  def apply(phenotype: Phenotype[T]): Adaptatype[X] = throw GeneticsException("apply not implemented") // FIXME implement me (??)

  override def toString: String = s"$name generation $generation with ${organisms.size} organisms"

  // CONSIDER removing the parameter tab from the invocation: it isn't really needed (in all defs of render)
  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Colony[Any, Any, Any, Any, Any]]).render(indent)(tab)

}

object Colony {

  def apply[B, G, T, V: Incrementable, X](name: String, generation: Version[V], ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]): Colony[B, G, T, V, X] = Colony(name, Nil, generation, ecology, ecoFactors, genome, phenome)

  def apply[B, G, T, X](name: String, ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T]): Colony[B, G, T, Long, X] = apply(name, Version.longVersion("0"), ecology, ecoFactors, genome, phenome)

}