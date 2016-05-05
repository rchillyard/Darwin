package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
 * @author scalaprof
 */
trait Genomic[A] {
  def chromosomes: Int
  def ploidy: Int
}

trait Gene[A] extends Seq[Allele[A]] with Identifier

case class Allele[A](name: String, bases: Seq[A]) extends Identifier

trait Locus[A] extends Seq[Gene[A]]

trait Chromosome[A] extends Seq[Locus[A]] {
  def isSex: Boolean
}

trait Genotype[A,B] extends Seq[Chromosome[A]] {
  def genomic: Genomic[A]
  def express(expresser: Expresser[A,B]): Phenotype[B]
}

trait Expresser[A,B] extends (Locus[A]=>Trait[B])

trait Phenotype[B] extends Seq[Trait[B]] {
  def fitness[C](environment: Environment[C])(fitnessFunction: FitnessFunction[B,C]): Probability
}

trait Trait[B] extends Identifier {
}

trait FitnessFunction[B,C]

trait Environment[C] extends Seq[EcoFactor[C]] with Identifier

trait EcoFactor[C] extends (() => C) with Identifier

trait Identifier {
  def name: String
  override def toString = name
}

case class Probability(p: Double)

object Allele {
  def apply(name: String, base: Base): Allele[Base] = apply(name,Seq(base))
}
