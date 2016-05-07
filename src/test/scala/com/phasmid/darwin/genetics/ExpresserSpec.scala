package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class ExpresserSpec extends FlatSpec with Matchers {

  val tall = Trait[Double](2.0)
  val short = Trait[Double](1.6)
  val gene = new Gene[Boolean,String] {
    override def name: String = "test"
    override def apply(b: Boolean): Allele[String] = if (b) Allele("T") else Allele("S")
    override def distinct: Product = (Allele("T"))
  }

  "apply" should "work" in {
    // XXX a rather simple Expresser which determines the trait simply from one specific alternative
    // of the gene
    val exp = new Expresser[Boolean,String] {
      override type T = Double
      override def apply(gene: Gene[Boolean, String]): Trait[Double] = gene(true) match {
        case Allele("T") => tall
        case Allele("S") => short
      }
    }
    exp.apply(gene) shouldBe tall
  }
  it should "work for Mendelian expression" in {
    val exp = new Expresser[Boolean,String] {
      override type T = Double
      override def apply(gene: Gene[Boolean, String]): Trait[Double] = gene.distinct match {
        case (Allele("T")) => tall // recessive
        case _ => short // dominant
      }
    }
    exp.apply(gene) shouldBe tall
  }
}
