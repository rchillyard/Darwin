package com.phasmid.darwin.genetics.dna

import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */

class DNASpec extends FlatSpec with Matchers {

  "base names" should "be correct" in {
    Cytosine.name shouldBe "C"
    Guanine.name shouldBe "G"
    Thymine.name shouldBe "T"
    Adenine.name shouldBe "A"
  }
  "bases" should "derive from String" in {
    Base('C') shouldBe Cytosine
    Base('T') shouldBe Thymine
    Base('G') shouldBe Guanine
    Base('A') shouldBe Adenine
    Base('X') shouldBe Invalid('X')
  }
  "base pairs" should "be correct" in {
    Cytosine.pair shouldBe Guanine
    Thymine.pair shouldBe Adenine
  }

}