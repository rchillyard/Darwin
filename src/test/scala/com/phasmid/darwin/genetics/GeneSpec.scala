package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class GeneSpec extends FlatSpec with Matchers {

  /**
    * A diploid gene which extends Gene[Boolean,String]
    *
    * @param location the locus on the chromosome where this gene can be found
    * @param alleles  the two alleles of this (diploid) gene
    */
  case class GeneDiploidString(location: Location, alleles: (Allele[String], Allele[String])) extends Gene[Boolean, String] {
    def apply(p: Boolean): Allele[String] = if (p) alleles._1 else alleles._2

    val name: String = location.name

    override def distinct: Product = alleles._1

    override def locus: Locus[String] = UnknownLocus(Location("test", 0, 0))
  }

  "Gene" should "have 2 Alleles" in {
    val location = Location("hox", 0, 0)
    val x = GeneDiploidString(location, (Allele("x"), Allele("y")))
    x(true) shouldBe Allele("x")
  }
}