/*
 * Darwin Evolutionary Computation Project
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 */

package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import com.phasmid.laScala.LongRNG
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class ReplicatorSpec extends FlatSpec with Matchers {

  "PerfectReplicator" should "work perfectly" in {
    val replicator = PerfectReplicator[Base]()
    val bs = Sequence(Seq(Cytosine, Guanine))
    replicator.replicate(bs) shouldBe bs
  }
  "ImperfectReplicator" should "work imperfectly" in {
    val replicator = ImperfectReplicator[Base](2, LongRNG(0L).map(_.toInt))
    val bs = Sequence(Seq(Cytosine, Guanine, Adenine, Adenine, Thymine, Cytosine))
    replicator.replicate(bs) shouldBe Sequence[Base]("CGGATC")
  }
}
