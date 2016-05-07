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
  * @tparam T the underlying Gene type
  *
 * @author scalaprof
 */
case class Genotype[P,T](genes: Seq[Gene[P,T]])

/**
  * This trait defines the function to take a selector (a P) and return the particular Allele that corresponds to
  * that selection for the given gene.
  *
  * @tparam P
  * For a diploid system, P will be Boolean.
  * For a haploid system, P will be Unit.
  * Otherwise, P will be Int.
  * @tparam T the underlying Gene type
  */
trait Gene[P,T] extends (P=>Allele[T]) with Identifier

/**
  * A diploid gene which extends Gene[Boolean]
  * @param locus the locus on the chromosome where this gene can be found
  * @param alleles the two alleles of this (diploid) gene
  */
case class GeneDiploidString(locus: Locus, alleles: (Allele[String],Allele[String])) extends Gene[Boolean,String] {
  def apply(p: Boolean): Allele[String] = if (p) alleles._1 else alleles._2
  val name = locus.name
}

/**
  * An allele with a particular name/identifier
  *
 */
case class Allele[T](t: T) extends Identifier {
  override def name: String = t.toString
}
