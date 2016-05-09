package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
  * @tparam T the underlying type of the Traits
  *
 * @author scalaprof
 */
case class Phenotype[T](traits: Seq[Trait[T]])

/**
  * @tparam T the underlying type of the Trait
 */
case class Trait[T](characteristic: Characteristic, value: T)