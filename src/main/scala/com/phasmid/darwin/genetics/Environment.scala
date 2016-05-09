package com.phasmid.darwin.genetics

/**
  * An Environment is where the fitness of phenotypes (or organisms) is evaluated to determine viability.
  * An Environment is essentially the intersection of a number of EcoFactors, for each of which an organism
  * is evaluated. The fitness of the various eco factors are then combined to generate the overall fitness
  * for the environment.
  *
  * @tparam X underlying type of Environment
  *
  *           Created by scalaprof on 5/5/16.
  */
case class Environment[X](name: String, factors: EcoFactor[X]) extends Identifier

case class EcoFactor[X](factor: Factor, x: X) extends Identifier {
  val name = factor.name
}

