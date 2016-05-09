package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/9/16.
  */
case class Ecology[T, X](name: String, factors: Map[String, Factor], fitness: FitnessFunction[T, X], adapter: Adapter[T, X]) extends Ecological[T, X] with Identifier {

  /**
    * The apply method for this Ecology. For each Trait in the given Phenotype, we lookup its corresponding Factor
    * and invoke the Adapter to create an Adaptation.
    *
    * Note that if the lookup fails, we simply ignore the trait without warning.
    *
    * @param phenotype the phenotype for which we want to measure the adaptation to this ecology
    * @return an Adaptatype
    */
  def apply(phenotype: Phenotype[T]): Adaptatype[X] =
    Adaptatype(for (t <- phenotype.traits; f <- factors.get(t.characteristic.name); p <- adapter.apply(f, t, fitness)) yield p)
}

case class Factor(name: String) extends Identifier
