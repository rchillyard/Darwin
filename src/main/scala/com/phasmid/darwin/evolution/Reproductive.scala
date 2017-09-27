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

trait ASexual[B, G, T, V, X, OrganismType <: Organism[B, G, Unit, T, V, X]] extends Reproductive[OrganismType] {

  override def apply(): Iterable[OrganismType] = reproduce

  def reproduce: Iterable[OrganismType]
}

trait Sexual[B, G, T, V, X, OrganismType <: Organism[B, G, Boolean, T, V, X]] extends Reproductive[OrganismType] {

  override def apply(): Iterable[OrganismType] = mate(pool)

  def mate(evolvable: Evolvable[X]): Iterable[OrganismType]

  def pool: Evolvable[X]
}
