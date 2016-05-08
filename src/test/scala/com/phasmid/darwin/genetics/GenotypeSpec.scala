package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Base, Cytosine, Guanine}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author scalaprof
 */
class GenotypeSpec extends FlatSpec with Matchers {

  /**
    * A diploid gene which extends Gene[Boolean,String]
    * @param locus the locus on the chromosome where this gene can be found
    * @param alleles the two alleles of this (diploid) gene
    */
  case class GeneDiploidString(locus: Locus, alleles: (Allele[String],Allele[String])) extends Gene[Boolean,String] {
    def apply(p: Boolean): Allele[String] = if (p) alleles._1 else alleles._2
    val name = locus.name
    override def distinct: Product = (alleles._1)
  }

  "Gene" should "have 2 Alleles" in {
    val locus = Locus("hox",0,0)
    val x = GeneDiploidString(locus,(Allele("x"),Allele("y")))
    x(true) shouldBe Allele("x")
  }
}