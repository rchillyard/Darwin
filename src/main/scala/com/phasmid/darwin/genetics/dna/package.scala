/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
package object dna {
  /**
    * DNA is a type alias for Sequence[Base] with the understanding that, although in nature, there are two strands (i.e. the double helix),
    * since they are (usually) complementary, we just consider it as one strand.
    */
  type DNA = Seq[Base]
}
