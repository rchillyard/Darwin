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

import com.phasmid.darwin.base.{CaseIdentifiable, Identified, Identifier, RandomName}
import com.phasmid.darwin.eco.{EcoFactor, Ecology, Fitness}
import com.phasmid.darwin.genetics._
import com.phasmid.laScala.Version
import com.phasmid.laScala.fp.Streamer

import scala.util.Try

/**
  * Created by scalaprof on 7/27/16.
  */
trait Organism[B, P, G, T, X] {

  def genome: Genome[B, P, G]

  def phenome: Phenome[P, G, T]

  def nucleus: Nucleus[B]

  def genotype: Genotype[P, G] = genome(nucleus)

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
trait SedentaryOrganism[B, P, G, T, X] extends Organism[B, P, G, T, X] {

  def ecology: Ecology[T, X]

  def adaptatype: Adaptatype[X] = ecology(phenotype)

  def build(d: Identifier, genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T], nucleus: Nucleus[B], ecology: Ecology[T, X]): Organism[B, P, G, T, X]
}

case class SexualSedentaryOrganism[B, G, T, X](id: Identifier, genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T], nucleus: Nucleus[B], ecology: Ecology[T, X]) extends Identified(id) with SedentaryOrganism[B, Boolean, G, T, X] with CaseIdentifiable[SexualSedentaryOrganism[Any, Any, Any, Any]] {
  def build(d: Identifier, genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T], nucleus: Nucleus[B], ecology: Ecology[T, X]): Organism[B, Boolean, G, T, X] = SexualSedentaryOrganism(id, genome, phenome, nucleus, ecology)

  //  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[SexualSedentaryOrganism[Any,Any,Any,Any]]).render(indent)(tab)

}

object SexualSedentaryOrganism {

  def apply[B, G, T, X, V](generation: Version[V], genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T], nucleus: Nucleus[B], ecology: Ecology[T, X])(implicit streamer: Streamer[Long]): SexualSedentaryOrganism[B, G, T, X] = new SexualSedentaryOrganism[B, G, T, X](RandomName("sso", generation, streamer), genome, phenome, nucleus, ecology)

  //  def apply[B, G, T, X](genome: Genome[B, Boolean, G], phenome: Phenome[Boolean, G, T], random: Stream[(B,B)], ecology: Ecology[T, X]): SexualSedentaryOrganism[B, G, T, X] = {
  //    val loci: Int = genome.loci
  //    val x: List[(B, B)] = random take loci toList
  //    val y: (Seq[B], Seq[B]) = x unzip
  //    apply(genome, phenome, Seq(y._1, y._2), ecology)
  //  }

}