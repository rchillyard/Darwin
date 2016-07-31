package com.phasmid.darwin.evolution

import com.phasmid.laScala.Incrementable

/**
  * Created by scalaprof on 7/25/16.
  *
  * @tparam Q the type of object in this Generation.
  */
trait Generation[Q] {

  /**
    * @return a new generation of X
    */
  def next: Generation[Q]

  /**
    * @return the value of this Generation
    */
  def get: Q
}

/**
  * BaseGeneration is an abstract class which implements Generation and relies on the fact
  * that the underlying type is Incrementable
  *
  * @param q the current q-value
  * @tparam Q the type of object in this Generation.
  */
abstract class BaseGeneration[Q: Incrementable](q: Q) extends Generation[Q] {
  def build(q: Q): Generation[Q]

  def next: Generation[Q] = build(implicitly[Incrementable[Q]].increment(q).get)

  def get = q
}

/**
  * This case class yields a simple sequentially-numbered Generation
  *
  * @param generation the generation
  */
case class NumberedGeneration(generation: Int) extends BaseGeneration[Int](generation) {
  override def build(q: Int): Generation[Int] = NumberedGeneration(q)
}


