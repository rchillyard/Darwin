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
  * @tparam G the underlying Gene type
  *
 * @author scalaprof
 */
case class Genotype[P,G](genes: Seq[Gene[P,G]])

/**
  * This trait defines the function to take a selector (a P) and return the particular Allele that corresponds to
  * that selection for the given gene.
  *
  * TODO we are somewhat confusing the concepts of a Locus and a Gene. The Locus isn't really just a position on the
  * chromosome--it should also tell us what possible Alleles can appear there and maybe something about dominance, if any.
  *
  * @tparam P
  * For a diploid system, P will be Boolean.
  * For a haploid system, P will be Unit.
  * Otherwise, P will be Int.
  * @tparam G the underlying Gene type
  */
trait Gene[P,G] extends (P=>Allele[G]) with Identifier {
  /**
    * returns distinct alleles as a Tuple
     * @return a tuple of Allele[G]
    */
  def distinct: Product
}

/**
  * This trait defines the concept of Dominance for Mendelian genetics.
  * @return
  */
trait Dominance[G] extends (()=>Option[Allele[G]]) {
  /**
    * the apply method yields, if appropriate, the dominant gene
    * @return Some(allele) or None
    */
  def apply(): Option[Allele[G]] = None
}

case class MendelianGene[P,G](locus: Locus, alleles: Seq[Allele[G]], dominant: Allele[G]) extends AbstractDominanceGene[P,G](locus, alleles, dominant)

abstract class AbstractGene[P,G](locus: Locus, as: Seq[Allele[G]]) extends Gene[P,G] {

  def apply(p: P): Allele[G] = p match {
    case u: Unit => as.head
    case q: Boolean => if (q) as.head else as(1)
    case q: Int => as(q)
    case _ => throw new GeneticsException("type P must be Unit, Boolean or Int")
  }

  val name = locus.name

  /**
    * returns distinct alleles as a Tuple
    *
    * @return a sequence of Allele[G]
    */
  def distinct: Product = {
    val x = as distinct;
    x.length match {
      case 1 => (x.head)
      case 2 => (x.head, x.tail.head)
      case 3 => (x.head, x.tail.head, x.tail.tail.head)
      case _ => throw new GeneticsException(s"unsupported number of distinct alleles: $x")
    }
  }
}

abstract class AbstractDominanceGene[P,G](locus: Locus, as: Seq[Allele[G]], dominant: Allele[G]) extends AbstractGene[P,G](locus,as) with Dominance[G]

/**
  * An allele with a particular name/identifier
  *
  * @param t the value of this Allele
  * @tparam G the type of the value
  */
case class Allele[G](t: G) extends Identifier {
  override def name: String = t.toString
}
