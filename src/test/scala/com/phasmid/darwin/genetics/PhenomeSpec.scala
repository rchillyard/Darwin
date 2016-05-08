package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class PhenomeSpec extends FlatSpec with Matchers {

  val gene1 = MendelianGene[Boolean,String](Locus("test1",0,0),Seq(Allele("T"),Allele("S")),Allele("S"))
  val gene2 = MendelianGene[Boolean,String](Locus("test2",0,0),Seq(Allele("P"),Allele("Q")),Allele("P"))
  val tall = Trait[Double](2.0)
  val short = Trait[Double](1.6)
  val fat = Trait[Double](3.0)
  val thin = Trait[Double](1.2)
  val mapper: Allele[String]=>Trait[Double] = {a => a match {
    case Allele("T") => tall
    case Allele("S") => short
    case Allele("Q") => fat
    case Allele("P") => thin
  }}

  val expresser: Expresser[Boolean, String, Double] = new ExpresserMendelian[Boolean, String, Double](mapper)

  "apply" should "work" in {
    val genotype = Genotype(Seq(gene1,gene2))
    val phenome = Phenome("test",Seq(),expresser)
    val phenotype = phenome(genotype)
    phenotype.traits.length shouldBe 2
    phenotype.traits.head shouldBe short
    phenotype.traits.tail.head shouldBe fat
  }
}
