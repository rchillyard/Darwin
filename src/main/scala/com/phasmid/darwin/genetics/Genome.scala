package com.phasmid.darwin.genetics

/**
  * Genomic is a trait which provides the functionality to transcribe a Nucleus (that's to say a matrix of Sequences)
  * into a Genotype.
  *
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
  *           for haploid: P is Unit;
  *           for polyploid: P is Int.
  * @tparam G the underlying gene value type
  */
trait Genomic[B,P,G] extends (Nucleus[B]=>Genotype[P,G]) with Identifier

/**
  * Genome represents a template for a Genotype. It is a particular subtype for the Genomic trait.
  * In particular, it is able to transcribe a matrix of Sequences into a Genotype.
  *
  * @param name the identifier for this Genome, e.g. Homo Sapiens, Mus Musculus, etc.
  * @param karyotype the number of Chromosome pairs. For Home Sapiens: 23
  * @param ploidy the actual ploidy: 1 for haploid, 2 for diploid, N for polyploid
  * @param transcriber a transcriber of Sequence[B] (with Location) into a Gene[G].
  * @param locusMap a mapper between Locations (on a Chromosome) to Loci (that define the Alleles and any dominance)
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
  *           for haploid: P is Unit;
  *           for polyploid: P is Int.
  * @tparam G the underlying gene value type
  */
case class Genome[B,P,G](name: String, karyotype: Seq[Chromosome], ploidy: P,
                        // CONSIDER combining transcriber and locusMap
                         transcriber: Transcriber[B, G], locusMap: (Location) => Locus[G]) extends Genomic[B,P,G] {
  def chromosomes = karyotype.size
  def loci = (karyotype map (_.loci)).sum

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
    * @param location the location of the gene on the Sequence
    * @return a new instance of Gene[P]
    */
  def transcribe(bss: SequenceSet[B], location: Location): Gene[P,G] =
    PGene(locusMap(location), for (bs <- bss; g <- transcriber(bs,location)) yield g)

  /**
    * This class defines a generic type of Gene that corresponds to Gene[P].
    *
    * @param l the Locus of the Gene
    * @param as a sequence of Alleles. For a diploid system (P is Boolean), then the cardinality of as should be 2
    */
  case class PGene(l: Locus[G], as: Seq[Allele[G]]) extends AbstractGene[P,G](l,as)
}

/**
  * This class represents the karyotic aspect of a Chromosome, that's to say the template for a sequence of actual genes.
  * The use of the term Chromosome here is not strictly according to genetics practice, therefore.
  * What we are really representing here, therefore, is the template for a multiple of chromosomes because at each location
  * there will be, except for a haploid system, more than one allele at each location. For the typical diploid arrangement,
  * there will be two alleles at each location.
  *
  * @param name the identifier for this Chromosome
  * @param isSex if true then this is an allosome (i.e. is sex-linked) otherwise an autosome.
  * @param ls the gene loci on this Chromosome
  */
case class Chromosome(name: String, isSex: Boolean, ls: Seq[Location]) extends Identifier {
  def loci = ls.size
}

/**
  * A Location is the position on the chromosome at which a gene can be found.
  * I'd like to use the term Locus but that is needed for the similar type in Genotype.
  *
  * @param name the name that we give to the "gene" at this locus/location
  * @param offset the offset at which the gene starts in the sequence for this Chromosome
  * @param length the length of the gene in terms of the sequence
  */
case class Location(name: String, offset: Int, length: Int) extends Identifier
