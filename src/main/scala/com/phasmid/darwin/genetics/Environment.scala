package com.phasmid.darwin.genetics

/**
  * @tparam X underlying type of Environment
  *
  * Created by scalaprof on 5/5/16.
  */
case class Environment[X](name: String, factors: EcoFactor[X]) extends Identifier

/**
 * @author scalaprof
 *
 * @tparam X underlying type of EcoFactor
 */
trait EcoFactor[X] extends (() => X) with Identifier

