package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

import scala.util.Try

/**
  * This class models the physical genetic material from which a genotype is derived.
  *
  * Created by scalaprof on 5/5/16.
  */
case class Sequence[+B](bases: Seq[B]) {
  def locate(location: Location): Option[Seq[B]] =
    if (location.offset>=0 && location.length+location.offset<=bases.length)
      Try(bases.slice(location.offset, location.offset+location.length)).toOption
    else None
  def :+[Z>:B] (other: Sequence[Z]): Sequence[Z] = :+ (other.bases)
  def +:[Z>:B] (other: Sequence[Z]): Sequence[Z] = other.bases +: this
  def :+[Z>:B] (other: Seq[Z]): Sequence[Z] = Sequence(bases++other)
  def +:[Z>:B] (other: Seq[Z]): Sequence[Z] = Sequence(other++:bases)
  def :+[Z>:B] (other: Z): Sequence[Z] = :+(Seq(other))
  def +:[Z>:B] (other: Z): Sequence[Z] = +:(Seq(other))

  // TODO bring back the implicit renderer: but be aware that can
  // mess up the concatenation methods above.
  override def toString = bases.mkString("", "", "")
}

trait Renderer[B] extends (Seq[B]=>String)

/**
 * @author scalaprof
 *
 */
object Sequence {
  def apply[B](w: String)(implicit conv: Char=>B): Sequence[B] = new Sequence((for (c <- w) yield conv(c)).toList)
  def create[B](bases: B*) = Sequence(bases)
  implicit def renderer[B] = new Renderer[B] {
    def apply(bs: Seq[B]): String = bs.mkString("", "", "")
  }
}
