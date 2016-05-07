package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}
import com.phasmid.darwin.genetics.dna._

/**
  * Created by scalaprof on 5/6/16.
  */
class GenomeSpec extends FlatSpec with Matchers {

  // XXX this is a very simple 1:1 mapping from bases to alleles
  val transcriber = PlainTranscriber[Base,String]{bs => Allele(bs.head.toString)}
  val hox = Locus("hox", 0, 1) // C or A
  val hix = Locus("hix", 1, 1) // G or G
  
  "transcribeGene" should "give Alleles A and C" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test",karyotype,true,transcriber)
    // NOTE that we must explicitly state the type because the sequences are only of length 1
    val bss: Seq[Sequence[Base]] = Seq(Sequence(Seq(Cytosine)),Sequence(Seq(Adenine)))
    val geneHox: Gene[Boolean,String] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(false) shouldBe Allele("A")
    geneHox.apply(true) shouldBe Allele("C")
  }

  it should "get multiple loci right" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox,hix)))
    val g = Genome("test",karyotype,true,transcriber)
    val bss = Seq(Sequence(Seq(Cytosine,Guanine)),Sequence(Seq(Adenine,Guanine)))
    val geneHox: Gene[Boolean,String] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(false) shouldBe Allele("A")
    geneHox.apply(true) shouldBe Allele("C")
    val geneHix: Gene[Boolean,String] = g.transcribeGene(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix.apply(false) shouldBe Allele("G")
    geneHix.apply(true) shouldBe Allele("G")
  }

  it should "work with haploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox,hix)))
    val g = Genome("test",karyotype,(),transcriber)
    val bss = Seq(Sequence(Seq(Cytosine,Guanine)))
    val geneHox: Gene[Unit,String] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(()) shouldBe Allele("C")
    val geneHix: Gene[Unit,String] = g.transcribeGene(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix.apply(()) shouldBe Allele("G")
  }

  it should "work with triploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox,hix)))
    val g = Genome("test",karyotype,3,transcriber)
    val bss = Seq(Sequence(Seq(Cytosine,Guanine)),Sequence(Seq(Adenine,Thymine)),Sequence(Seq(Guanine,Adenine)))
    val geneHox: Gene[Int,String] = g.transcribeGene(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox.apply(0) shouldBe Allele("C")
    geneHox.apply(1) shouldBe Allele("A")
    geneHox.apply(2) shouldBe Allele("G")
    val geneHix: Gene[Int,String] = g.transcribeGene(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix.apply(1) shouldBe Allele("T")
  }

  "apply" should "work" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test",karyotype,true,transcriber)
    val loci = g.loci
    val bsss = Seq(Seq(Sequence(Seq(Cytosine,Guanine)),Sequence(Seq(Adenine,Guanine))))
    val gt: Genotype[Boolean,String] = g(bsss)
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
    val bsss: Nucleus[Base] = Seq(Seq(Sequence("CG"),Sequence("AG")),Seq(Sequence("CT"),Sequence("AG")),Seq(Sequence("CGT"),Sequence("AGA")))
    val gt: Genotype[Boolean,String] = g(bsss)
    gt.genes.size shouldBe loci
    val gene = gt.genes.head
    gene.name shouldBe "hox"
    gene.apply(false) shouldBe Allele("A")
    gene.apply(true) shouldBe Allele("C")
  }
}
