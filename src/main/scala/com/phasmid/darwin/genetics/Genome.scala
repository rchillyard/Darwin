package com.phasmid.darwin.genetics

import com.phasmid.darwin.util.MonadOps

/**
  * Genome represents a template for a Genotype.
  * In particular, it is able to transcribe a matrix of Sequences into a Genotype.
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
    * method to transcribe a matrix of Sequences into a Genotype
 *
    * @param bsss the nucleus, i.e. the matrix of Sequences--the inner dimension should agree with ploidy (2 for diploid) of this genome--
    *             while the outer dimension should match the ploidy (2 for diploid)
    * @tparam B the underlying type of the Sequences
    * @return a new instance of Genotype[P]
    */
  def transcribe[B](bsss: Nucleus[B]): Genotype[P] = {
    require(bsss.size == chromosomes,s"size of outer Sequences dimension (${bsss.size}) should equal the karyotype ($chromosomes)")
    val genes = for ((bss, k) <- bsss zip karyotype; l <- k.ls) yield transcribeGene(bss, l)
    Genotype[P](this, genes)
  }

  /**
    * This is essentially a private method made public only for unit testing
    *
    * @param bss a sequence of Sequences one for each ploidy (e.g. two for diploid)
    * @param locus the locus of the gene on the Sequence
    * @tparam B the underlying type of the Sequences
    * @return a new instance of Gene[P]
    */
  def transcribeGene[B](bss: SequenceSet[B], locus: Locus): Gene[P] =
    PGene(locus, for (bs <- bss; g <- transcriber.transcribe(bs)(locus)) yield g)

  /**
    * This class defines a generic type of Gene that corresponds to Gene[P].
    * @param locus the locus on a Chromosome at which the Gene is to be found
    * @param as a sequence of Alleles. For a diploid system (P is Boolean), then the cardinality of as should be 2
    */
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
  * @param isSex if true then this is an allosome (i.e. is sex-linked) otherwise an autosome.
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
  * A Transcriber is the heart of the process for taking Sequence information and generating its corresponding Genotype.
  * There are two more or less independent phases, and one phases which combines the two others:
  * <ol>
  *   <li>locateBases: locate the region of the Sequence at which the locus is to be found;</li>
  *   <li>transcribeBases: transcribe that region into a particular Allele.</li>
  *   <li>transcribe: locate and transcribe.</li>
  * </ol>
  * All three methods may be overridden in extenders of Transcriber, but transcribeBases MUST be defined.
  */
trait Transcriber {
  /**
    * This method locates a Seq[B] from a Sequence[B] according to the details of the given locus
    * @param bs the Sequence[B] (corresponding to a Chromosome) on which the locus is expected to be found
    * @param locus the locus
    * @tparam B the underlying type of the Sequence
    * @return Some(Seq[B]) if the location was found, otherwise None
    */
  def locateBases[B](bs: Sequence[B], locus: Locus): Option[Seq[B]] = bs.locate(locus)

  /**
    * This method is required to be defined by sub-types (extenders) of Transcriber.
    * Given a Seq[B] corresponding to the location of a gene on a Chromosome, return the Allele that
    * this sequence encodes.
    * @param bs the sequence of bases
    * @tparam B the underlying base type, for example Base
    * @return an Allele
    */
  def transcribeBases[B](bs: Seq[B]): Allele

  /**
    * This method is called directly by the Genome method transcribeGene and indirectly by the Genome's
    * transcribe method.
    * It is normally not necessary to override this method.
    *
    * @param bs the Sequence of bases to transcribe
    * @param locus the locus on the Chromosome at which we expect to find the gene we are interested in
    * @tparam B the underlying type of the Sequence
    * @return Some(Allele) assuming that all went well, otherwise None
    */
  def transcribe[B](bs: Sequence[B])(locus: Locus): Option[Allele] = MonadOps.optionLift(transcribeBases _)(locateBases(bs,locus))
}
