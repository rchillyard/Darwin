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
  val mapper: Allele[G]=>Trait[T]
}

abstract class AbstractExpresserMendelian[P,G,T] extends Expresser[P,G,T] {
  def apply(gene: Gene[P,G]): Trait[T] = {
    def isDominant(a: Allele[G]) = gene match {
      case g: Dominance[G] => g()==a
      case _ => throw new GeneticsException(s"gene does not extend Dominance")
    }
    gene distinct match {
      case Product1(x) => mapper(x.asInstanceOf[Allele[G]])
      case Product2(x,y) => val a = x.asInstanceOf[Allele[G]]; if (isDominant(a)) mapper(a) else mapper(y.asInstanceOf[Allele[G]])
      case _ => throw new GeneticsException(s"Mendelian logic problem with gene")
    }
  }
}

case class ExpresserMendelian[P,G,T](mapper: Allele[G]=>Trait[T]) extends AbstractExpresserMendelian[P,G,T]