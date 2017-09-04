/*
 * DARWIN Genetic Algorithms Framework Project.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 *
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
 *
 *      This file is part of Darwin.
 *
 *      Darwin is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.phasmid.darwin.genetics

import com.phasmid.darwin.Identifier
import com.phasmid.darwin.evolution.{RNG, Random}
import com.phasmid.laScala.fp.FP._
import com.phasmid.laScala.fp.Spy


/**
  * Genome represents a template for a Genotype. It is a particular subtype for the Genomic trait.
  * In particular, it is able to transcribe a matrix of Sequences into a Genotype.
  *
  * @param name        the identifier for this Genome, e.g. Homo Sapiens, Mus Musculus, etc.
  * @param karyotype   the number of Chromosome pairs. For Home Sapiens: 23
  * @param ploidy      the actual ploidy: 1 for haploid, 2 for diploid, N for polyploid
  * @param transcriber a transcriber of Sequence[B] (with Location) into a Gene[G].
  * @param locusMap    a mapper between Locations (on a Chromosome) to Loci (that define the Alleles and any dominance)
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
  *           for haploid: P is Unit;
  *           for polyploid: P is Int.
  * @tparam G the underlying gene value type
  */
case class Genome[B, P, G](name: String, karyotype: Seq[Chromosome], ploidy: P,
                           // CONSIDER combining transcriber and locusMap
                           transcriber: Transcriber[B, G], locusMap: (Location) => Locus[G]) extends Genomic[B, P, G] with Identifier {
  /**
    * @return the the number of chromosomes (pairs, actually) defined for this Genome. Synonymous with "karyotype"
    */
  def chromosomes: Int = karyotype.size

  implicit private val spyLogger = Spy.getLogger(getClass)

  /**
    * @return the total number of loci (locations) on this Genome
    */
  lazy val loci: Int = (karyotype map (_.loci)).sum

  /**
    * method to transcribe a matrix of Sequences into a Genotype
    *
    * @param bsss the nucleus, i.e. the matrix of Sequences--the inner dimension should agree with ploidy (2 for diploid) of this genome--
    *             while the outer dimension should match the number of chromosomes
    * @return a new instance of Genotype[P]
    */
  def apply(bsss: Nucleus[B]): Genotype[P, G] = {
    require(bsss.size == chromosomes, s"size of outer Sequences dimension (${bsss.size}) should equal the karyotype ($chromosomes)")
    val genes = for ((bss, k) <- bsss zip karyotype; l <- Spy.spy(s"l for $k: ", k.ls)) yield Spy.spy(s"yield for $bss, $l: ", transcribe(bss, l))
    Genotype[P, G](genes)
  }

  /**
    * TODO make loci variable, with a separate value for each chromosome
    *
    * Method to take a RNG[B] and yield a Nucleus[B], given the ploidy and the number of loci and chromosomes
    *
    * @param random an RNG[B]
    * @return a tuple of Nucleus and Random[B]
    */
  def recombine(random: RNG[B]): (Nucleus[B], Random[B]) = {
    val (br, bs) = random.take(ploidyVal * loci * chromosomes)
    (recombineNucleus(bs), br)
  }

  /**
    * This is essentially a private method made public only for unit testing
    *
    * TODO make this private
    *
    * @param bss      a sequence of Sequences one for each ploidy (e.g. two for diploid)
    * @param location the location of the gene on the Sequence
    * @return a new instance of Gene[P]
    */
  def transcribe(bss: SequenceSet[B], location: Location): Gene[P, G] = {
    val as = for (bs <- bss) yield for (g <- transcriber(bs, location)) yield g
    Spy.log("transcribe: as = $as")
    PGene(locusMap(location), sequence(as).get)
  }

  def recombineSequenceSet(bs: Seq[B]): SequenceSet[B] = (for (x <- bs.grouped(bs.size / ploidyVal)) yield Sequence(x)).toSeq

  def recombineNucleus(bs: Seq[B]): Nucleus[B] = (for (x <- bs.grouped(bs.size / loci)) yield recombineSequenceSet(x)).toSeq

  /**
    * This class defines a generic type of Gene that corresponds to Gene[P].
    *
    * @param l  the Locus of the Gene
    * @param as a sequence of Alleles. For a diploid system (P is Boolean), then the cardinality of as should be 2
    */
  case class PGene(l: Locus[G], as: Seq[Allele[G]]) extends AbstractGene[P, G](l, as) {
    override def toString: String = "PGene:" + super.toString
  }

  private lazy val ploidyVal: Int = ploidy match {
    case _: Boolean => 2
    case _: Unit => 1
    case i: Int => i
    case _ => throw GeneticsException(s"invalid Ploidy type: ${ploidy.getClass}")
  }

}

/**
  * This class represents the karyotic aspect of a Chromosome, that's to say the template for a sequence of actual genes.
  * The use of the term Chromosome here is not strictly according to genetics practice, therefore.
  * What we are really representing here, therefore, is the template for a multiple of chromosomes because at each location
  * there will be, except for a haploid system, more than one allele at each location. For the typical diploid arrangement,
  * there will be two alleles at each location.
  *
  * @param name  the identifier for this Chromosome
  * @param isSex if true then this is an allosome (i.e. is sex-linked) otherwise an autosome.
  * @param ls    the gene loci on this Chromosome
  */
case class Chromosome(name: String, isSex: Boolean, ls: Seq[Location]) extends Identifier {
  /**
    * @return the number of locations (loci) in this Chromosome
    */
  def loci: Int = ls.size
}

/**
  * A Location is the position on the chromosome at which a gene can be found.
  * I'd like to use the term Locus but that is needed for the similar type in Genotype.
  *
  * @param name   the name that we give to the "gene" at this locus/location
  * @param offset the offset at which the gene starts in the sequence for this Chromosome
  * @param length the length of the gene in terms of the sequence
  */
case class Location(name: String, offset: Int, length: Int) extends Identifier {
  override def toString: String = s"L:$name:$offset:$length"
}
