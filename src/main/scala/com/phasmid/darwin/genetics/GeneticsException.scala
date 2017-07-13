/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/6/16.
  */
case class GeneticsException(s: String, cause: Throwable) extends Exception(s, cause)

object GeneticsException {
  def apply(s: String): GeneticsException = apply(s, null)
}
