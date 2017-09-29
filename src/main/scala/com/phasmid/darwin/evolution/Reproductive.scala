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

package com.phasmid.darwin.evolution

trait Reproductive[O] extends (() => Iterable[O])

/**
  * This trait defines reproductive style.
  *
  * @tparam P the Ploidy type
  */
trait Sexual[P] {
  /**
    * Determine if this reproduction style is sexual
    *
    * @return true for diploid or polyploid genomes
    */
  def sexual(p: P): Boolean = p match {
    case _: Int => true
    case _: Boolean => true
    case _ => false
  }

}

trait ASexual[B, G, T, V, X, OrganismType <: Organism[B, G, Unit, T, V, X]] extends Reproductive[OrganismType] {

  override def apply(): Iterable[OrganismType] = fission

  /**
    * Method to define, in general terms, the means of reproducing this ASexual object.
    * In strict Fission, there is only one daughter cell. But here, we may have many daughter cells.
    *
    * @return
    */
  def fission: Iterable[OrganismType]
}

trait Mating[B, G, T, V, X, OrganismType <: Organism[B, G, Boolean, T, V, X]] extends Reproductive[OrganismType] {

  override def apply(): Iterable[OrganismType] = mate(pool)

  /**
    * Method to define the means of reproducing from this Sexual object and one of the pool of potential mates taken from the members of evolvable.
    *
    * @param evolvable the pool of potential mates. In general, this evolvable may include organisms of the same sex as this which allows for the possibility of hermaphroditic reproduction.
    * @return an Iterable of organisms, i.e. progeny from this mating.
    */
  def mate(evolvable: Evolvable[X]): Iterable[OrganismType]

  /**
    * Method to define a pool of potential mates for this object.
    * In the general case, this pool of mates will include members of a different Colony who have entered the territory of this Colony for the purpose of mating.
    *
    * @return an Evolvable which contains a number of organisms from which to choose a mate for this object.
    *         Note that the result may include this object and may include members of this object's own sex.
    */
  def pool: Evolvable[X]
}
