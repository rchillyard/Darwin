package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
package object dna {
  /**
    * DNA is a type alias for Strand[Base] with the understanding that, although in nature, there are two strands (i.e. the double helix),
    * since they are (usually) identical, we just consider it as one strand.
    */
  type DNA = Strand[Base]
}
