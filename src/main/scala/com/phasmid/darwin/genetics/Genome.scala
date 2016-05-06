package com.phasmid.darwin.genetics

/**
  * Genome represents a template for a Genotype.
  * In particular, it is able to transcribe a MultiStrand into a Genotype.
  *
  * @author scalaprof
 */
case class Genome[P](name: String, karyotype: Seq[Chromosome], ploidy: Int, transcriber: Transcriber) extends Identifier {
  def transcribe[B](bms: Seq[MultiStrand[B]]): Genotype[P] =
    Genotype[P](this, for ((bm, k) <- bms zip karyotype; l <- k.loci) yield transcribe(bm, l))
  def transcribe[B](bm: MultiStrand[B], locus: Locus): Gene[P] = {
    val as = for (bs <- bm) yield transcriber.transcribe(bs)(locus)
    (fAsP(as) _).asInstanceOf[Gene[P]]
  }
  def fAsP(as: Seq[Allele])(p: P): Allele = p match {
    case q: Boolean => if (q) as.head else as(1)
    case q: Int => as(q)
    case _ => throw new GeneticsException("type P must be Boolean or Int")
  }
}

case class Chromosome(name: String, isSex: Boolean, loci: Seq[Locus]) extends Identifier

case class Locus(index: Int, length: Int)

trait Transcriber {
  def transcribe[B](bs: Strand[B])(locus: Locus): Allele
}
