package com.phasmid.darwin.genetics

/**
  * No... I think what we want to do is to create another model from the Phenotype: an adaptation.
  * This adaptation can then be crossed with an Environment to determine the fitness function.
  *
  * This trait models the evaluation of adaptation for a specific Phenotype in an Environment
  * @tparam X the underlying type of the ecological types such as Environment
  * @tparam T the underlying type of the Traits
  */
trait Ecological[X,T] extends ((Phenotype[T],Environment[X])=>Fitness) with Identifier

/**
  * FIXME
  *
  * This class represents an EcoSystem: that's to say the template for creating an Environment blah blah... as a result of "expressing" a Genotype.
  * Phenome is to Genome as Phenotype is to Genotype.
  * Furthermore, Expresser is to Phenome as Transcriber is to Genome;
  * and characteristics is to Phenome as karyotype is to Genome;
  * and Trait is to Phenome as Gene is to Genome.
  * And, finally, Phenome is to Phenomic as Genome is to Genomic.
  *

  * @param name
  * @param factors
  * @param fitnessFunction
  * @tparam X the underlying type of the ecological types such as Environment
  * @tparam T the underlying type of the Traits
  */
case class EcoSystem[X,T](name: String, factors: Map[Trait[T], Factor], fitnessFunction: FitnessFunction[X, T]) extends Ecological[X,T] {

  def apply(phenotype: Phenotype[T], environment: Environment[X]): Fitness = ???
}

case class Factor(name: String) extends Identifier

