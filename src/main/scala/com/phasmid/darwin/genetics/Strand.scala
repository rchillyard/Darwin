package com.phasmid.darwin.genetics

/**
  * This class models the physical genetic material from which a genotype is derived.
  *
  * Created by scalaprof on 5/5/16.
  */
case class Strand[B](bases: Seq[B])(implicit render: Seq[B]=>String) {
  def locate(locus: Locus): Seq[B] = bases.slice(locus.offset, locus.offset+locus.length)
  def :+ (other: Seq[B]) = Strand(bases++other)
  def +: (other: Seq[B]) = Strand(other++bases)
  override def toString = render(bases)
}

/**
 * @author scalaprof
 *
 */
object Strand {
  def create[B](bases: B*)(implicit render: Seq[B]=>String) = Strand(bases)(render)
}
