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

import com.phasmid.darwin.base.Auditable
import com.phasmid.darwin.eco._
import com.phasmid.laScala.fp.FP
import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass}

import scala.util.Try

/**
  * This class represents the Adaptations of an Organism.
  *
  * CONSIDER fitness should take an Ecology instead of a Map[String, EcoFactor]
  *
  * Created by scalaprof on 5/9/16.
  */

case class Adaptatype[X](adaptations: Seq[Adaptation[X]]) extends Auditable {
  /**
    * Method to evaluate and blend the fitness of each adaptation into a single fitness, wrapped in Try
    *
    * @param ecology a map of factor keys to EcoFactor instances
    * @return a Fitness, wrapped in Try
    */
  def fitness(ecology: Map[String, EcoFactor[X]]): Try[Fitness] = {
    val ts = for (a <- adaptations; f <- ecology.get(a.factor.name)) yield (a, f)
    assert(ts.nonEmpty, s"the ecology map did not match any adaptations: map keys: ${ecology.keys}; adaptations: $adaptations")
    for (fs <- FP.sequence(for ((a, e) <- ts) yield a(e))) yield Viability(fs)()
  }

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Adaptatype[Any]]).render(indent)(tab)

}

/**
  * This class represents a particular Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from a Trait.
  *
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER simply extending the fitness function
  */
case class Adaptation[X](factor: Factor, ecoFitness: EcoFitness[X]) extends Auditable with EcoFitness[X] {

  def apply(x: EcoFactor[X]): Try[Fitness] = ecoFitness(x)

  override def toString(): String = s"Adaptation($factor, $ecoFitness)"

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Adaptation[Any]]).render(indent)(tab)
}

