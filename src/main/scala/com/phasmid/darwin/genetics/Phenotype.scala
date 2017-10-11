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

package com.phasmid.darwin.genetics

import com.phasmid.darwin.base._

/**
  * This class represents the Phenotype for an Organism. The Phenotype is "expressed" from the Genotype with respect to
  * the Phenome of the Organism.
  *
  * @param traits all the Traits that make up this Phenotype
  * @tparam T the underlying type of the Traits
  * @author scalaprof
  *         Created by scalaprof on 5/5/16.
  */
case class Phenotype[T](id: Identifier, traits: Seq[Trait[T]]) extends CaseIdentifiable[Phenotype[Any]] {
  def name: String = id.name
}

/**
  *
  * @param characteristic the Characteristic of this Trait
  * @param value          the T value of this Trait
  * @tparam T the underlying type of the Trait
  */
case class Trait[T](characteristic: Characteristic, value: T) extends CaseIdentifiable[Trait[Any]] with Auditable {
  def isSexuallySelective: Boolean = characteristic.isSexuallySelective


  //  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Trait[Any]]).render(indent)(tab)
  def name: String = s"$value@$characteristic"
}
