package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Base, Cytosine, Guanine}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */
class GenomicSpec extends FlatSpec with Matchers {

//  "Allele" should "work for one base" in {
//    val x = Allele.create("x",Cytosine)
//    x should matchPattern {case Allele("x",Seq(Cytosine)) =>}
//    x.name shouldBe "x"
//    x.basePairs shouldBe "C"
//  }
//  it should "work for two bases" in {
//    val x = Allele.create("x",Cytosine, Guanine)
//    x should matchPattern {case Allele("x",Seq(Cytosine, Guanine)) =>}
//    x.name shouldBe "x"
//    x.bases.size shouldBe 2
//    x.basePairs shouldBe "CG"
//  }
  "Gene" should "have 2 Alleles" in {
    val x = new Gene[Boolean] {
      def apply(p: Boolean): Allele = if (p) Allele("x") else Allele("y")
    }
    x(true) shouldBe Allele("x")
  }
}