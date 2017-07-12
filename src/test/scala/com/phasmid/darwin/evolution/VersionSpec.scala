package com.phasmid.darwin.evolution

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

/**
  * Created by scalaprof on 7/25/16.
  */
class VersionSpec extends FlatSpec with Matchers {

  behavior of "next"
  it should "work" in {
    val v = Version(0, None)
    v.next() should matchPattern { case Success(Version(1, None, false)) => }
  }

  behavior of "subversions"
  it should "work" in {
    val v = Version(0, None)
    val stream: Seq[Version[Int]] = v.subversions take 3
    stream shouldBe Seq(Version(0, Some(Version(0, None))), Version(0, Some(Version(1, None))), Version(0, Some(Version(2, None))))
  }
}
