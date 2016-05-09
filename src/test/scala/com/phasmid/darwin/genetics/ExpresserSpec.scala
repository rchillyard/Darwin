package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class ExpresserSpec extends FlatSpec with Matchers {

  val height = Characteristic("height")
  val tall = Trait[Double](height, 2.0)
  val short = Trait[Double](height, 1.6)

  "apply" should "work" in {
    val gene = new Gene[Boolean, String] {
      override def locus: Locus[String] = UnknownLocus(Location("test", 0, 0))

      override def name: String = "test"

      override def apply(b: Boolean): Allele[String] = if (b) Allele("T") else Allele("S")

      override def distinct: Product = Allele("T")

      override def toString = s"gene $name at $locus with alleles: ${apply(true)} and ${apply(false)} and distinct: $distinct"
    }

    val exp = new Expresser[Boolean, String, Double] {
      // XXX a rather simple non-Mendelian selector which determines the allele simply from one specific alternative of the gene
      override def selectAllele(gene: Gene[Boolean, String]): Allele[String] = gene(true)

      val traitMapper: (Characteristic, Allele[String]) => Trait[Double] = {
        case (_, Allele(h)) => Trait(height, h match { case "T" => 2.0; case "S" => 1.6 })
        case (c, _) => throw new GeneticsException(s"traitMapper failed for $c")
      }
    }
    exp.apply(height, gene) shouldBe tall
  }
  it should "work for Mendelian expression" in {
    val ts = Set(Allele("T"), Allele("S"))
    val pq = Set(Allele("P"), Allele("Q"))
    val locus1 = PlainLocus(Location("height", 0, 0), ts, Some(Allele("S")))
    val locus2 = PlainLocus(Location("girth", 0, 0), pq, Some(Allele("P")))
    val gene1 = MendelianGene[Boolean, String](locus1, Seq(Allele("T"), Allele("S")))
    val girth = Characteristic("girth")
    val traitMapper: (Characteristic, Allele[String]) => Trait[Double] = {
      case (`height`, Allele(h)) => Trait(height, h match { case "T" => 2.0; case "S" => 1.6 })
      case (`girth`, Allele(g)) => Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 })
      case (c, _) => throw new GeneticsException(s"traitMapper failed for $c")
    }
    val exp = ExpresserMendelian[Boolean, String, Double](traitMapper)
    exp.apply(height, gene1) shouldBe short
  }
}

case class UnknownLocus[G](location: Location) extends Locus[G] {
  override def dominant: Option[Allele[G]] = None

  override def apply: Set[Allele[G]] = Set()
}
