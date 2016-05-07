package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Adenine, Cytosine, Guanine, Thymine}
import org.scalatest.{FlatSpec, Matchers}
import com.phasmid.darwin.genetics.dna._

/**
  * Created by scalaprof on 5/6/16.
  */
class GenomeSpec extends FlatSpec with Matchers {

  val transcriber = new Transcriber {
    // XXX this is a simple 1:1 mapping from bases to alleles
    def transcribe[Base](bs: Strand[Base])(locus: Locus): Allele = bs.locate(locus) match
    {
      case Some(x) => Allele(x.head.toString)
      case None => throw new GeneticsException(s"invalid locus $locus for strand of length ${bs.bases.length}")
      case x => throw new GeneticsException(s"cannot transcribe $x")
    }
  }

  val hox = Locus("hox", 0, 1) // C or A
  val hix = Locus("hix", 1, 1) // G or G
  
  "Genome.transcribeGene" should "should give Alleles A and C" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test",karyotype,true,transcriber)
    // NOTE that we must explicitly state the type because the sequences are only of length 1
    val bss: Seq[Strand[Base]] = Seq(Strand(Seq(Cytosine)),Strand(Seq(Adenine)))
    val geneHox: Gene[Boolean] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(false) shouldBe Allele("A")
    geneHox.apply(true) shouldBe Allele("C")
  }

  it should "should get multiple loci right" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox,hix)))
    val g = Genome("test",karyotype,true,transcriber)
    val bss = Seq(Strand(Seq(Cytosine,Guanine)),Strand(Seq(Adenine,Guanine)))
    val geneHox: Gene[Boolean] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(false) shouldBe Allele("A")
    geneHox.apply(true) shouldBe Allele("C")
    val geneHix: Gene[Boolean] = g.transcribeGene(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix.apply(false) shouldBe Allele("G")
    geneHix.apply(true) shouldBe Allele("G")
  }

  "Genome.transcribe" should "work" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test",karyotype,true,transcriber)
    val loci = g.loci
    val bsss = Seq(Seq(Strand(Seq(Cytosine,Guanine)),Strand(Seq(Adenine,Guanine))))
    val gt: Genotype[Boolean] = g.transcribe(bsss)
    gt.genome shouldBe g
    gt.genes.size shouldBe loci
    val gene = gt.genes.head
    gene.name shouldBe "hox"
    gene.apply(false) shouldBe Allele("A")
    gene.apply(true) shouldBe Allele("C")
  }

  it should "work with multiple chromosomes" in {
    val chromosome1 = Chromosome("test1", isSex = false, Seq(hox,hix))
    val chromosome2 = Chromosome("test2", isSex = false, Seq(Locus("hoxB", 1, 1)))
    val chromosome3 = Chromosome("test3", isSex = false, Seq(Locus("hoxA", 0, 1),Locus("hoxB", 1, 1),Locus("hoxC", 2, 1)))
    val karyotype: Seq[Chromosome] = Seq(chromosome1,chromosome2,chromosome3)
    val g = Genome("test",karyotype,true,transcriber)
    val loci = g.loci
    val bsss = Seq(Seq(Strand("CG"),Strand("AG")),Seq(Strand("CT"),Strand("AG")),Seq(Strand("CGT"),Strand("AGA")))
    val gt: Genotype[Boolean] = g.transcribe(bsss)
    gt.genome shouldBe g
    gt.genes.size shouldBe loci
    val gene = gt.genes.head
    gene.name shouldBe "hox"
    gene.apply(false) shouldBe Allele("A")
    gene.apply(true) shouldBe Allele("C")
  }

  "Transcriber transcriber" should "work" in {
    val bs = Strand(Seq(Cytosine,Guanine))
    val allele = transcriber.transcribe(bs)(hox)
  }

}
