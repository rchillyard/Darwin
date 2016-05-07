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
  "locate" should "fail for bad Locus" in {
    val x = Strand.create(Cytosine)
    val r = x.locate(Locus("",-1,0))
    r should matchPattern { case None => }
  }
  "concatenation" should "work with +: base" in {
    val x = Strand.create(Cytosine)
    val r = x :+ Guanine
    r shouldBe Strand(Seq(Cytosine,Guanine))
  }
  it should "work with :+ Seq" in {
    val x = Strand.create(Cytosine)
    val r = x :+ Seq(Guanine)
    r shouldBe Strand(Seq(Cytosine,Guanine))
  }
  it should "work with base +: " in {
    val x = Strand.create(Cytosine)
    val r = Guanine +: x
    r shouldBe Strand(Seq(Guanine,Cytosine))
  }
  it should "work with Seq +:" in {
    val x = Strand.create(Cytosine)
    val r = Seq(Guanine) +: x
    r shouldBe Strand(Seq(Guanine,Cytosine))
  }
  it should "work with :+ Strand" in {
    val x = Strand.create(Cytosine)
    val y = Strand.create(Guanine)
    val r = x :+ y
    r shouldBe Strand(Seq(Cytosine,Guanine))
  }
  it should "work with Strand +:" in {
    val x = Strand.create(Cytosine)
    val y = Strand.create(Guanine)
    val r = x +: y
    r shouldBe Strand(Seq(Cytosine,Guanine))
  }
}