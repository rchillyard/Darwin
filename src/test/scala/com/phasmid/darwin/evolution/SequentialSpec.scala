package com.phasmid.darwin.evolution

import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

/**
  * Created by scalaprof on 7/25/16.
  */
class SequentialSpec extends FlatSpec with Matchers {

  behavior of "next()"
  it should "work for Int" in {
    val x = IncrementableSequential(0)
    x.next() should matchPattern { case Success(IncrementableSequential(1, false, "")) => }
  }
  it should "work for String" in {
    val x = IncrementableSequential("A")
    x.next() should matchPattern { case Success(IncrementableSequential("B", false, "")) => }
  }
  it should "work for LocalDate" in {
    val now = LocalDate.now()
    val tomorrow = now.plusDays(1)
    val x = IncrementableSequential(now, isSnapshot = false, "d")
    x.next() should matchPattern { case Success(IncrementableSequential(`tomorrow`, false, "d")) => }
  }
  behavior of "next(true)"
  it should "work for Int" in {
    val x0 = IncrementableSequential(0)
    val x1y = x0.next(true)
    x1y should matchPattern { case Success(IncrementableSequential(1, true, "")) => }
    import Ordered._
    implicit val ord = x0
    x0.compare(x1y.get) shouldBe -1
    val x2y = x1y.get.next(true)
    x2y should matchPattern { case Success(IncrementableSequential(1, true, "")) => }
    val x3y = x2y.get.next()
    x3y should matchPattern { case Success(IncrementableSequential(1, false, "")) => }
  }
  it should "work for String" in {
    val x = IncrementableSequential("A")
    x.next(true) should matchPattern { case Success(IncrementableSequential("B", true, "")) => }
  }
}
