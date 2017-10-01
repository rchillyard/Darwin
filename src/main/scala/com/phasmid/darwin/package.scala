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

package com.phasmid

import com.phasmid.darwin.eco.{Factor, FitnessFunction}
import com.phasmid.darwin.genetics.{Adaptation, Adaptatype, Phenotype, Trait}

import scala.util.Try

/**
  * Definition of types which span the entire Darwin code.
  *
  * Created by scalaprof on 7/15/17.
  */
package object darwin {

  /**
    * No... I think what we want to do is to create another model from the Phenotype: an adaptation.
    * This adaptation can then be crossed with an Environment to determine the fitness function.
    *
    * This type models the evaluation of adaptation for a specific Phenotype in an Environment
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type Ecological[TraitType, EcoType] = Phenotype[TraitType] => Adaptatype[EcoType]

  /**
    * This function type is the basis of the success of traits into adaptations. [Yes, I know this needs a better explanation].
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type AdapterFunction[TraitType, EcoType] = (Factor, Trait[TraitType], FitnessFunction[TraitType, EcoType]) => Try[Adaptation[EcoType]]

}
