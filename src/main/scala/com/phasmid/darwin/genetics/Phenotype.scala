package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
  * @tparam T
  *
 * @author scalaprof
 */
case class Phenotype[T](phenome: Phenome, traits: Seq[Trait[T]])

/**
 * @author scalaprof
 *
 * @tparam T
 */
case class Trait[T](value: T)