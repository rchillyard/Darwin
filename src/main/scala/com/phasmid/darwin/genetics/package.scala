/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin

import com.phasmid.darwin.genetics.dna.Base

import scala.util.Try

/**
  * The genetics package contains all of the code which is the basis of genetic algorithms.
  * There are four distinct "models":
  * <dl>
  * <dt>Sequence</dt>
  * <dd>This is a random collection of values which is the source of variation in the adaptability of organisms
  * to an environment.</dd>
  * <dt>Genotype</dt>
  * <dd>This is the set of an organism's "genes" which are transcribed from the Sequences.</dd>
  * </dl>
  * Created by scalaprof on 5/5/16.
  */
package object genetics {

  trait Identifier {
    def name: String

    override def toString: String = name
  }

  /**
    * The cardinality of this set is the same as the ploidy for the Genome that transcribes it.
    * For diploid genetics, that number is 2
    *
    * @tparam BaseType is the underlying type of the Sequence: for natural genetics, BaseType is Base, that's to say one of a
    *                  set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    *                  But different applications might want to choose something else.
    */
  type SequenceSet[BaseType] = Seq[Sequence[BaseType]]

  /**
    * We are using the term Nucleus here to denote: all the physical genetic material that creates variability in genotypes.
    * If you can think of a better name, please let me know.
    *
    * The cardinality of this set is the same as the karyotype (number of chromosome pairs) for the Genome that transcribes it.
    * For humans, that number is 23
    *
    * @tparam BaseType is the underlying type of the Sequence: for natural genetics, BaseType is Base, that's to say one of a
    *                  set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    *                  But different applications might want to choose something else.
    */
  type Nucleus[BaseType] = Seq[SequenceSet[BaseType]]

  /**
    * A specific nuclear type where BaseType is set to Base, that's to say as in the nuclei of the natural world.
    */
  type NucleusDNA = Nucleus[Base]

  /**
    * A specific Genome type where BaseType is set to Base, Ploidy is Boolean and GeneType is String.
    * That's to say, genomes of the natural, eukaryotic world (everything except bacteria).
    */
  type NaturalGenome = Genome[Base, Boolean, String]

  // Function Types...

  /**
    * Genomic is a trait which provides the functionality to transcribe a Nucleus (that's to say a matrix of Sequences)
    * into a Genotype.
    *
    * @tparam BaseType is the underlying type of the Sequence: for natural genetics, BaseType is Base, that's to say one of a
    *                  set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    *                  But different applications might want to choose something else.
    * @tparam Ploidy   the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
    *                  for haploid: Ploidy is Unit;
    *                  for polyploid: Ploidy is Int.
    * @tparam GeneType the underlying Gene value type, typically String
    */
  type Genomic[BaseType, Ploidy, GeneType] = Nucleus[BaseType] => Genotype[Ploidy, GeneType]

  /**
    * Phenomic is a type which provides the functionality to express a Genotype (that's to say a sequence of Genes)
    * into a Phenotype. As far as I'm aware, Phenomic is not a real word.
    *
    * @tparam Ploidy    the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
    *                   for haploid: Ploidy is Unit;
    *                   for polyploid: Ploidy is Int.
    * @tparam GeneType  the underlying Gene value type, typically String
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    */
  type Phenomic[Ploidy, GeneType, TraitType] = Genotype[Ploidy, GeneType] => Phenotype[TraitType]

  /**
    * No... I think what we want to do is to create another model from the Phenotype: an adaptation.
    * This adaptation can then be crossed with an Environment to determine the fitness function.
    *
    * This type models the evaluation of adaptation for a specific Phenotype in an Environment
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type Ecological[TraitType, EcoType] = Phenotype[TraitType] => Adaptatype[EcoType]

  /**
    * This function type is the type of a parameter of an Adaptation. In the context of an Adapter, this function
    * will yield, for a given EcoType, an optional Fitness.
    *
    * CONSIDER making the result a Try instead of an Option
    *
    * @tparam EcoType the underlying type of the ecological types such as Environment
    */
  type EcoFitness[EcoType] = EcoFactor[EcoType] => Try[Fitness]

  /**
    * This function type is the type of a parameter of an Adapter. For a tuple of trait value, function "type", and eco factor value.
    *
    * TODO need to rename FunctionShape
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type FitnessFunction[TraitType, EcoType] = (TraitType, FunctionShape[TraitType, EcoType], EcoType) => Fitness

  /**
    * This function type is a mapper between a Characteristic/Allele pair and an (optional) Trait. It is used by implementers
    * of the ExpresserFunction
    *
    * @tparam GeneType  the underlying Gene value type, typically String
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    */
  type TraitMapper[GeneType, TraitType] = (Characteristic, Allele[GeneType]) => Try[Trait[TraitType]]

  /**
    * This function type is the basis of the transcription of sequences of bases into genes.
    * This function returns an Option, rather than a Try, because it is relatively normal for the transcriber to fail.
    *
    * @tparam BaseType is the underlying type of the Sequence: for natural genetics, BaseType is Base, that's to say one of a
    *                  set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    *                  But different applications might want to choose something else.
    * @tparam GeneType the underlying Gene value type, typically String
    */
  type TranscriberFunction[BaseType, GeneType] = (Sequence[BaseType], Location) => Option[Allele[GeneType]]

  /**
    * This function type is the basis of the expression of genes into traits.
    *
    * @tparam Ploidy    the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
    *                   for haploid: Ploidy is Unit;
    *                   for polyploid: Ploidy is Int.
    * @tparam GeneType  the underlying Gene value type, typically String
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    */
  type ExpresserFunction[Ploidy, GeneType, TraitType] = (Characteristic, Gene[Ploidy, GeneType]) => Try[Trait[TraitType]]

  /**
    * This function type is the basis of the success of traits into adaptations. [Yes, I know this needs a better explanation].
    *
    * @tparam TraitType the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    * @tparam EcoType   the underlying type of the ecological types such as Environment
    */
  type AdapterFunction[TraitType, EcoType] = (Factor, Trait[TraitType], FitnessFunction[TraitType, EcoType]) => Try[Adaptation[EcoType]]
}
