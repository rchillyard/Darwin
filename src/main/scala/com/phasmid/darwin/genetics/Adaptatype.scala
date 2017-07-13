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

/**
  * This class represents the Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from the Phenotype with respect to
  * an "Ecology"
  *
  * Created by scalaprof on 5/9/16.
  */
case class Adaptatype[X](adaptations: Seq[Adaptation[X]])

/**
  * This class represents a particular Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from a Trait.
  *
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER simply extending the fitness function
  */
case class Adaptation[X](factor: Factor, ecoFitness: EcoFitness[X])

