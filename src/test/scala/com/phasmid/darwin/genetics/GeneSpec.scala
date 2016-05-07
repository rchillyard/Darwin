package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Base, Cytosine, Guanine}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */
class GeneSpec extends FlatSpec with Matchers {

  "Gene" should "have 2 Alleles" in {
    val locus = Locus("hox",0,0)
    val x = GeneDiploidString(locus,(Allele("x"),Allele("y")))
    x(true) shouldBe Allele("x")
  }
}