package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class PhenomeSpec extends FlatSpec with Matchers {

  val locus1 = PlainLocus(Location("height",0,0),Seq(Allele("T"),Allele("S")),Some(Allele("S")))
  val locus2 = PlainLocus(Location("girth",1,0),Seq(Allele("P"),Allele("Q")),Some(Allele("P")))
  val gene1 = MendelianGene[Boolean,String](locus1,Seq(Allele("T"),Allele("S")))
  val gene2 = MendelianGene[Boolean,String](locus2,Seq(Allele("P"),Allele("Q")))
  val height = Characteristic("height")
  val girth = Characteristic("girth")
  val tall = Trait[Double](height,2.0)
  val short = Trait[Double](height,1.6)
  val fat = Trait[Double](girth,3.0)
  val thin = Trait[Double](girth,1.2)
  val mapper: (Locus[String],Allele[String])=>Trait[Double] = {
    case (`locus1`,Allele("T")) => tall
    case (`locus1`,Allele("S")) => short
    case (`locus2`,Allele("Q")) => fat
    case (`locus2`,Allele("P")) => thin
    case (l,a) => throw new GeneticsException(s"no mapper for ($l,$a)")
  }

  // TODO the map should really come from the definition of the phenome itself, i.e. the characteristics.
  val expresser: Expresser[Boolean, String, Double] = new ExpresserMendelian[Boolean, String, Double](mapper)

  "apply" should "work" in {
    val genotype = Genotype(Seq(gene1,gene2))
    val phenome = Phenome("test",Seq(),expresser)
    val phenotype = phenome(genotype)
    println(phenotype)
    phenotype.traits.length shouldBe 2
    phenotype.traits.head shouldBe short
    phenotype.traits.tail.head shouldBe thin
  }
}
