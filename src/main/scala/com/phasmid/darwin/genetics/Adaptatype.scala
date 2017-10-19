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
import com.phasmid.darwin.eco._
import com.phasmid.laScala.Prefix
import com.phasmid.laScala.fp.FP

import scala.util.Try

/**
  * This class represents the Adaptations of an Organism.
  *
  * Created by scalaprof on 5/9/16.
  */

case class Adaptatype[X](id: Identifier, adaptations: Seq[Adaptation[X]]) extends SelfIdentified(id) with Identifiable {

  /**
    * Method to evaluate and blend the fitness of each adaptation into a single fitness, wrapped in Try
    *
    * CONSIDER extracting this into a trait
    *
    * @param habitat the habitat for which we wish to evaluate fitness
    * @return a Fitness, wrapped in Try
    */
  def fitness(habitat: Habitat[X]): Try[Fitness] = {
    val ts = for (a <- adaptations; f <- habitat.get(a.factor.name)) yield (a, f)
    assert(ts.nonEmpty, s"the ecology map did not match any adaptations: map keys: ${habitat.keys}; adaptations: $adaptations")
    for (fs <- FP.sequence(for ((a, e) <- ts) yield a(e))) yield Viability(fs)()
  }

  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[Adaptatype[Any]])(indent)

  // TODO this should not be necessary
  override def toString: String = s"Adaptatype($id, $adaptations)"
}

/**
  * This class represents a particular Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from a Trait.
  *
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER simply extending the fitness function
  *
  * CONSIDER use Identifying
  */
case class Adaptation[X](factor: Factor, ecoFitness: EcoFitness[X]) extends NamedFunction[EcoFactor[X], Try[Fitness]](s"adaptation for $factor", ecoFitness) with Auditable with EcoFitness[X] {

  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this.asInstanceOf[Adaptation[Any]])(indent)
}

