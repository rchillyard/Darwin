/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER defining the function as a type in package
  *
  * XXX why is this sealed?
  */
sealed trait Adapter[T, X] extends AdapterFunction[T, X] {

  def matchFactors(f: Factor, t: Trait[T]): Try[(T, FunctionShape[T, X])]

  override def apply(factor: Factor, `trait`: Trait[T], ff: FitnessFunction[T, X]): Try[Adaptation[X]] = {
    // TODO tidy this all up nicely
    val fc: (T) => (FunctionShape[T, X]) => (X) => Fitness = ff.curried
    val x_f_t: Try[(X) => Fitness] = for ((t, s) <- matchFactors(factor, `trait`)) yield fc(t)(s)

    def f_xe_fo(ef: EcoFactor[X]): Try[Fitness] = x_f_t match {
      // TODO need to check the factor types
      case Success(f) => Success(f(ef.x))
      case Failure(t) => Failure(t)
    }

    x_f_t map { _ => Adaptation(factor, f_xe_fo) }
  }
}

abstract class AbstractAdapter[T, X] extends Adapter[T, X]
