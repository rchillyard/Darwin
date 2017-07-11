package com.phasmid.darwin.genetics

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
      case _ => throw new GeneticsException(s"gene does not define dominant")
    }

    gene.distinct match {
      // XXX it would be nice to avoid these instanceOf operators. Maybe we should give up on Product and just use Seq
      case Product1(x) => x.asInstanceOf[Allele[G]]
      case Product2(x, y) => val a = x.asInstanceOf[Allele[G]]; if (isDominant(a)) a else y.asInstanceOf[Allele[G]]
      case _ => throw new GeneticsException(s"Mendelian logic problem with gene $gene")
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

case class ExpresserMendelian[P, G, T](traitMapper: TraitMapper[G, T]) extends AbstractExpresser[P, G, T]