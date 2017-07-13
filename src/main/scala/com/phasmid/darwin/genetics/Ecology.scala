/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

import com.phasmid.laScala.fp.FP._


/**
  * Created by scalaprof on 5/9/16.
  */
case class Ecology[T, X](name: String, factors: Map[String, Factor], fitness: FitnessFunction[T, X], adapter: Adapter[T, X]) extends Ecological[T, X] with Identifier {

  /**
    * The apply method for this Ecology. For each Trait in the given Phenotype, we look up its corresponding Factor
    * and invoke the Adapter to create an Adaptation.
    *
    * Note that if the lookup fails, we simply ignore the trait without warning.
    *
    * @param phenotype the phenotype for which we want to measure the adaptation to this ecology
    * @return an Adaptatype
    */
  def apply(phenotype: Phenotype[T]): Adaptatype[X] = {
    val xats = for (t <- phenotype.traits; f <- factors.get(t.characteristic.name)) yield for (a <- adapter(f, t, fitness)) yield a
    Adaptatype(sequence(xats).get)
  }
}

case class Factor(name: String) extends Identifier
