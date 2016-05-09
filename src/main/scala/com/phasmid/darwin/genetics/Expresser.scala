package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/7/16.
  *
  * @tparam P the ploidy type
  * @tparam G the gene type
  * @tparam T the trait type
  */
trait Expresser[P,G,T]{
  def apply(gene: Gene[P,G]): Trait[T]
  val mapper: (Locus[G],Allele[G])=>Trait[T]
}

abstract class AbstractExpresserMendelian[P,G,T] extends Expresser[P,G,T] {
  def apply(gene: Gene[P,G]): Trait[T] = {
    val locus = gene.locus
    //noinspection ComparingUnrelatedTypes
    def isDominant(a: Allele[G]) = gene.locus.dominant match {
      case Some(x) => x==a
      case _ => throw new GeneticsException(s"gene does not define dominant")
    }
    gene.distinct match {
        // XXX it would be nice to avoid these instanceOf operators. Maybe we should give up on Product and just use Seq
      case Product1(x) => mapper(locus,x.asInstanceOf[Allele[G]])
      case Product2(x,y) => val a = x.asInstanceOf[Allele[G]]; if (isDominant(a)) mapper(locus,a) else mapper(locus,y.asInstanceOf[Allele[G]])
      case _ => throw new GeneticsException(s"Mendelian logic problem with gene $gene")
    }
  }
}

case class ExpresserMendelian[P,G,T](mapper: (Locus[G],Allele[G])=>Trait[T]) extends AbstractExpresserMendelian[P,G,T]