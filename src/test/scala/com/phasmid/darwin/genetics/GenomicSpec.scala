package com.phasmid.darwin.genetics.dna

import com.phasmid.darwin.genetics.Allele
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */
class GenomicSpec extends FlatSpec with Matchers {

  "Allele" should "work" in {
    val x = Allele("x",Cytosine)
    x should matchPattern {case Allele("x",Seq(Cytosine)) =>}
  }
}