package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class GenotypeSpec extends FlatSpec with Matchers {

  // XXX this is a very simple 1:1 mapping from bases to alleles
  val transcriber = PlainTranscriber[Base, String] { bs => Some(Allele(bs.head.toString)) }
  val hox = Location("hox", 0, 1)
  // C or A
  val hix = Location("hix", 1, 1)
  // G or G
  val hoxB = Location("hoxB", 1, 1)
  val hoxA = Location("hoxA", 0, 1)
  val hoxC = Location("hoxC", 2, 1)
  val locusMap: (Location) => Locus[String] = Map(
    hox -> UnknownLocus[String](hox),
    hix -> UnknownLocus[String](hix),
    hoxA -> UnknownLocus[String](hoxA),
    hoxB -> UnknownLocus[String](hoxB),
    hoxC -> UnknownLocus[String](hoxC))

  "apply" should "work" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    val loci = g.loci
    val bsss = Seq(Seq(Sequence(Seq(Cytosine, Guanine)), Sequence(Seq(Adenine, Guanine))))
    val gt: Genotype[Boolean, String] = g(bsss)
    gt.genes.size shouldBe loci
    val gene = gt.genes.head
    gene.name shouldBe "hox"
    gene(false) shouldBe Allele("A")
    gene(true) shouldBe Allele("C")
  }

  it should "work with multiple chromosomes" in {
    val chromosome1 = Chromosome("test1", isSex = false, Seq(hox, hix))
    val chromosome2 = Chromosome("test2", isSex = false, Seq(hoxB))
    val chromosome3 = Chromosome("test3", isSex = false, Seq(hoxA, hoxB, hoxC))
    val karyotype: Seq[Chromosome] = Seq(chromosome1, chromosome2, chromosome3)
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    val loci = g.loci
    val bsss: Nucleus[Base] = Seq(Seq(Sequence("CG"), Sequence("AG")), Seq(Sequence("CT"), Sequence("AG")), Seq(Sequence("CGT"), Sequence("AGA")))
    val gt: Genotype[Boolean, String] = g(bsss)
    gt.genes.size shouldBe loci
    val gene = gt.genes.head
    gene.name shouldBe "hox"
    gene(false) shouldBe Allele("A")
    gene(true) shouldBe Allele("C")
  }
}
