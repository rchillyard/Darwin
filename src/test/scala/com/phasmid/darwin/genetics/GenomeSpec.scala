package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class GenomeSpec extends FlatSpec with Matchers {

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

  "transcribe" should "give Alleles A and C" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox)))
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    // NOTE that we must explicitly state the type because the sequences are only of length 1
    val bss: Seq[Sequence[Base]] = Seq(Sequence(Seq(Cytosine)), Sequence(Seq(Adenine)))
    val geneHox: Gene[Boolean, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(false) shouldBe Allele("A")
    geneHox(true) shouldBe Allele("C")
  }

  it should "get multiple loci right" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, true, transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)), Sequence(Seq(Adenine, Guanine)))
    val geneHox: Gene[Boolean, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(false) shouldBe Allele("A")
    geneHox(true) shouldBe Allele("C")
    val geneHix: Gene[Boolean, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(false) shouldBe Allele("G")
    geneHix(true) shouldBe Allele("G")
  }

  it should "work with haploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, (), transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)))
    val geneHox: Gene[Unit, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(()) shouldBe Allele("C")
    val geneHix: Gene[Unit, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(()) shouldBe Allele("G")
  }

  it should "work with triploid genetics" in {
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(hox, hix)))
    val g = Genome("test", karyotype, 3, transcriber, locusMap)
    val bss = Seq(Sequence(Seq(Cytosine, Guanine)), Sequence(Seq(Adenine, Thymine)), Sequence(Seq(Guanine, Adenine)))
    val geneHox: Gene[Int, String] = g.transcribe(bss, hox)
    geneHox.name shouldBe "hox"
    geneHox(0) shouldBe Allele("C")
    geneHox(1) shouldBe Allele("A")
    geneHox(2) shouldBe Allele("G")
    val geneHix: Gene[Int, String] = g.transcribe(bss, hix)
    geneHix.name shouldBe "hix"
    geneHix(1) shouldBe Allele("T")
  }
}
