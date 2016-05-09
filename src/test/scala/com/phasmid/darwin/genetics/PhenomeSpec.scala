package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class PhenomeSpec extends FlatSpec with Matchers {

  val ts = Set(Allele("T"), Allele("S"))
  val pq = Set(Allele("P"), Allele("Q"))
  val locus1 = PlainLocus(Location("height", 0, 0), ts, Some(Allele("S")))
  val locus2 = PlainLocus(Location("girth", 1, 0), pq, Some(Allele("P")))
  val gene1 = MendelianGene[Boolean, String](locus1, Seq(Allele("T"), Allele("S")))
  val gene2 = MendelianGene[Boolean, String](locus2, Seq(Allele("P"), Allele("Q")))
  val height = Characteristic("height")
  val girth = Characteristic("girth")
  val traitMapper: (Characteristic, Allele[String]) => Trait[Double] = {
    case (`height`, Allele(h)) => Trait(height, h match { case "T" => 2.0; case "S" => 1.6 })
    case (`girth`, Allele(g)) => Trait(height, g match { case "Q" => 3.0; case "P" => 1.2 })
    case (c, _) => throw new GeneticsException(s"no trait traitMapper for $c")
  }

  // TODO the map should really come from the definition of the phenome itself, i.e. the characteristics.
  val expresser: Expresser[Boolean, String, Double] = new ExpresserMendelian[Boolean, String, Double](traitMapper)

  "apply" should "work" in {
    val genotype = Genotype(Seq(gene1, gene2))
    val phenome: Phenome[Boolean, String, Double] = Phenome("test", Map(locus1 -> height, locus2 -> girth), expresser)
    val phenotype = phenome(genotype)
    phenotype.traits.length shouldBe 2
    phenotype.traits.head shouldBe Trait[Double](height, 1.6)
    phenotype.traits.tail.head shouldBe Trait[Double](height, 1.2)
  }
}
