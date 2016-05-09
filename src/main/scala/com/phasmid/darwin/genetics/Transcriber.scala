package com.phasmid.darwin.genetics

import com.phasmid.darwin.util.MonadOps

/**
  * A Transcriber is the heart of the process for taking Sequence information and generating its corresponding Genotype.
  * There are two more or less independent phases, and one phases which combines the two others:
  * <ol>
  *   <li>locateBases: locate the region of the Sequence at which the locus is to be found;</li>
  *   <li>transcribeBases: transcribe that region into a particular Allele.</li>
  *   <li>apply: locate and transcribe.</li>
  * </ol>
  * All three methods may be overridden in extenders of Transcriber, but transcribeBases MUST be defined.
  *
  * @tparam B the underlying type of Nucleus and its Sequences, typically (for natural genetic algorithms) Base
  * @tparam T the gene type
  */
trait Transcriber[B,T] extends ((Sequence[B],Location) => Option[Allele[T]]) {
  /**
    * This method locates a Seq[B] from a Sequence[B] according to the details of the given locus
 *
    * @param bs the Sequence[B] (corresponding to a Chromosome) on which the location is expected to be found
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
  def transcribeBases(bs: Seq[B]): Allele[T]

  /**
    * This method is called directly by the Genome method transcribe and indirectly by the Genome's
    * transcribe method.
    * It is normally not necessary to override this method.
    *
    * @param bs the Sequence of bases to transcribe
    * @param location the locus on the Chromosome at which we expect to find the gene we are interested in
    * @return Some(Allele) assuming that all went well, otherwise None
    */
  def apply(bs: Sequence[B],location: Location): Option[Allele[T]] = MonadOps.optionLift(transcribeBases _)(locateBases(bs,location))
}

case class PlainTranscriber[B,T](f: Seq[B]=>Allele[T]) extends Transcriber[B,T] {
  /**
    * This method is required to be defined by sub-types (extenders) of Transcriber.
    * Given a Seq[B] corresponding to the location of a gene on a Chromosome, return the Allele that
    * this sequence encodes.
    *
    * @param bs the sequence of bases
    * @return an Allele
    */
  override def transcribeBases(bs: Seq[B]): Allele[T] = f(bs)
}
