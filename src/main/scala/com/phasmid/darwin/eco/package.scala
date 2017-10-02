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

package com.phasmid.darwin

import scala.util.Try

/**
  * Created by scalaprof on 7/14/17.
  */
package object eco {
  /**
    * This function type is the type of a parameter of an Adaptation. In the context of an Adapter, this function
    * will yield, for a given EcoType, its Fitness (wrapped in Try)
    *
    * @tparam EcoType the underlying type of the ecological types such as Environment
    */
  type EcoFitness[EcoType] = EcoFactor[EcoType] => Try[Fitness]

  /**
    * This function type is the type of a parameter of an Adapter. For a tuple of trait value, function "type", and eco factor value.
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type FitnessFunction[TraitType, EcoType] = (TraitType, ShapeFunction[TraitType, EcoType], EcoType) => Fitness

  /**
    * A Habitat is defined as a set of EcoFactors, indexed by name
    *
    * @tparam EcoType the type of the EcoFactors present in this Habitat
    */
  type Habitat[EcoType] = Map[String, EcoFactor[EcoType]]

}
