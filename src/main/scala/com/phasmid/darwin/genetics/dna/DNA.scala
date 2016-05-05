package com.phasmid.darwin.genetics.dna

import com.phasmid.darwin.genetics.Identifier

/**
 * @author scalaprof
 */
//case class DNA(x: Seq[Base]) extends Allele[Base] {
//  def +(b: Base) = DNA(b+:x)
//  def ++(d: DNA) = DNA(d.x++x)
//  def zip(d: DNA) = x zip d.x
//  def euclidean(d: DNA) = (for ((a,b) <- zip(d)) yield DNA.dist(a,b)) reduceLeft{_+_}
//  def basePairs = x.length
//  val name = x.reverse.foldLeft(""){case (s,d) => s+d.toString}
//    // Members declared in scala.collection.IterableLike
//  def iterator: Iterator[Base] = x.iterator
//  // Members declared in scala.collection.SeqLike
//  def apply(idx: Int): Base = x(idx)
//  def length: Int = x.length
//}
trait Base extends Identifier {
  def pair: Base
}
case object Cytosine extends Base {
  def pair = Guanine
  val name = "C"
}
case object Guanine extends Base {
  def pair = Cytosine
  val name = "G"
}
case object Adenine extends Base {
  def pair = Thymine
  val name = "A"
}
case object Thymine extends Base {
  def pair = Adenine
  val name = "T"
}
case class Invalid(x: Char) extends Base {
  def pair = Invalid(x)
  val name = s"<Invalid: $x>"
}
object Base {
  def apply(x: Char) = x match {
    case 'G' => Guanine
    case 'C' => Cytosine
    case 'A' => Adenine
    case 'T' => Thymine
    case _ => Invalid(x)
  }
}

//object DNA {
//  def apply(): DNA = apply(List())
//  def apply(s: String): DNA = s.toSeq.foldLeft(DNA())({_+Base(_)})
//  def dist(a: Base, b: Base) = if (a==b) 0 else 1
//}
