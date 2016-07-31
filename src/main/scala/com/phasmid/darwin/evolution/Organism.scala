package com.phasmid.darwin.evolution

import com.phasmid.darwin.genetics._

/**
  * Created by scalaprof on 7/27/16.
  */
trait Organism[B, P, G, T, X] {

  def genome: Genome[B, P, G]

  def phenome: Phenome[P, G, T]

  def ecology: Ecology[T, X]

  def nucleus: Nucleus[B]

  def genotype: Genotype[P, G] = genome(nucleus)

  def phenotype: Phenotype[T] = phenome(genotype)

  def adaptatype: Adaptatype[X] = ecology(phenotype)

}
