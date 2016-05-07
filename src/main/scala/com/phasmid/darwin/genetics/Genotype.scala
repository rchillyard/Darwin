package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
  * This class represents a genotype: the genes of a particular organism.
  *
  *
  * @tparam P the "ploidy" type:
  * P is normally a Boolean to distinguish alleles in a diploid arrangement.
  * But if you want to have a triploid arrangement (or any other ploidy) then you might
  * want to use something different for P, such Int or Unit (for haploid).
  *
 * @author scalaprof
 */
case class Genotype[P](genome: Genome[P], genes: Seq[Gene[P]])

/**
  * This trait defines the function to take a selector (a P) and return the particular Allele that corresponds to
  * that selection for the given gene.
  * For a diploid system, P will be Boolean.
  * For a haploid system, P will be Unit.
  * Otherwise, P will be Int.
  *
  * @tparam P
  */
trait Gene[P] extends (P=>Allele) with Identifier

/**
  * A diploid gene which extends Gene[Boolean]
  * @param locus the locus on the chromosome where this gene can be found
  * @param alleles the two alleles of this (diploid) gene
  */
case class GeneDiploid(locus: Locus, alleles: (Allele,Allele)) extends Gene[Boolean] {
  def apply(p: Boolean): Allele = if (p) alleles._1 else alleles._2
  val name = locus.name
}

/**
  * An allele with a particular name/identifier
  *
 */
case class Allele(name: String) extends Identifier
