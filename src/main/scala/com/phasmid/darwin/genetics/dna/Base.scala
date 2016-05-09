package com.phasmid.darwin.genetics.dna

import com.phasmid.darwin.genetics.Identifier

import scala.language.implicitConversions

/**
  * This module defines the DNA base set.
  * Thus Base can be used as the type in Sequence[B] if you want DNA-based genetics.
  * Of course, you could equally well just define Sequence[Int] or Sequence[Boolean] or whatever.
  *
  * @author scalaprof
  */
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

  implicit def convert(c: Char): Base = apply(c)

  implicit val render: Seq[Base] => String = { bs => bs.mkString("", "", "") }
}
