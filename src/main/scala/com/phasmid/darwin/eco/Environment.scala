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

package com.phasmid.darwin.eco

import com.phasmid.darwin.base.{CaseIdentifiable, Identifiable, Identifying}
import com.phasmid.laScala.{OldRenderableCaseClass, Prefix}

/**
  * TODO redefine this: it should be a pair (or collection) of Ecologies, where there are boundaries between pairs.
  *
  * TODO there should be a type, similar to Genome/Phenome, perhaps called Biome, that defines the characteristics of an eco system, and a another type like Biotype that actually defines those characteristics for a specific environment.
  *
  * An Environment is where the fitness of phenotypes (or organisms) is evaluated to determine viability.
  * An Environment is essentially the intersection of a number of EcoFactors, for each of which an organism
  * is evaluated. The fitness of the various eco factors are then combined to generate the overall fitness
  * for the environment.
  *
  * @tparam X underlying type of Environment
  *
  *           Created by scalaprof on 5/5/16.
  */
case class Environment[T, X](name: String, ecology: Ecology[T, X], habitat: Habitat[X]) extends Identifying with Identifiable {
  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[Environment[Any, Any]])(indent)
}

trait Environmental[T, X] {
  def environment: Environment[T, X]
}

case class EcoFactor[X](factor: Factor, x: X) extends Identifiable {
  val name: String = factor.name

  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = OldRenderableCaseClass(this.asInstanceOf[EcoFactor[Any]]).render(indent)(tab)
}
