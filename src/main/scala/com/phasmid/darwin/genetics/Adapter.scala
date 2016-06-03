package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER defining the function as a type in package
  */
sealed trait Adapter[T, X] extends AdapterFunction[T,X] {

  def matchFactors(f: Factor, t: Trait[T]): Option[(T, FunctionType[T,X])]

  override def apply(factor: Factor, `trait`: Trait[T], ff: FitnessFunction[T, X]): Option[Adaptation[X]] = {
    // TODO tidy this all up nicely
    val fc: (T) => (FunctionType[T,X]) => (X) => Fitness = ff.curried
    val f_x_f_o: Option[(X) => Fitness] = for ((t, s) <- matchFactors(factor, `trait`)) yield fc(t)(s)
    def f_xe_fo(ef: EcoFactor[X]): Option[Fitness] = f_x_f_o match {
      // TODO need to check the factor types
      case Some(f) => Some(f(ef.x))
      case _ => None
    }
    f_x_f_o map { f => Adaptation(factor, f_xe_fo) }
  }
}

abstract class AbstractAdapter[T, X] extends Adapter[T, X]
