package com.phasmid.darwin.genetics

/**
  * This class represents the Phenotype for an Organism. The Phenotype is "expressed" from the Genotype with respect to
  * the Phenome of the Organism.
  *
  * @param traits all the Traits that make up this Phenotype
  * @tparam T the underlying type of the Traits
  * @author scalaprof
  *         Created by scalaprof on 5/5/16.
  */
case class Phenotype[T](traits: Seq[Trait[T]])

/**
  *
  * @param characteristic the Characteristic of this Trait
  * @param value          the T value of this Trait
  * @tparam T the underlying type of the Trait
  */
case class Trait[T](characteristic: Characteristic, value: T)