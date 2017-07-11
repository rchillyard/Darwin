package com.phasmid.darwin.evolution

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

/**
  * Created by scalaprof on 7/25/16.
  */
class SequentialSpec extends FlatSpec with Matchers {

  behavior of "next"
  it should "work for Int" in {
    val x = IncrementableSequential(0)
    x.next should matchPattern { case Success(IncrementableSequential(1)) => }
  }
  it should "work for String" in {
    val x = IncrementableSequential("A")
    x.next should matchPattern { case Success(IncrementableSequential("B")) => }
  }
}
