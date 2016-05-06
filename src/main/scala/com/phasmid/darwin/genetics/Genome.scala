package com.phasmid.darwin.genetics

/**
  * Genome represents a template for a Genotype.
  * In particular, it is able to transcribe a MultiStrand into a Genotype.
  *
  * @tparam P the type of ploidy: for the usual diploid arrangement, P is Boolean;
  *           for haploid: P is Unit;
  *           for multiploid: P is Int.
  * @author scalaprof
 */
case class Genome[P](name: String, karyotype: Seq[Chromosome], ploidy: P, transcriber: Transcriber) extends Identifier {
  def transcribe[B](bsss: Seq[Seq[Strand[B]]]): Genotype[P] = {
    val genes = for ((bss, k) <- bsss zip karyotype; l <- k.loci) yield transcribeGene(bss, l)
    Genotype[P](this, genes)
  }

  /**
    * This is essentially a private method made public only for unit testing
    *
    * @param bss
    * @param locus
    * @tparam B
    * @return
    */
  def transcribeGene[B](bss: Seq[Strand[B]], locus: Locus): Gene[P] =
    PGene(locus,for (bs <- bss) yield transcriber.transcribe(bs)(locus))

  case class PGene(locus: Locus, as: Seq[Allele]) extends Gene[P] {
    def apply(p: P): Allele = p match {
      case u: Unit => as.head
      case q: Boolean => if (q) as.head else as(1)
      case q: Int => as(q)
      case _ => throw new GeneticsException("type P must be Unit, Boolean or Int")
    }
    val name = locus.name
  }
}

/**
  * This class represents the karyotic aspect of a Chromosome, that's to say the template for a sequence of actual genes.
  * The use of the term Chromosome here is not strictly according to genetics practice, therefore.
  * What we are really representing here, therefore, is the template for a multiple of chromosomes because at each locus
  * there will be, except for a haploid system, more than one allele at each locus. For the typical diploid arrangement,
  * there will be two alleles at each locus.
  *
  * @param name
  * @param isSex
  * @param loci
  */
case class Chromosome(name: String, isSex: Boolean, loci: Seq[Locus]) extends Identifier

/**
  * A location is the position on the chromosome that a locus can be found.
  *
  * @author scalaprof
  * @param name the name that we give to the "gene" at this locus
  * @param offset the offset at which the gene starts in the strand for this Chromosome
  * @param length the length of the gene in terms of the strand
  */
case class Locus(name: String, offset: Int, length: Int) extends Identifier

/**
  * @tparam B
 * @author scalaprof
 *
 */
trait Transcriber {
  def transcribe[B](bs: Strand[B])(locus: Locus): Allele
}
