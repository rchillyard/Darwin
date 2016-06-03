package com.phasmid.darwin.genetics

/**
  * Fitness is a measure of the viability of an organism's phenotype adapting to an environment.
  * It's a Double value and should be in the range 0..1
  *
  * Created by scalaprof on 5/5/16.
  */
case class Fitness(x: Double) {
  require(x >= 0.0 && x <= 1.0, s"invalid Fitness: $x must be in range 0..1")

  def *(other: Fitness): Fitness = Fitness(x * other.x)
}

case class FunctionType[T,X](shape: String, f: (T,X)=>Fitness)

object Fitness {
  val delta = FunctionType[Double,Double]("delta",{(t,x) => if (t >= x) Fitness(1) else Fitness(0)})
  val inverseDelta = FunctionType[Double,Double]("delta-inv",{(t,x) => if (t < x) Fitness(1) else Fitness(0)})
}