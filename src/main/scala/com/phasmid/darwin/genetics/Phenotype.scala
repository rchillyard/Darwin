package com.phasmid.darwin.genetics

/**
  * This class represents the Phenotype for an Organism. The Phenotype is "expressed" from the Genotype with respect to
  * the Phenome of the Organism.
  *
  * @tparam T the underlying type of the Traits
  * @author scalaprof
  * Created by scalaprof on 5/5/16.
  */
case class Phenotype[T](traits: Seq[Trait[T]])

/**
  * @tparam T the underlying type of the Trait
  */
case class Trait[T](characteristic: Characteristic, value: T)