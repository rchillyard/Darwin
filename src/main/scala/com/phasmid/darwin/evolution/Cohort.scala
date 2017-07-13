/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.evolution

import scala.collection.{TraversableLike, mutable}

/**
  * Created by scalaprof on 7/11/17.
  */
case class Cohort[T](ts: Iterable[T]) extends TraversableLike[T, Cohort[T]] {
  def foreach[U](f: (T) => U): Unit = {seq map f}

  protected[this] def newBuilder: mutable.Builder[T, Cohort[T]] = new mutable.Builder[T, Cohort[T]] {
    private val list = mutable.MutableList[T]()

    def +=(elem: T): this.type = {list += elem; this}

    def clear(): Unit = list.clear()

    def result(): Cohort[T] = Cohort(list)
  }

  def seq: TraversableOnce[T] = ts.iterator
}
