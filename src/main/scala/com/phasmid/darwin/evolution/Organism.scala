/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

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
