package com.phasmid.darwin.experimental

case class Tribe[A : Genetic](as: Seq[A]) {
  val g = implicitly[Genetic[A]]

  def progeny: Seq[A] = for (b <- lek.pairBonds) yield b.progeny

  def lek: Lek[A] = Lek(as filterNot g.isFemale, as filter g.isFemale)
}

case class Lek[A : Genetic](ms: Seq[A], fs: Seq[A]) {
  def pairBonds: Seq[PairBond[A]] = for ((m,f) <- ms zip fs) yield PairBond(m,f) // TODO make this more sophisticated
}

case class PairBond[A : Genetic](m: A, f: A) {
  val g = implicitly[Genetic[A]]
  def progeny = g.progeny(m,f)
}

trait Member {
  def female: Boolean
}

trait Genetic[A] {
  def isFemale(a: A): Boolean
  def progeny(m: A, f: A): A
}