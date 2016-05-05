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

//  "DNA(CTG)" should "appear as CTG" in {
//    val dna = DNA("CTG")
//    dna.toString should be ("CTG")
//  }
//  it should "be CTGAG when combined with AG" in {
//    val dna = DNA("CTG")++DNA("AG")
//    dna.toString should be ("CTGAG")
//  }
//  it should "be list (reverse order) when zipped" in {
//    val zip = DNA("CTG") zip DNA("AGC")
//    zip should be (List((Guanine,Cytosine),(Thymine,Guanine),(Cytosine,Adenine)))
//  }
//  it should "distance 2 from AGG" in {
//    val dist = DNA("CTG") euclidean DNA("AGG")
//    dist should be (2)
//  }
//  it should "have 2 base pairs" in {
//    val dna = DNA("CTG")
//    dna.basePairs should be (3)
//  }
}