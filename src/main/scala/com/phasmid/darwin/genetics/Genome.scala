package com.phasmid.darwin.genetics

/**
  * Genomic is a trait which provides the functionality to transcribe a Nucleus (that's to say a matrix of Sequences)
  * into a Genotype.
  *
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) Boolean (ploidy=2)
  * @tparam G the underlying gene value type
  */
trait Genomic[B,P,G] extends (Nucleus[B]=>Genotype[P,G]) with Identifier

/**
  * Genome represents a template for a Genotype. It is a particular subtype for the Genomic trait.
  * In particular, it is able to transcribe a matrix of Sequences into a Genotype.
  *
  * @tparam B the underlying type of the Sequences which make up the Nucleus
  * @tparam P the type of ploidy: for the usual diploid arrangement, P is Boolean;
  *           for haploid: P is Unit;
  *           for multiploid: P is Int.
  * @tparam G the underlying gene value type
  * @author scalaprof
 */
case class Genome[B,P,G](name: String, karyotype: Seq[Chromosome], ploidy: P, transcriber: Transcriber[B,G]) extends Genomic[B,P,G] {
  def chromosomes = karyotype.size
  def loci = karyotype map (_.loci) sum

  /**
    * method to transcribe a matrix of Sequences into a Genotype
    *
    * @param bsss the nucleus, i.e. the matrix of Sequences--the inner dimension should agree with ploidy (2 for diploid) of this genome--
    *             while the outer dimension should match the ploidy (2 for diploid)
    * @return a new instance of Genotype[P]
    */
  def apply(bsss: Nucleus[B]): Genotype[P,G] = {
    require(bsss.size == chromosomes,s"size of outer Sequences dimension (${bsss.size}) should equal the karyotype ($chromosomes)")
    val genes = for ((bss, k) <- bsss zip karyotype; l <- k.ls) yield transcribe(bss, l)
    Genotype[P,G](genes)
  }

  /**
    * This is essentially a private method made public only for unit testing
    *
    * @param bss a sequence of Sequences one for each ploidy (e.g. two for diploid)
    * @param locus the locus of the gene on the Sequence
    * @return a new instance of Gene[P]
    */
  def transcribe(bss: SequenceSet[B], locus: Locus): Gene[P,G] =
    PGene(locus, for (bs <- bss; g <- transcriber(bs,locus)) yield g)

  /**
    * This class defines a generic type of Gene that corresponds to Gene[P].
    *
    * @param locus the locus on a Chromosome at which the Gene is to be found
    * @param as a sequence of Alleles. For a diploid system (P is Boolean), then the cardinality of as should be 2
    */
  case class PGene(locus: Locus, as: Seq[Allele[G]]) extends AbstractGene[P,G](locus,as)

}

/**
  * This class represents the karyotic aspect of a Chromosome, that's to say the template for a sequence of actual genes.
  * The use of the term Chromosome here is not strictly according to genetics practice, therefore.
  * What we are really representing here, therefore, is the template for a multiple of chromosomes because at each locus
  * there will be, except for a haploid system, more than one allele at each locus. For the typical diploid arrangement,
  * there will be two alleles at each locus.
  *
  * @param name the identifier for this Chromosome
  * @param isSex if true then this is an allosome (i.e. is sex-linked) otherwise an autosome.
  * @param ls the gene loci on this Chromosome
  */
case class Chromosome(name: String, isSex: Boolean, ls: Seq[Locus]) extends Identifier {
  def loci = ls.size
}

/**
  * A Locus is the position on the chromosome at which a gene can be found.
  *
  * @param name the name that we give to the "gene" at this locus
  * @param offset the offset at which the gene starts in the sequence for this Chromosome
  * @param length the length of the gene in terms of the sequence
  */
case class Locus(name: String, offset: Int, length: Int) extends Identifier
