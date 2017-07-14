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

import com.phasmid.darwin.genetics.Identifier

/**
  * An Environment is where the fitness of phenotypes (or organisms) is evaluated to determine viability.
  * An Environment is essentially the intersection of a number of EcoFactors, for each of which an organism
  * is evaluated. The fitness of the various eco factors are then combined to generate the overall fitness
  * for the environment.
  *
  * @tparam X underlying type of Environment
  *
  *           Created by scalaprof on 5/5/16.
  */
case class Environment[X](name: String, factors: EcoFactor[X]) extends Identifier

case class EcoFactor[X](factor: Factor, x: X) extends Identifier {
  val name: String = factor.name
}
