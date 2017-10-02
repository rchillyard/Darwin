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

package com.phasmid.darwin.run

import com.phasmid.darwin.base.Identifiable
import com.phasmid.darwin.genetics.{Genome, Phenome}
import com.phasmid.darwin.visualization.Visualizer

/**
  * This class models a species by defining a name, the Genome, Phenome and Visualizer that are appropriate for members of this Species.
  *
  * NOTE: while there is no really good definition of a species, here a species is determined by its genome and phenome.
  * Incidentally, each species will have its own visualizer.
  *
  * @param name    the name (String) of this species
  * @param genome  the Genome of this species
  * @param phenome the Phenome of this species
  * @param tXv     the visualizer of this species
  * @tparam B the Base type
  * @tparam G the Gene type
  * @tparam P the Ploidy-type
  * @tparam T the Trait type
  * @tparam X the Eco-type
  */
case class Species[B, G, P, T, X](name: String, genome: Genome[B, G, P], phenome: Phenome[G, P, T])(tXv: Visualizer[T, X]) extends Identifiable {
  def visualizer: Visualizer[T, X] = tXv
}
