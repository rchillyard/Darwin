package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/7/16.
  *
  * @tparam P the ploidy type
  * @tparam G the gene type
  */
trait Expresser[P,G]{
  type T
  def apply(gene: Gene[P,G]): Trait[T]
}
