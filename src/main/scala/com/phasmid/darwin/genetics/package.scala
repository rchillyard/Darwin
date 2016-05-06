package com.phasmid.darwin

import com.phasmid.darwin.genetics.dna.Base

/**
  * Created by scalaprof on 5/5/16.
  */
package object genetics {
  trait Identifier {
    def name: String
    override def toString = name
  }
  type MultiStrand[A] = Seq[Strand[A]]
}
