package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
 * @author scalaprof
 */
case class Phenotype[T](phenome: Phenome, traits: Seq[Trait[T]])

case class Trait[T](value: T)