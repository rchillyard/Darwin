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

import com.phasmid.darwin.genetics._
import com.phasmid.laScala.fp.FP.sequence

/**
  * Created by scalaprof on 5/9/16.
  */
case class Ecology[T, X](name: String, factors: Map[String, Factor], fitness: FitnessFunction[T, X], adapter: Adapter[T, X]) extends Ecological[T, X] with Identifier {

  /**
    * The apply method for this Ecology. For each Trait in the given Phenotype, we look up its corresponding Factor
    * and invoke the Adapter to create an Adaptation.
    *
    * Note that if the lookup fails, we simply ignore the trait without warning.
    *
    * @param phenotype the phenotype for which we want to measure the adaptation to this ecology
    * @return an Adaptatype
    */
  def apply(phenotype: Phenotype[T]): Adaptatype[X] = {
    val xats = for (t <- phenotype.traits; f <- factors.get(t.characteristic.name)) yield for (a <- adapter(f, t, fitness)) yield a
    Adaptatype(sequence(xats).get)
  }
}

case class Factor(name: String) extends Identifier
