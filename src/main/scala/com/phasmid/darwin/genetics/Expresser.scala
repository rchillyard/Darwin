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

import com.phasmid.laScala.fp.FP
import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass}

import scala.util.Try

/**
  * Created by scalaprof on 5/7/16.
  *
  * The trait Expresser defines the mechanism for expressing a Gene (given its Characteristic) into a Trait.
  * There are two more or less independent phases, and one phase which combines the two others:
  * <ol>
  * <li>selectAllele: select which allele is expressed by the gene; The default implementation of selectAllele
  * assumes that the gene is Mendelian in nature and tries to determine its dominant allele.</li>
  * <li>traitMapper: create a trait from the given characteristic and allele.</li>
  * <li>apply: selectAllele and traitMapper.</li>
  * </ol>
  * All three methods may be overridden in extenders of Expresser, but traitMapper MUST be defined.
  *
  * //@tparam P the ploidy type
  * //@tparam G the gene type
  * //@tparam T the trait type
  */
sealed trait Expresser[P, G, T] extends ExpresserFunction[P, G, T] {
  /**
    * Method to select the operative Allele for this Gene.
    * If your application is based on non-Mendelian genetics, you will need to override this method.
    *
    * @param gene the given gene
    * @return the expressed allele
    */
  def selectAllele(gene: Gene[P, G]): Allele[G] = {
    //noinspection ComparingUnrelatedTypes
    def isDominant(a: Allele[G]) = gene.locus.dominant match {
      case Some(x) => x == a
      case _ => throw GeneticsException(s"gene does not define dominant")
    }

    gene.distinct match {
      case ga :: Nil => ga
      case gas@ga1 :: ga2 :: Nil => if (isDominant(ga1)) ga1 else if (isDominant(ga2)) ga2 else throw GeneticsException(s"Mendelian logic problem: neither allele is dominant: $gas")
      case _ => throw GeneticsException(s"Mendelian logic problem with gene $gene")
    }
  }

  /**
    * Function to make a Trait given a Characteristic and an Allele.
    */
  val traitMapper: TraitMapper[G, T]

  /**
    * Method to make a Trait given a Gene.
    *
    * @param ch   the Characteristic
    * @param gene the given gene
    * @return a new Trait
    */
  def apply(ch: Characteristic, gene: Gene[P, G]): Try[Trait[T]] = traitMapper(ch, selectAllele(gene))
}

abstract class AbstractExpresser[P, G, T] extends Expresser[P, G, T]

case class ExpresserMendelian[P, G, T](traitMapper: TraitMapper[G, T]) extends AbstractExpresser[P, G, T] with Renderable {
  override def toString(): String = s"ExpresserMendelian($traitMapper)"

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[ExpresserMendelian[Any, Any, Any]]).render(indent)(tab)
}

case class TraitMapperMapped[G, T](map: Map[Characteristic, Map[G, T]]) extends TraitMapper[G, T] with Renderable {
  def apply(ch: Characteristic, ga: Allele[G]): Try[Trait[T]] = FP.optionToTry(
    ga match {
      case Allele(g) => for (m <- map.get(ch); t <- m.get(g)) yield Trait(ch, t)
      case _ => None
    },
    GeneticsException(s"TraitMapperMapped: no trait defined for: $ch, $ga")
  )

  override def toString(): String = s"TraitMapperMapped($map)"

  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[TraitMapperMapped[Any, Any]]).render(indent)(tab)
}
