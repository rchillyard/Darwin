package com.phasmid.darwin.evolution

import com.phasmid.darwin.eco.{Fit, Survival}
import com.phasmid.laScala.Sequential
import com.phasmid.laScala.values.Rational

import scala.util.Try

trait Evolute[Z] extends Iterable[Z] with Permutable[Z] with Sequential[Evolute[Z]] {

  def build(zs: Seq[Z]): Evolute[Z]

  def *(fraction: Rational[Long]): Seq[Z]

}

trait Reproductive[Z] {

  def progeny(zs: Seq[Z]): Seq[Z]
}


abstract class BaseEvolute[Z: Survival : Reproductive](zs: Seq[Z])(implicit random: RNG[Long]) extends Evolute[Z] {
  def iterator: Iterator[Z] = zs.iterator

  /**
    * Get the next generation of this Evolute
    *
    * @param isSnapshot ignored
    * @return the result of nextGeneration, wrapped in Try
    */
  def next(isSnapshot: Boolean): Try[Evolute[Z]] = Try(nextGeneration)

  /**
    * This method yields an evolute from the elements of xs which survive this generation.
    * Note that, although the default implementation simply culls all the unfit xs
    * and keeps all of the fit xs, sub-classes may redefine this method to allow a random choice
    * of survivors which is only partially guided by fitness.
    *
    * @return an Evolute containing the members of this Evolute which survive this generation.
    */
  protected def survivors: Seq[Z] = zs filter (implicitly[Survival[Z]].survives(_))

  /**
    * This method yields the complement of survivors such that survivors + nonSurvivors = this
    *
    * @return
    */
  protected def nonSurvivors: Seq[Z] = this - survivors

  /**
    * @param i the iterator whose elements are to be removed
    * @return an Evolute composed from this but without any of the elements of i
    */
  protected def -(i: Seq[Z]): Seq[Z] = zs.filterNot(i.toSet)

  /**
    * This method randomly selects a fraction of this Evolute
    *
    * @param fraction the fraction of xs that will be randomly selected.
    * @return an Iterator containing a randomly chosen fraction of the xs of this.
    */
  def *(fraction: Rational[Long]): Seq[Z] = permute.take((fraction * zs.size).floor.toInt).toSeq

  private def nextGeneration: Evolute[Z] = {
    val z = survivors
    build(z ++ implicitly[Reproductive[Z]].progeny(z ++ build(nonSurvivors).*(implicitly[Survival[Z]].posthumousProgeny(true).toDouble)))
  }

}

/**
  *
  * @param zs an unsorted collection of objects which, together, are undergo evolution
  * @tparam Z underlying type of the members of the evolute.
  */
case class Evolute_Colony[Z: Fit : Reproductive : Survival](zs: Seq[Z])(implicit random: RNG[Long]) extends BaseEvolute[Z](zs) {
  def build(_zs: Seq[Z]): Evolute[Z] = Evolute_Colony(_zs)
}

