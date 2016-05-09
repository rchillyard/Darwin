package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
  * This class represents a genotype: the genes of a particular organism.
  *
  * @tparam P the "ploidy" type:
  * P is normally a Boolean to distinguish alleles in a diploid arrangement.
  * But if you want to have a triploid arrangement (or any other ploidy) then you might
  * want to use something different for P, such Int or Unit (for haploid).
  * @tparam G the underlying Gene type
  * @author scalaprof
 */
case class Genotype[P,G](genes: Seq[Gene[P,G]])

/**
  * This trait defines the function to take a selector (a P) and return the particular Allele that corresponds to
  * that selection for the given gene.
  *
  * TODO we are somewhat confusing the concepts of a Location and a Gene. The Location isn't really just a position on the
  * chromosome--it should also tell us what possible Alleles can appear there and maybe something about dominance, if any.
  *
  * @tparam P
  * For a diploid system, P will be Boolean.
  * For a haploid system, P will be Unit.
  * Otherwise, P will be Int.
  * @tparam G the underlying Gene type
  */
trait Gene[P,G] extends (P=>Allele[G]) with Identifier {
  def locus: Locus[G]
  /**
    * returns distinct allele as a Tuple
    *
    * @return a tuple of Allele[G]
    */
  def distinct: Product
}

/**
  * This trait models the notion of a locus in the sense of the alleles that are possible at that locus.
  * We use Location (in Genome) to model the position on a Chromosome at which the gene (and its alleles) can be found.
  *
  * @tparam G
  */
trait Locus[G] extends (()=>Seq[Allele[G]]) {
  def location: Location
  def dominant: Option[Allele[G]]
  override def toString = s"Locus at $location with dominant: $dominant and possible alleles: ${apply()}"
}

case class PlainLocus[G](location: Location, alleles: Seq[Allele[G]], dominant: Option[Allele[G]]) extends Locus[G] {
  def apply(): Seq[Allele[G]] = alleles
}

/**
  * A Mendelian gene which is to say one that has/expresses recessive and dominant alleles/traits.
  * @param l the locus
  * @param as the actual alleles
  * @tparam P
  * For a diploid system, P will be Boolean.
  * For a haploid system, P will be Unit.
  * Otherwise, P will be Int.
  * @tparam G the underlying Gene type
  */
case class MendelianGene[P,G](l: Locus[G], as: Seq[Allele[G]]) extends AbstractDominanceGene[P,G](l,as) {
  override def toString = s"""MendelianGene: at $l with alleles: ${as.mkString(", ")}"""
}

abstract class AbstractGene[P,G](l: Locus[G], as: Seq[Allele[G]]) extends Gene[P,G] {

  def locus = l
  def apply(p: P): Allele[G] = p match {
    case u: Unit => as.head
    case q: Boolean => if (q) as.head else as(1)
    case q: Int => as(q)
    case _ => throw new GeneticsException("type P must be Unit, Boolean or Int")
  }

  val name = locus.location.name

  /**
    * Returns distinct alleles as a Tuple
    *
    * @return a sequence of Allele[G]
    */
  def distinct: Product = {
    val x = as.distinct
    x.length match {
      case 1 => x.head
      case 2 => (x.head, x.tail.head)
      case 3 => (x.head, x.tail.head, x.tail.tail.head)
      case _ => throw new GeneticsException(s"unsupported number of distinct alleles: $x")
    }
  }
}

abstract class AbstractDominanceGene[P,G](locus: Locus[G], as: Seq[Allele[G]]) extends AbstractGene[P,G](locus,as)

/**
  * An allele with a particular name/identifier
  *
  * @param t the value of this Allele
  * @tparam G the type of the value
  */
case class Allele[G](t: G) extends Identifier {
  override def name: String = t.toString
}
