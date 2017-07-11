package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/6/16.
  */
case class GeneticsException(s: String, cause: Throwable) extends Exception(s, cause)

object GeneticsException {
  def apply(s: String): GeneticsException = apply(s, null)
}
