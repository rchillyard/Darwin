package com.phasmid.darwin

import com.phasmid.darwin.genetics.Sequence
import com.phasmid.darwin.genetics.dna.Base

/**
  * The genetics package contains all of the code which is the basis of genetic algorithms.
  * There are four distinct "models":
  * <dl>
  *   <dt>Sequence</dt>
  *   <dd>This is a random collection of values which is the source of variation in the adaptability of organisms
  *   to an environment.</dd>
  *   <dt>Genotype</dt>
  *   <dd>This is the set of an organim's "genes" which are transcribed from the Sequences.</dd>
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
//    * @tparam B is the underlying type of the Sequence: for natural genetics, B is Base, that's to say one of a
//    *           set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
//    *           But different applications might want to choose something else.
    */
  type SequenceSet[B] = Seq[Sequence[B]]

  /**
    * We are using the term Nucleus here to denote: all the physical genetic material that creates variability in genotypes.
    * If you can think of a better name, please let me know.
    *
    * The cardinality of this set is the same as the karyotype (number of chromosome pairs) for the Genome that transcribes it.
    * For humans, that number is 23
    *
//    * @tparam B is the underlying type of the Sequence: for natural genetics, B is Base, that's to say one of a
//    *           set of four alphabetic bases made up of proteins and which make up the molecule called DNA.
//    *           But different applications might want to choose something else.
    */
  type Nucleus[B] = Seq[SequenceSet[B]]

  type NucleusDNA = Nucleus[Base]

  type NaturalGenome = Genome[Base,Boolean,String]

}
