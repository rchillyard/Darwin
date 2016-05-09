package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER defining the function as a type in package
  */
sealed trait Adapter[T, X] extends ((Factor, Trait[T], FitnessFunction[T, X]) => Option[Adaptation[X]]) {

  def matchFactors(f: Factor, t: Trait[T]): Option[(T, String)]

  override def apply(factor: Factor, `trait`: Trait[T], ff: FitnessFunction[T, X]): Option[Adaptation[X]] = {
    // TODO tidy this all up nicely
    val ft: (T) => (String) => (X) => Fitness = ff.curried
    val fo: Option[(X) => Fitness] = for ((t, s) <- matchFactors(factor, `trait`)) yield ft(t)(s)
    def jj(ef: EcoFactor[X]): Option[Fitness] = fo match {
      // need to check the factor types
      case Some(f) => Some(f(ef.x))
      case _ => None
    }
    fo map { f => Adaptation(factor, jj) }
  }
}

abstract class AbstractAdapter[T, X] extends Adapter[T, X]
