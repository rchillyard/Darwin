/*
 * DARWIN Genetic Algorithms Framework Project.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 *
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
 *
 *      This file is part of Darwin.
 *
 *      Darwin is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.phasmid.darwin.genetics

import com.phasmid.darwin.base._
import com.phasmid.darwin.eco.{Fitness, Viability}
import com.phasmid.laScala.fp.FP._
import com.phasmid.laScala.{Prefix, RenderableCaseClass}
import org.slf4j.Logger

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
case class Phenome[G, P, T](name: String, characteristics: Map[Locus[G], Characteristic], expresser: Expresser[G, P, T], attraction: (Trait[T], Trait[T]) => Fitness) extends Phenomic[G, P, T] with Identifiable {

  /**
    * Method to express a Genotype with respect to this Phenome.
    * Note that if a Locus doesn't have a mapping in the characteristics map, we currently ignore it.
    *
    * CONSIDER making the key for the characteristics a String (the locus name) rather than the whole Locus.
    *
    * @param genotype the genotype to be expressed
    * @return a Phenotype
    */
  def apply(genotype: Genotype[G, P]): Phenotype[T] = {
    val ttts: Seq[Try[Trait[T]]] = for (g <- genotype.genes;
                                        //                                        c <- Spy.spy(s"g: $g; charactistics: ", characteristics.get(g.locus))
                                        c <- characteristics.get(g.locus)
    )
      yield for (t <- expresser(c, g))
        yield t
    // TODO create a different, but related, Identifier for phenotype
    Phenotype(IdentifierStrUID("ph", UID(genotype.id)), sequence(ttts).get)
  }

  /**
    * Method to yield for two potential mates, the observer and her (usually her) observed candidate, the fitness of
    * the match to produce a viable offspring. Thus we combine the attractiveness of observed to observer
    * and also their genetic compatibility.
    *
    * @param observer the observer's Phenotype
    * @param observed the observed's Phenotype
    * @return the Fitness of the match
    */
  def attractiveness(observer: Phenotype[T], observed: Phenotype[T]): Fitness = {
    val tts: Seq[(Trait[T], Trait[T])] = for (t1 <- observer.traits; t2 <- observed.traits; if t1.isSexuallySelective && t2.isSexuallySelective) yield (t1, t2)
    Viability(for ((t1, t2) <- tts) yield attraction(t1, t2))()
  }

  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this.asInstanceOf[Phenome[Any, Any, Any]]).render(indent)(tab)

  implicit private val auditLogger: Logger = Audit.getLogger(getClass)
}

/**
  * This class defines a Characteristic, that's to say the "type" or "domain" of a Trait.
  *
  * @param name                the identifier of this Characteristic
  * @param isSexuallySelective (defaults to false) true if this characteristic is observable as a sexually selective trait
  */
case class Characteristic(name: String, isSexuallySelective: Boolean = false) extends Identifiable {
  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = RenderableCaseClass(this).render(indent)(tab)
}
