package com.phasmid.darwin.evolution

/**
  * Created by scalaprof on 1/7/17.
  */
case class EvolutionException(s: String, cause: Throwable) extends Exception(s, cause)

object EvolutionException {
  def apply(s: String): EvolutionException = apply(s, null)
}
