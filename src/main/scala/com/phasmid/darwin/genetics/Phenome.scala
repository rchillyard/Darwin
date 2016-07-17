package com.phasmid.darwin.genetics

import com.phasmid.laScala.FP

import scala.util.Try

/**
  * This class represents a Phenome: that's to say the template for creating a Phenotype as a result of "expressing" a Genotype.
  * Phenome is to Genome as Phenotype is to Genotype.
  * Furthermore, Expresser is to Phenome as Transcriber is to Genome;
  * and characteristics is to Phenome as karyotype is to Genome;
  * and Trait is to Phenome as Gene is to Genome.
  * And, finally, Phenome is to Phenomic as Genome is to Genomic.
  *
  * Created by scalaprof on 5/5/16.
  *
  * @param name            the identifier of this Phenome, for example Homo Sapiens, or more generally, say, Apes.
  * @param characteristics the "characteristics" modeled by this Phenome: properties that are represented in a specific Phenotype
  *                        as Traits.
  * @param expresser       the Expresser function which maps Genes into Traits.
  * @tparam P the ploidy type for the Genotype, typically (for eukaryotic genetics) Boolean (ploidy=2)
  * @tparam G the underlying Gene value type, typically String
  * @tparam T the underlying type of Phenotype and its Traits, typically (for natural genetic algorithms) Double
  */
case class Phenome[P, G, T](name: String, characteristics: Map[Locus[G], Characteristic], expresser: Expresser[P, G, T]) extends Phenomic[P, G, T] with Identifier {
  /**
    * Method to express a Genotype with respect to this Phenome.
    * Note that if a Locus doesn't have a mapping in the characteristics map, we currently ignore it.
    *
    * CONSIDER making the key for the characteristics a String (the locus name) rather than the whole Locus.
    *
    * @param genotype the genotype to be expressed
    * @return a Phenotype
    */
  def apply(genotype: Genotype[P, G]): Phenotype[T] = {
    val ttts: Seq[Try[Trait[T]]] = for (g <- genotype.genes; c <- characteristics.get(g.locus)) yield for (t <- expresser(c, g)) yield t
    Phenotype(FP.sequence(ttts).get)
  }
}

/**
  * This class defines a Characteristic, that's to say the "type" or "domain" of a Trait.
  *
  * @param name the identifier of this Characteristic
  */
case class Characteristic(name: String) extends Identifier
