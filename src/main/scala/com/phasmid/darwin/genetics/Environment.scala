package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
case class Environment[X](name: String, factors: EcoFactor[X]) extends Identifier

trait EcoFactor[X] extends (() => X) with Identifier

