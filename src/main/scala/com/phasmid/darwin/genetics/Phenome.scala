package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
case class Phenome[P,G,T](name: String, characteristics: Seq[Characteristic], expresser: Expresser[P,G,T]) extends Phenomic[P,G,T] {

  /**
    * method to express this Phenome
    * @param genotype
    * @return
    */
  def apply(genotype: Genotype[P,G]): Phenotype[T] = Phenotype(for (g <- genotype.genes) yield expresser(g))
}

/**
 * @author scalaprof
 *
 */
case class Characteristic(name: String) extends Identifier

/**
  * Phenomic is a trait which provides the functionality to express a Genotype (that's to say a sequence of Genes)
  * into a Phenotype. As far as I'm aware, Phenomic is not a real word.
  *
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) Boolean (ploidy=2)
  * @tparam G the underlying Gene value type
  * @tparam T the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Base
  */
trait Phenomic[P,G,T] extends (Genotype[P,G]=>Phenotype[T]) with Identifier

