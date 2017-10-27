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

import com.phasmid.darwin.AdapterFunction
import com.phasmid.darwin.eco._
import com.phasmid.laScala.fp.NamedFunction

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER defining the function as a type in package
  *
  * XXX why is this sealed?
  */
sealed trait Adapter[T, X] extends AdapterFunction[T, X] {

  def matchFactors(f: Factor, t: Trait[T]): Try[(T, ShapeFunction[T, X])]

}

/**
  * Abstract base class implements Adapter[T,X]
  *
  * CONSIDER extending NamedFunction based on a more useful function type, in particular, this is really a function3, not a function1
  *
  * @tparam T the trait type
  * @tparam X the eco-type
  */
abstract class AbstractAdapter[T, X](name: String) extends NamedFunction[Unit, Unit](name, { _ => () }) with Adapter[T, X] {

  def apply(factor: Factor, `trait`: Trait[T], ff: FitnessFunction[T, X]): Try[Adaptation[X]] = Adapter.applyAdapterFunction(factor, `trait`, ff)(matchFactors)

  override def toString(): String = super[NamedFunction].toString()

}

object Adapter {
  def applyAdapterFunction[T, X](factor: Factor, `trait`: Trait[T], ff: FitnessFunction[T, X])(mf: (Factor, Trait[T]) => Try[(T, ShapeFunction[T, X])]): Try[Adaptation[X]] = {
    // TODO tidy this all up nicely
    val fc: T => (ShapeFunction[T, X]) => X => Fitness = ff.curried
    val x_f_t: Try[X => Fitness] = for ((t, s) <- mf(factor, `trait`)) yield fc(t)(s)

    def f_xe_fo(ef: EcoFactor[X]): Try[Fitness] = x_f_t match {
      // TODO need to check the factor types
      case Success(f) => Success(f(ef.x))
      case Failure(t) => Failure(t)
    }

    // TODO this doesn't look right at all! See definition of f_xe_fo above.
    x_f_t map { _ => Adaptation(factor, f_xe_fo) }
  }

}