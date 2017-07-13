/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

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