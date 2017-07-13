/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

/**
  * This class represents the Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from the Phenotype with respect to
  * an "Ecology"
  *
  * Created by scalaprof on 5/9/16.
  */
case class Adaptatype[X](adaptations: Seq[Adaptation[X]])

/**
  * This class represents a particular Adaptation of an Organism for an Environment (EcoSystem). The Adaptation is "adapted" from a Trait.
  *
  * Created by scalaprof on 5/9/16.
  *
  * CONSIDER simply extending the fitness function
  */
case class Adaptation[X](factor: Factor, ecoFitness: EcoFitness[X])

