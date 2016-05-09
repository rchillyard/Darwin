package com.phasmid.darwin.genetics


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

  * @param name the identifier for this EcoSystem
  * @param factors the (eco) factors that we expect to find in Environments which support this eco-system
  * @param fitnessFunction the function which measures the fitness of a particular Phenotype within a particular Environment
  * @tparam X the underlying type of the ecological types such as Environment
  * @tparam T the underlying type of the Traits
  */
case class EcoSystem[X,T](name: String, factors: Map[Trait[T], Factor], fitnessFunction: FitnessFunction[X, T]) extends Ecological[X,T] with Identifier {

  // FIXME implement me
  def apply(phenotype: Phenotype[T], environment: Environment[X]): Fitness = ???
}

case class Factor(name: String) extends Identifier

