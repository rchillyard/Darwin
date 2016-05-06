package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
case class Fitness(x: Double) {
  require(x>=0.0 && x<=1.0,s"invalid Fitness: $x must be in range 0..1")
  def * (other: Fitness): Fitness = Fitness(x*other.x)
}

trait FitnessFunction[X,T] extends ((EcoFactor[X],Trait[T])=>Fitness)