package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */
class StrandSpec extends FlatSpec with Matchers {

  "Strand" should "work for one base" in {
    val x = Strand.create(Cytosine)
    x should matchPattern {case Strand(Seq(Cytosine)) =>}
    x.toString shouldBe "C"
  }
  it should "work for two bases" in {
    val x = Strand.create(Cytosine, Guanine)
    x should matchPattern {case Strand(Seq(Cytosine, Guanine)) =>}
    x.bases.size shouldBe 2
    x.toString shouldBe "CG"
  }
}