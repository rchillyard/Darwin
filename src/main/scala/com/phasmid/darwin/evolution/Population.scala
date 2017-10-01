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

import com.phasmid.darwin.eco.{EcoFactor, Ecology}
import com.phasmid.darwin.genetics.{Genome, Phenome}
import com.phasmid.darwin.visualization.Visualizer
import com.phasmid.laScala.Version
import com.phasmid.laScala.values.Incrementable

/**
  * Created by scalaprof on 9/30/17.
  *
  * @param name       an identifier for this Population
  * @param colonies   the colonies which belong to this Population
  * @param version    a version representing this generation
  * @param ecology    an Ecology type for this Population
  * @param ecoFactors the actual ecology in which this Population flourishes
  * @param genome     the Genome of the organisms represented in this Population
  * @param phenome    the Phenome of the organisms represented in this Population
  * @tparam B the Base type
  * @tparam G the Gene type
  * @tparam T the Trait type
  * @tparam V the generation type (defined to be Incrementable)
  * @tparam X the underlying type of the xs
  */
case class Population[B, G, T, V: Incrementable, X](name: String, colonies: Iterable[Colony[B, G, T, V, X]], version: Version[V], visualizer: Visualizer[T, X], ecology: Ecology[T, X], ecoFactors: Map[String, EcoFactor[X]], genome: Genome[B, G, Boolean], phenome: Phenome[G, Boolean, T]) extends BaseGenerational[V, Population[B, G, T, V, X]](version) {
  /**
    * Method to yield the next generation of this Population
    *
    * @param v the Version for the next generation
    * @return the next generation of this Population as a Repr
    */
  def next(v: Version[V]): Population[B, G, T, V, X] = build(v, for (c <- colonies) yield c.next(v))

  /**
    *
    * @param v  the Version for the next generation
    * @param cs the colonies which will make up the next generation
    * @return the next generation of this Population as a Repr
    */
  private def build(v: Version[V], cs: Iterable[Colony[B, G, T, V, X]]) = {
    for (c <- colonies) visualizer.visualize(c)
    Population(name, cs, v, visualizer, ecology, ecoFactors, genome, phenome)
  }
}
