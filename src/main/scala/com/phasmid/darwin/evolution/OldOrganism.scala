///*
// * DARWIN Genetic Algorithms Framework Project.
// * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
// *
// * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
// * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
// *
// *      This file is part of Darwin.
// *
// *      Darwin is free software: you can redistribute it and/or modify
// *      it under the terms of the GNU General Public License as published by
// *      the Free Software Foundation, either version 3 of the License, or
// *      (at your option) any later version.
// *
// *      This program is distributed in the hope that it will be useful,
// *      but WITHOUT ANY WARRANTY; without even the implied warranty of
// *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *      GNU General Public License for more details.
// *
// *      You should have received a copy of the GNU General Public License
// *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.phasmid.darwin.evolution
//
//import com.phasmid.darwin.base._
//import com.phasmid.darwin.eco.{Ecology, Environment, Fitness}
//import com.phasmid.darwin.genetics._
//import com.phasmid.darwin.run.Species
//import com.phasmid.laScala.fp.{Named, Streamer}
//import com.phasmid.laScala.{Prefix, Version}
//
//import scala.util.Try
//
///**
//  * Created by scalaprof on 7/27/16.
//  *
//  * Definition of an organism and its behaviors.
//  * An organism belongs to a Species and is able to reproduce.
//  * It's nucleus is considered fixed: there are no changes to the nucleus after the organism is born.
//  *
//  * @tparam B the Base type
//  * @tparam G the Gene type
//  * @tparam P the Ploidy type
//  * @tparam T the Trait type
//  * @tparam V the Version (Generation) type
//  * @tparam X the Eco-type
//  */
//trait OldOrganism[B, G, P, T, V, X] extends Reproductive[OldOrganism[B, G, P, T, V, X]] with Sexual[P] with Individual[T, X] {
//
//  /**
//    * @return the generation during which this organism was formed
//    */
//  def generation: Version[V]
//
//  /**
//    * @return the species to which this organism belongs
//    */
//  def species: Species[B, G, P, T, X]
//
//  /**
//    * @return the nucleus of this organism
//    */
//  def nucleus: Nucleus[B]
//
//  /**
//    * @return the genotype of this organism
//    */
//  def genotype: Genotype[G, P] = species.genome(nucleus)
//
//  /**
//    * @return the phenotype of this organism
//    */
//  def phenotype: Phenotype[T] = species.phenome(genotype)
//
//  /**
//    * @param environment the Environment
//    * @return the Fitness of this OldOrganism in the environment, wrapped in Try
//    */
//  def fitness(environment: Environment[T, X]): Try[Fitness] = environment.ecology(phenotype).fitness(environment.habitat)
//
//}
//
///**
//  * Definition of an adapted organism, one that is adapted to an Ecology.
//  * It is not generally fixed to a particular Habitat, but it its Ecology, and therefore its Adaptatype, is fixed.
//  *
//  * Created by scalaprof on 7/27/16.
//  *
//  * @tparam B the Base type
//  * @tparam G the Gene type
//  * @tparam P the Ploidy type
//  * @tparam T the Trait type
//  * @tparam V the Version (Generation) type
//  * @tparam X the Eco-type
//  */
//trait AdaptedOldOrganism[B, G, P, T, V, X] extends OldOrganism[B, G, P, T, V, X] {
//
//  /**
//    * @return the Ecology to which this organism is adapted
//    */
//  def ecology: Ecology[T, X]
//
//  /**
//    * @return the adaptatype for this organism
//    */
//  def adaptatype: Adaptatype[X] = ecology(phenotype)
//
//  /**
//    * Method to build a new AdaptedOldOrganism
//    *
//    * @param id the id of the new instance
//    * @param generation the natal generation (version) of the new instance
//    * @param species the species of the new instance
//    * @param nucleus the nucleus of the new instance
//    * @param ecology the ecology of the new instance
//    * @return the new instance
//    */
//  def build(id: Named, generation: Version[V], species: Species[B, G, P, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X]): OldOrganism[B, G, P, T, V, X]
//}
//
///**
//  * Concrete case class which implements AdaptedOldOrganism and is Sexual (i.e. diploid).
//  *
//  * @param id the Identifier
//  * @param generation the generation
//  * @param species the species
//  * @param nucleus the nucleus
//  * @param ecology the ecology
//  * @tparam B the Base type
//  * @tparam G the Gene type
//  * @tparam T the Trait type
//  * @tparam V the Version (Generation) type
//  * @tparam X the Eco-type
//  */
//case class SexualAdaptedOldOrganism[B, G, T, V, X](id: Named, generation: Version[V], species: Species[B, G, Boolean, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X]) extends Identified(id) with Mating[B, G, T, V, X, OldOrganism[B, G, Boolean, T, V, X]] with AdaptedOldOrganism[B, G, Boolean, T, V, X] with SelfAuditing with Identifiable {
//  def build(id: Named, generation: Version[V], species: Species[B, G, Boolean, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X]): OldOrganism[B, G, Boolean, T, V, X] = SexualAdaptedOldOrganism(id, generation, species, nucleus, ecology)
//
//  def mate(evolvable: Evolvable[OldOrganism[B, G, Boolean, T, V, X]]): Iterable[OldOrganism[B, G, Boolean, T, V, X]] = ??? // TODO
//
//  def pool: Evolvable[OldOrganism[B, G, Boolean, T, V, X]] = ??? // TODO
//
//  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[SexualAdaptedOldOrganism[Any, Any, Any, Any, Any]])(indent)
//}
//
//object SexualAdaptedOldOrganism {
//
//  def apply[B, G, T, V, X](generation: Version[V], species: Species[B, G, Boolean, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X])(implicit streamer: Streamer[Long]): SexualAdaptedOldOrganism[B, G, T, V, X] = new SexualAdaptedOldOrganism[B, G, T, V, X](IdentifierStrVerUID("sso", generation, streamer), generation, species, nucleus, ecology)
//
//}
//
///**
//  * Concrete case class which implements AdaptedOldOrganism and is ASexual (i.e. haploid).
//  *
//  * @param id the Identifier
//  * @param generation the generation
//  * @param species the species
//  * @param nucleus the nucleus
//  * @param ecology the ecology
//  * @tparam B the Base type
//  * @tparam G the Gene type
//  * @tparam T the Trait type
//  * @tparam V the Version (Generation) type
//  * @tparam X the Eco-type
//  */
//case class Bacterium[B, G, T, V, X](id: Named, generation: Version[V], species: Species[B, G, Unit, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X]) extends Identified(id) with ASexual[B, G, T, V, X, OldOrganism[B, G, Unit, T, V, X]] with AdaptedOldOrganism[B, G, Unit, T, V, X] with SelfAuditing {
//
//  def fission: Iterable[OldOrganism[B, G, Unit, T, V, X]] = ??? // TODO
//
//  def build(id: Named, generation: Version[V], species: Species[B, G, Unit, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X]) = Bacterium(id, generation, species, nucleus, ecology)
//
//  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[Bacterium[Any, Any, Any, Any, Any]])(indent)
//}
//
//object Bacterium {
//
//  def apply[B, G, T, V, X](generation: Version[V], species: Species[B, G, Unit, T, X], nucleus: Nucleus[B], ecology: Ecology[T, X])(implicit streamer: Streamer[Long]): Bacterium[B, G, T, V, X] = new Bacterium[B, G, T, V, X](IdentifierStrVerUID("sso", generation, streamer), generation, species, nucleus, ecology)
//}
//
//trait OldOrganismBuilder[Z] {
//
//  def build[B, G, P, T, V, X](generation: Version[V], species: Species[B, G, P, T, X], nucleus: Nucleus[B], environment: Environment[T, X]): Z
//}
//
