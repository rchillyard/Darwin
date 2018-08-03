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
//import com.phasmid.darwin.Ecological
//import com.phasmid.darwin.base.{CaseIdentifiable, SelfAuditing}
//import com.phasmid.darwin.eco._
//import com.phasmid.darwin.genetics._
//import com.phasmid.darwin.visualization.{Visualization}
//import com.phasmid.laScala.values.Incrementable
//import com.phasmid.laScala.{Prefix, Version}
//
//import scala.annotation.tailrec
//
///**
//  * Created by scalaprof on 7/27/16.
//  *
//  * @param organisms  a collection of organisms, each of type Z
//  * @param generation a version representing this generation
//  * @param env        the Environment in which this Colony flourishes
//  * @tparam V    the generation type (defined to be Incrementable)
//  * @tparam X    the underlying type of the xs
//  * @tparam Z    the Organism type
//  * @tparam Repr the Representation type for this Colony
//  */
//abstract class AbstractColony[V: Incrementable, X, Z : Fit : Reproduction : Viable : Visualization : Ordering, Repr](organisms: Iterable[Z], generation: Version[V], env: Environment[T, X]) extends BaseEvolvable[V, Z, Repr](organisms, generation) with Ecological[T, X] with Theocratic[Repr] {
//
////  /**
////    * Method to create an Organism of type Z from a Nucleus
////    *
////    * @param nucleus the Nucleus from which to create an Organism
////    * @return an instance of Z
////    */
////  def createOrganism(nucleus: Nucleus[B]): Z
//
//  /**
//    * Method to signal a change in status for the individual referenced.
//    *
//    * @param z    the individual organism
//    * @param kill true if this organism is to be killed off
//    */
//  def updateOrganism(z: Z, kill: Boolean): Unit = viable.updateOrganism(z, kill)
//
//  /**
//    * Evaluate the fitness of a member of this Evolvable
//    *
//    * @param x the member
//    * @return true if x is fit enough to survive this generation
//    * @throws Exception if the logic to evaluate the fitness of x fails in some unexpected way
//    */
//  def evaluateFitness(x: Z): Boolean = fit.isFit(x,env)
//
//  /**
//    * This method yields a new Evolvable by reproduction.
//    * If the ploidy of X is haploid, then reproduction will be asexual, otherwise mating must occur between male/female pairs.
//    *
//    * CONSIDER returning Iterable rather than Iterator
//    *
//    * @return a new Evolvable
//    */
//  override def offspring: Iterable[Z] =
//    if (reproduction.isSexual)
//      organisms filter (fit.isFit(_,env)) // FIXME implement me
//    else
//      organisms filter (fit.isFit(_,env)) // FIXME implement me
//
//
//  def cullMembers(): Repr = (for (v <- generation.next()) yield build(Nil)(v)).get
//
////  def seedMembers(size: Int, genome: Genome[B, G, P], p_ : Int, random: RNG[B]): Repr = {
////    @tailrec def inner(bns: Seq[Nucleus[B]], br: Random[B], n: Int): (Seq[Nucleus[B]], Random[B]) =
////      if (n == 0) (bns, br)
////      else {
////        val (bn, br_) = genome.recombine(random)
////        inner(bns :+ bn, br_, n - 1)
////      }
////
////    val (bns, _) = inner(Nil, random, size)
////    build(bns map createOrganism, generation)
////  }
//
//  private val fit = implicitly[Fit[Z]]
//  private val reproduction = implicitly[Reproduction[Z]]
//  private val viable = implicitly[Viable[Z]]
//  private val visualization = implicitly[Visualization[Z]]
//
//  private def neonate[B](bs: Sequence[B]): Z = reproduction.neonate(bs)
//
//  /**
//    * Method to seed a this
//    * {@link Evolvable} which a certain number of
//    * members.
//    */
//  override def seedMembers[B](size: Int, bss: Stream[Sequence[B]]): Repr = build(bss map neonate take size, generation)
//}
//
///**
//  * Created by scalaprof on 7/27/16.
//  *
//  * @param name        an identifier for this Colony
//  * @param organisms   a collection of organisms, each of type OrganismType
//  * @param generation  a version representing this generation
//  * @param environment the Environment in which this Colony flourishes
//  * @tparam T the Trait type
//  * @tparam V the generation type (defined to be Incrementable)
//  * @tparam X the Eco-type
//  * @tparam Y the underlying type of the Colony, i.e. the type of the members
//  */
//case class Colony[T, V: Incrementable, X, Y : Fit : Reproduction : Viable : Visualization](name: String, organisms: Iterable[Y], override val generation: Version[V], environment: Environment[T, X]) extends AbstractColony[T, V, X, Y, Colony[T, V, X, Y]](organisms, generation, environment) with SelfAuditing {
//
//  import com.phasmid.darwin.evolution.Random.RandomizableLong
//
////  implicit val idStreamer: Streamer[Long] = Streamer(RNG[Long](0).toStream)
//
////  private val genome = species.genome
////
////  private val visualizer: Visualizer[T, X] = species.visualizer
//
////  def seedMembers(size: Int, random: RNG[B]): Colony[B, T, V, X, Y] = seedMembers(size, genome, 2, random)
//
//  def build(xs: Iterable[Y], v: Version[V]): Colony[T, V, X, Y] = new Colony(name, xs, v, environment)
//
////  private val builder = implicitly[OrganismBuilder[Y]]
//
////  def createOrganism(nucleus: Nucleus[B]): Y = {
////    val result = builder.build(generation, species, nucleus, environment)
////    visualizer.createAvatar(result)
////    result
////  }
//
////  def updateOrganism(o: Y, kill: Boolean): Unit = {
////    val yv = implicitly[Visualization[Y]]
////    if (kill) yv.destroyAvatar(o)
////    else yv.updateAvatar(o)
////  }
//
//  def apply(phenotype: Phenotype[T]): Adaptatype[X] = throw GeneticsException("apply not implemented") // FIXME implement me (??)
//
//  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[Colony[Any, Any, Any, Organism[Any, Any]]])(indent)
//
//  override def toString: String = s"$name generation $generation with ${organisms.size} organisms"
//
//  // CONSIDER removing the parameter tab from the invocation: it isn't really needed (in all defs of render)
//  //  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Colony[Any, Any, Any, Any, Long, Any, SexualAdaptedOrganism[B,G,T,V,X]]]).render(indent)(tab)
//
//}
//
////trait ColonyBuilder[Y] {
////
////  def build[B, G, P, T, V, X, Z](name: String, generation: Version[V], species: Species[B, G, P, T, X], ecology: Ecology[T, X]): Y
////}
//
//object Colony {
//
//  def apply[B, G, T, V: Incrementable, X, Y : Fit : Reproduction : Visualization : Viable](name: String, generation: Version[V], environment: Environment[T, X]): Colony[T, V, X, Y] = Colony(name, Nil, generation, environment)
//
//  def apply[B, G, T, X, Y : Fit : Reproduction : Visualization : Viable](name: String, environment: Environment[T, X]): Colony[T, Long, X, Y] = apply(name, Version.longVersion("0"), environment)
//
//}
//
//trait Viable[Z] {
//  def updateOrganism(z: Z, kill: Boolean): Unit
//
//}