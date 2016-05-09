package com.phasmid.darwin

import com.phasmid.darwin.genetics.dna.Base

/**
  * The genetics package contains all of the code which is the basis of genetic algorithms.
  * There are four distinct "models":
  * <dl>
  * <dt>Sequence</dt>
  * <dd>This is a random collection of values which is the source of variation in the adaptability of organisms
  * to an environment.</dd>
  * <dt>Genotype</dt>
  * <dd>This is the set of an organim's "genes" which are transcribed from the Sequences.</dd>
  * </dl>
  * Created by scalaprof on 5/5/16.
  */
package object genetics {

  trait Identifier {
    def name: String

    override def toString = name
  }

  /**
    * The cardinality of this set is the same as the ploidy for the Genome that transcribes it.
    * For diploid genetics, that number is 2
    *
    * //    * @tparam B is the underlying type of the Sequence: for natural genetics, B is Base, that's to say one of a
    * //    *           set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    * //    *           But different applications might want to choose something else.
    */
  type SequenceSet[B] = Seq[Sequence[B]]

  /**
    * We are using the term Nucleus here to denote: all the physical genetic material that creates variability in genotypes.
    * If you can think of a better name, please let me know.
    *
    * The cardinality of this set is the same as the karyotype (number of chromosome pairs) for the Genome that transcribes it.
    * For humans, that number is 23
    *
    * //    * @tparam B is the underlying type of the Sequence: for natural genetics, B is Base, that's to say one of a
    * //    *           set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
    * //    *           But different applications might want to choose something else.
    */
  type Nucleus[B] = Seq[SequenceSet[B]]

  type NucleusDNA = Nucleus[Base]

  type NaturalGenome = Genome[Base, Boolean, String]

  /**
    * Genomic is a trait which provides the functionality to transcribe a Nucleus (that's to say a matrix of Sequences)
    * into a Genotype.
    *
    * //@tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
    * //@tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) is Boolean (ploidy=2);
    * //          for haploid: P is Unit;
    * //          for polyploid: P is Int.
    * //@tparam G the underlying gene value type
    */
  type Genomic[B, P, G] = (Nucleus[B]) => Genotype[P, G]

  /**
    * Phenomic is a type which provides the functionality to express a Genotype (that's to say a sequence of Genes)
    * into a Phenotype. As far as I'm aware, Phenomic is not a real word.
    *
    * //@tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) Boolean (ploidy=2)
    * //@tparam G the underlying Gene value type, typically String
    * //@tparam T the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
    */
  type Phenomic[P, G, T] = (Genotype[P, G]) => Phenotype[T]

  /**
    * No... I think what we want to do is to create another model from the Phenotype: an adaptation.
    * This adaptation can then be crossed with an Environment to determine the fitness function.
    *
    * This type models the evaluation of adaptation for a specific Phenotype in an Environment
    * //@tparam T the underlying type of the Traits
    * //@tparam X the underlying type of the ecological types such as Environment
    */
  type Ecological[T,X] = (Phenotype[T]) => Adaptatype[X]
}
