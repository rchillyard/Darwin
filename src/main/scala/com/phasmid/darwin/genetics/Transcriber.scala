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

/**
  * A Transcriber is the heart of the process for taking Sequence information and generating its corresponding Genotype.
  * There are two more or less independent phases, and one phase which combines the two others:
  * <ol>
  * <li>locateBases: locate the region of the Sequence at which the locus is to be found;</li>
  * <li>transcribeBases: transcribe that region into a particular Allele.</li>
  * <li>apply: locateBases and transcribeBases.</li>
  * </ol>
  * All three methods may be overridden by extenders of Transcriber, but transcribeBases MUST be defined.
  *
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam G the gene type
  */
sealed trait Transcriber[B, G] extends TranscriberFunction[B, G] {
  /**
    * This method locates a Seq[B] from a Sequence[B] according to the details of the given locus
    *
    * @param bs       the Sequence[B] (corresponding to a Chromosome) on which the location is expected to be found
    * @param location the location
    * @return Some(Seq[B]) if the location was found, otherwise None
    */
  def locateBases(bs: Sequence[B], location: Location): Option[Seq[B]] = bs.locate(location)

  /**
    * This method is required to be defined by sub-types (extenders) of Transcriber.
    * Given a Seq[B] corresponding to the location of a gene on a Chromosome, return the Allele that
    * this sequence encodes.
    *
    * @param bs the sequence of bases
    * @return an Allele
    */
  def transcribeBases(bs: Seq[B]): Option[Allele[G]]

  /**
    * This method is called directly by the Genome method transcribe and indirectly by the Genome's
    * apply method.
    * In the event of a failure (for example, a mutation prevents the transcription of a sequence),
    * the result will be a Failure. This is to be expected.
    *
    * It is normally not necessary to override this method.
    *
    * @param bs       the Sequence of bases to transcribe
    * @param location the locus on the Chromosome at which we expect to find the gene we are interested in
    * @return Success(Allele) assuming that all went well, otherwise Failure
    */
  def apply(bs: Sequence[B], location: Location): Option[Allele[G]] = for (bs <- locateBases(bs, location); ga <- transcribeBases(bs)) yield ga
}

/**
  * An abstract base class for extenders of Transcriber.
  *
  * @param f a function which, given a Seq[B] will return an Allele[G]
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam G the gene type
  */
abstract class AbstractTranscriber[B, G](f: Seq[B] => Option[Allele[G]]) extends Transcriber[B, G] {
  /**
    * This method is required to be defined by sub-types (extenders) of Transcriber.
    * Given a Seq[B] corresponding to the location of a gene on a Chromosome, return the Allele that
    * this sequence encodes.
    *
    * @param bs the sequence of bases
    * @return an Allele
    */
  def transcribeBases(bs: Seq[B]): Option[Allele[G]] = f(bs)
}

case class PlainTranscriber[B, G](f: Seq[B] => Option[Allele[G]]) extends AbstractTranscriber[B, G](f)
