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

import com.phasmid.darwin.base.Identifiable
import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass}

/**
  * This class represents a genotype: the genes of a particular organism.
  *
  * @tparam P the "ploidy" type:
  *           P is normally a Boolean to distinguish alleles in a diploid arrangement.
  *           But if you want to have a triploid arrangement (or any other ploidy) then you might
  *           want to use something different for P, such Int or Unit (for haploid).
  * @tparam G the underlying Gene type
  * @author scalaprof
  */
case class Genotype[P, G](genes: Seq[Gene[P, G]]) extends Renderable {

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Genotype[Any, Any]]).render(indent)(tab)

}

/**
  * This trait defines the function to take a selector (a P) and return the particular Allele that corresponds to
  * that selection for the given gene.
  *
  * TODO we are somewhat confusing the concepts of a Location and a Gene. The Location isn't really just a position on the
  * chromosome--it should also tell us what possible Alleles can appear there and maybe something about dominance, if any.
  *
  * @tparam P
  *           For a diploid system, P will be Boolean.
  *           For a haploid system, P will be Unit.
  *           Otherwise, P will be Int.
  * @tparam G the underlying Gene type
  */
trait Gene[P, G] extends (P => Allele[G]) with Identifiable {
  def locus: Locus[G]

  /**
    * Method to get all the different alleles present
    *
    * @return a sequence of Allele[G]
    */
  def distinct: Seq[Allele[G]]
}

/**
  * This trait models the notion of a locus in the sense of the alleles that are possible at that locus.
  * Even in diploid systems, it is possible to have more than two alleles. See for example blood types in humans.
  * Additionally, we specify if there is a dominant allele
  * We use Location (in Genome) to model the position on a Chromosome at which the gene (and its alleles) can be found.
  *
  * @tparam G the underlying Gene type
  */
trait Locus[G] extends (() => Set[Allele[G]]) {
  /**
    * @return the Location of this Locus
    */
  def location: Location

  /**
    * @return Some(Allele) if there is a dominant allele; else None
    */
  def dominant: Option[Allele[G]]

  override def toString = s"Locus at $location with dominant: $dominant and possible alleles: ${apply()}"
}

case class PlainLocus[G](location: Location, alleles: Set[Allele[G]], dominant: Option[Allele[G]]) extends Locus[G] with Renderable {
  /**
    * @return the actual Alleles present at this Locus
    */
  def apply(): Set[Allele[G]] = alleles

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[PlainLocus[Any]]).render(indent)(tab)

}

/**
  * A Mendelian gene which is to say one that has/expresses recessive and dominant alleles/traits.
  *
  * @param l  the locus
  * @param as the actual alleles
  * @tparam P
  *           For a diploid system, P will be Boolean.
  *           For a haploid system, P will be Unit.
  *           Otherwise, P will be Int.
  * @tparam G the underlying Gene type
  */
case class MendelianGene[P, G](l: Locus[G], as: Seq[Allele[G]]) extends AbstractGene[P, G](l, as) {
  override def toString = s"""MendelianGene: at $l with alleles: ${as.mkString(", ")}"""

  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[MendelianGene[Any, Any]]).render(indent)(tab)
}

abstract class AbstractGene[P, G](l: Locus[G], as: Seq[Allele[G]]) extends Gene[P, G] {

  /**
    * @return this Gene's Locus
    */
  def locus: Locus[G] = l

  /**
    * Method to yield one of the Alleles present
    *
    * @param p the selector, an instance of the Ploidy type (usually Boolean, but could be Unit or Int)
    * @return the selected Allele
    */
  def apply(p: P): Allele[G] = p match {
    case _: Unit => as.head
    case q: Boolean => if (q) as.head else as(1)
    case q: Int => as(q)
    case _ => throw GeneticsException("type P must be Unit, Boolean or Int")
  }

  val name: String = locus.location.name

  /**
    * Returns distinct alleles as a Tuple
    *
    * @return a sequence of Allele[G]
    */
  def distinct: Seq[Allele[G]] = as.distinct

  override def toString: String = s"$name:$as"
}

/**
  * An allele with a particular name/identifier
  *
  * @param t the value of this Allele
  * @tparam G the type of the value
  */
case class Allele[G](t: G) extends Identifiable {
  override def name: String = t.toString

  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Allele[Any]]).render(indent)(tab)
}
