package com.phasmid.darwin.genetics

/**
  * Genome represents a template for a Genotype.
  * In particular, it is able to transcribe a matrix of Strands into a Genotype.
  *
  * @tparam P the type of ploidy: for the usual diploid arrangement, P is Boolean;
  *           for haploid: P is Unit;
  *           for multiploid: P is Int.
  * @author scalaprof
 */
case class Genome[P](name: String, karyotype: Seq[Chromosome], ploidy: P, transcriber: Transcriber) extends Identifier {
  def chromosomes = karyotype.size
  def loci = karyotype map (_.loci) sum

  /**
    * method to transcribe a matrix of Strands into a Genotype
 *
    * @param bsss the matrix of Strands--the inner dimension should agree with ploidy (2 for diploid) of this genome--
    *             while the outer dimension should match the ploidy (2 for diploid)
    * @tparam B the underlying type of the Strands
    * @return a new instance of Genotype[P]
    */
  def transcribe[B](bsss: Seq[Seq[Strand[B]]]): Genotype[P] = {
    require(bsss.size == chromosomes,s"size of outer Strands dimension (${bsss.size}) should equal the karyotype ($chromosomes)")
    val genes = for ((bss, k) <- bsss zip karyotype; l <- k.ls) yield transcribeGene(bss, l)
    Genotype[P](this, genes)
  }

  /**
    * This is essentially a private method made public only for unit testing
    *
    * @param bss a sequence of Strands one for each ploidy (e.g. two for diploid)
    * @param locus the locus of the gene on the Strand
    * @tparam B the underlying type of the Strands
    * @return a new instance of Gene[P]
    */
  def transcribeGene[B](bss: Seq[Strand[B]], locus: Locus): Gene[P] =
    PGene(locus, for (bs <- bss) yield transcriber.transcribe(bs)(locus))

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
  * @param name the identifier for this Chromosome
  * @param isSex if true then this is an allosome (i.e. is sex-linked) otherwise a somasome
  * @param ls the gene loci on this Chromosome
  */
case class Chromosome(name: String, isSex: Boolean, ls: Seq[Locus]) extends Identifier {
  def loci = ls.size
}

/**
  * A location is the position on the chromosome that a locus can be found.
  *
  * @param name the name that we give to the "gene" at this locus
  * @param offset the offset at which the gene starts in the strand for this Chromosome
  * @param length the length of the gene in terms of the strand
  */
case class Locus(name: String, offset: Int, length: Int) extends Identifier

/**
  * @author scalaprof
  *
 */
trait Transcriber {
  def transcribe[B](bs: Strand[B])(locus: Locus): Allele
}
