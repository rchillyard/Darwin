package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

import scala.util.Try

/**
  * This class models the physical genetic material from which a genotype is derived.
  *
  * Created by scalaprof on 5/5/16.
  */
case class Strand[+B](bases: Seq[B]) {
  def locate(locus: Locus): Option[Seq[B]] =
    if (locus.offset>=0 && locus.length+locus.offset<=bases.length)
      Try(bases.slice(locus.offset, locus.offset+locus.length)).toOption
    else None
  def :+[Z>:B] (other: Strand[Z]): Strand[Z] = :+ (other.bases)
  def +:[Z>:B] (other: Strand[Z]): Strand[Z] = (other.bases) +: this
  def :+[Z>:B] (other: Seq[Z]): Strand[Z] = Strand(bases++other)
  def +:[Z>:B] (other: Seq[Z]): Strand[Z] = Strand(other++:bases)
  def :+[Z>:B] (other: Z): Strand[Z] = :+(Seq(other))
  def +:[Z>:B] (other: Z): Strand[Z] = +:(Seq(other))

  // TODO bring back the implicit renderer: but be aware that can
  // mess up the concatenation methods above.
  override def toString = bases.mkString("", "", "")
}

trait Renderer[B] extends (Seq[B]=>String)

/**
 * @author scalaprof
 *
 */
object Strand {
  def apply[B](w: String)(implicit conv: Char=>B): Strand[B] = new Strand((for (c <- w) yield conv(c)).toList)
  def create[B](bases: B*) = Strand(bases)
  implicit def renderer[B] = new Renderer[B] {
    def apply(bs: Seq[B]): String = bs.mkString("", "", "")
  }
}
