package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class ExpresserSpec extends FlatSpec with Matchers {

  val tall = Trait[Double](Characteristic("height"),2.0)
  val short = Trait[Double](Characteristic("girth"),1.6)

  "apply" should "work" in {
    val gene = new Gene[Boolean,String] {
      override def locus: Locus[String] = UnknownLocus(Location("test",0,0))
      override def name: String = "test"
      override def apply(b: Boolean): Allele[String] = if (b) Allele("T") else Allele("S")
      override def distinct: Product = Allele("T")
      override def toString = s"gene $name at $locus with alleles: ${apply(true)} and ${apply(false)} and distinct: $distinct"
    }
    // XXX a rather simple Expresser which determines the trait simply from one specific alternative
    // of the gene
    val exp = new Expresser[Boolean,String,Double] {
      val mapper: (Locus[String],Allele[String])=>Trait[Double] = {
        case (_,Allele("T")) => tall
        case (_,Allele("S")) => short
      }
      override def apply(gene: Gene[Boolean, String]): Trait[Double] = mapper(gene.locus,gene(true))
    }
    exp.apply(gene) shouldBe tall
  }
  it should "work for Mendelian expression" in {
    val locus1 = PlainLocus(Location("height",0,0),Seq(Allele("T"),Allele("S")),Some(Allele("S")))
    val locus2 = PlainLocus(Location("girth",0,0),Seq(Allele("P"),Allele("Q")),Some(Allele("P")))
    val gene1 = MendelianGene[Boolean,String](locus1,Seq(Allele("T"),Allele("S")))
   val exp = ExpresserMendelian[Boolean, String, Double] {
     case (`locus1`,Allele("T")) => tall
     case (`locus1`,Allele("S")) => short
   }
    exp.apply(gene1) shouldBe short
  }
}

case class UnknownLocus[G](location: Location) extends Locus[G] {
  override def dominant: Option[Allele[G]] = None
  override def apply: Seq[Allele[G]] = Seq()
}
