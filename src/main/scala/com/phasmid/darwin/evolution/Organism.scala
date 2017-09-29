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

import com.phasmid.darwin.base.{CaseIdentifiable, Identified, Identifier, IdentifierStrVerUID}
import com.phasmid.darwin.eco.{EcoFactor, Ecology, Fitness}
import com.phasmid.darwin.genetics._
import com.phasmid.laScala.Version
import com.phasmid.laScala.fp.Streamer

import scala.util.Try

/**
  * Created by scalaprof on 7/27/16.
  */
trait Organism[B, G, P, T, V, X] extends Reproductive[Organism[B, G, P, T, V, X]] with Sexual[P] {

  /**
    * @return the generation during which this organism was formed
    */
  def generation: Version[V]

  /**
    * @return the genome from which the genotype of this organism was formed
    */
  def genome: Genome[B, G, P]

  /**
    * @return the phenome from which the phenotype of this organism was formed
    */
  def phenome: Phenome[G, P, T]

  /**
    * @return the nucleus of this organism
    */
  def nucleus: Nucleus[B]

  /**
    * @return the genotype of this organism
    */
  def genotype: Genotype[G, P] = genome(nucleus)

  /**
    * @return the phenotype of this organism
    */
  def phenotype: Phenotype[T] = phenome(genotype)

  /**
    * CONSIDER changing the parameters to this method if we can find them more simply
    *
    * @param ecology    the Ecology
    * @param ecoFactors the local ecology
    * @return the Fitness of this Organism in the ecology, wrapped in Try
    */
  def fitness(ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]]): Try[Fitness] = ecology(phenotype).fitness(ecoFactors)

}

/**
  * Created by scalaprof on 7/27/16.
  */
trait SedentaryOrganism[B, G, P, T, V, X] extends Organism[B, G, P, T, V, X] {

  def ecology: Ecology[T, X]

  def adaptatype: Adaptatype[X] = ecology(phenotype)

  def build(d: Identifier, generation: Version[V], genome: Genome[B, G, P], phenome: Phenome[G, P, T], nucleus: Nucleus[B], ecology: Ecology[T, X]): Organism[B, G, P, T, V, X]
}

case class SexualSedentaryOrganism[B, G, T, V, X](id: Identifier, generation: Version[V], genome: Genome[B, G, Boolean], phenome: Phenome[G, Boolean, T], nucleus: Nucleus[B], ecology: Ecology[T, X]) extends Identified(id) with Mating[B, G, T, V, X, Organism[B, G, Boolean, T, V, X]] with SedentaryOrganism[B, G, Boolean, T, V, X] with CaseIdentifiable[SexualSedentaryOrganism[Any, Any, Any, Any, Any]] {
  def build(d: Identifier, generation: Version[V], genome: Genome[B, G, Boolean], phenome: Phenome[G, Boolean, T], nucleus: Nucleus[B], ecology: Ecology[T, X]): Organism[B, G, Boolean, T, V, X] = SexualSedentaryOrganism(id, generation, genome, phenome, nucleus, ecology)

  def mate(evolvable: Evolvable[X]): Iterable[Organism[B, G, Boolean, T, V, X]] = ??? // TODO

  def pool: Evolvable[X] = ??? // TODO
}

object SexualSedentaryOrganism {

  def apply[B, G, T, V, X](generation: Version[V], genome: Genome[B, G, Boolean], phenome: Phenome[G, Boolean, T], nucleus: Nucleus[B], ecology: Ecology[T, X])(implicit streamer: Streamer[Long]): SexualSedentaryOrganism[B, G, T, V, X] = new SexualSedentaryOrganism[B, G, T, V, X](IdentifierStrVerUID("sso", generation, streamer), generation, genome, phenome, nucleus, ecology)

  //  def apply[B, G, T, X](genome: Genome[B, G, Boolean], phenome: Phenome[G, Boolean, T], random: Stream[(B,B)], ecology: Ecology[T, X]): SexualSedentaryOrganism[B, G, T, X] = {
  //    val loci: Int = genome.loci
  //    val x: List[(B, B)] = random take loci toList
  //    val y: (Seq[B], Seq[B]) = x unzip
  //    apply(genome, phenome, Seq(y._1, y._2), ecology)
  //  }

}

case class Bacterium[B, G, T, V, X](id: Identifier, generation: Version[V], genome: Genome[B, G, Unit], phenome: Phenome[G, Unit, T], nucleus: Nucleus[B], ecology: Ecology[T, X]) extends Identified(id) with ASexual[B, G, T, V, X, Organism[B, G, Unit, T, V, X]] with SedentaryOrganism[B, G, Unit, T, V, X] with CaseIdentifiable[Bacterium[Any, Any, Any, Any, Any]] {
  //  def build(d: Identifier, genome: Genome[B, G, Unit], phenome: Phenome[G, Unit, T], nucleus: Nucleus[B], ecology: Ecology[T, X]): Organism[B, G, Unit, T, V, X] =

  def build(d: Identifier, generation: Version[V], genome: Genome[B, G, Unit], phenome: Phenome[G, Unit, T], nucleus: Nucleus[B], ecology: Ecology[T, X]) = Bacterium(id, generation, genome, phenome, nucleus, ecology)

  def fission: Iterable[Organism[B, G, Unit, T, V, X]] = ??? // TODO
}

object Bacterium {

  def apply[B, G, T, V, X](generation: Version[V], genome: Genome[B, G, Unit], phenome: Phenome[G, Unit, T], nucleus: Nucleus[B], ecology: Ecology[T, X])(implicit streamer: Streamer[Long]): Bacterium[B, G, T, V, X] = new Bacterium[B, G, T, V, X](IdentifierStrVerUID("sso", generation, streamer), generation, genome, phenome, nucleus, ecology)
}