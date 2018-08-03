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

trait Reproduction[O] {
  def isSexual: Boolean
  def extract[B](o: O): Seq[Sequence[B]]
  def replicator[B]: Replicator[B]
  def neonate[B](bss: Sequence[B]): O
}

trait SexualReproduction[O] extends Reproduction[O] {
  def isFemale(o: O): Boolean
  def progeny(f: O, m: O): O
  def fertilize[B](f: Seq[Sequence[B]], m: Seq[Sequence[B]]): O
}

trait AsexualReproduction[O] extends Reproduction[O] {
  def fission(o: O): O
}

case class Lek[A : SexualReproduction](ms: Seq[A], fs: Seq[A]) {
  def pairBonds: Seq[PairBond[A]] = for ((m,f) <- ms zip fs) yield PairBond(f, m) // TODO make this more sophisticated
}

case class PairBond[A : SexualReproduction](f: A, m: A) {
  val g = implicitly[SexualReproduction[A]]
  def progeny = g.progeny(f, m)
}
