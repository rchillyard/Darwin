/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.evolution

/**
  * Created by scalaprof on 1/7/17.
  */
case class EvolutionException(s: String, cause: Throwable) extends Exception(s, cause)

object EvolutionException {
  def apply(s: String): EvolutionException = apply(s, null)
}
