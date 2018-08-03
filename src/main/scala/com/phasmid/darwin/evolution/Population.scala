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
//import com.phasmid.darwin.eco.Ecology
//import com.phasmid.darwin.run.Species
//import com.phasmid.laScala.Version
//import com.phasmid.laScala.values.Incrementable
//
///**
//  * Created by scalaprof on 9/30/17.
//  *
//  * @param name     an identifier for this Population
//  * @param colonies the colonies which belong to this Population
//  * @param version  a version representing this generation
//  * @param ecology  an Ecology for which the members of this Population are adapted
//  * @param species  the Species of the organisms represented in this Population
//  * @tparam B the Base type
//  * @tparam G the Gene type
//  * @tparam P the Ploidy type
//  * @tparam T the Trait type
//  * @tparam V the generation type (defined to be Incrementable)
//  * @tparam X the underlying type of the xs
//  */
//case class Population[B, G, P, T, V: Incrementable, X, Z <: Organism[R, V], Y <: Colony[T, V, X, Z] : ColonyBuilder](name: String, colonies: Iterable[Y], version: Version[V], ecology: Ecology[T, X], species: Species[B, G, P, T, X]) extends BaseGenerational[V, Population[B, G, P, T, V, X, Z, Y]](version) {
//  val cb: ColonyBuilder[Y] = implicitly[ColonyBuilder[Y]]
//  val vi: Incrementable[V] = implicitly[Incrementable[V]]
//  /**
//    * Method to yield the next generation of this Population
//    *
//    * @param v the Version for the next generation
//    * @return the next generation of this Population as a Repr
//    */
//  def next(v: Version[V]): Population[B, G, P, T, V, X, Z, Y] = {
//    // TODO: remove this use of asInstanceOf -- it should not be necessary
//    val zs = for (c <- colonies) yield c.next(v).asInstanceOf[Y]
//    Population[B, G, P, T, V, X, Z, Y](name, zs, v, ecology, species)(vi, cb)
//  }
//
//}
