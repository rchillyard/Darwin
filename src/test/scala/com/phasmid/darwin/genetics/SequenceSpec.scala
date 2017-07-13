/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class SequenceSpec extends FlatSpec with Matchers {

  "Sequence" should "work for one base" in {
    val x = Sequence.create(Cytosine)
    x should matchPattern { case Sequence(Seq(Cytosine)) => }
    x.toString shouldBe "C"
  }
  it should "work for two bases" in {
    val x = Sequence.create(Cytosine, Guanine)
    x should matchPattern { case Sequence(Seq(Cytosine, Guanine)) => }
    x.bases.size shouldBe 2
    x.toString shouldBe "CG"
  }
  "locate" should "fail for bad Location" in {
    val x = Sequence.create(Cytosine)
    val r = x.locate(Location("", -1, 0))
    r should matchPattern { case None => }
  }
  "concatenation" should "work with +: base" in {
    val x = Sequence.create(Cytosine)
    val r = x :+ Guanine
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with :+ Seq" in {
    val x = Sequence.create(Cytosine)
    val r = x :+ Seq(Guanine)
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with base +: " in {
    val x = Sequence.create(Cytosine)
    val r = Guanine +: x
    r shouldBe Sequence(Seq(Guanine, Cytosine))
  }
  it should "work with Seq +:" in {
    val x = Sequence.create(Cytosine)
    val r = Seq(Guanine) +: x
    r shouldBe Sequence(Seq(Guanine, Cytosine))
  }
  it should "work with :+ Sequence" in {
    val x = Sequence.create(Cytosine)
    val y = Sequence.create(Guanine)
    val r = x :+ y
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
  it should "work with Sequence +:" in {
    val x = Sequence.create(Cytosine)
    val y = Sequence.create(Guanine)
    val r = x +: y
    r shouldBe Sequence(Seq(Cytosine, Guanine))
  }
}