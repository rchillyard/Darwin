package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class TranscribeSpec extends FlatSpec with Matchers {

  "apply" should "work with Base objects" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Base,String]{bs => Allele(bs.head.toString)}
    val hox = Location("hox", 0, 1) // C or A
    val bs = Sequence(Seq(Cytosine,Guanine))
    val allele = transcriber(bs,hox)
    allele shouldBe Some(Allele("C"))
  }
  it should "work with Int objects" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Int,String]{bs => Allele(bs.head.toString)}
    val hox = Location("hox", 0, 1)
    val bs = Sequence(Seq(1,2))
    val allele = transcriber(bs,hox)
    allele shouldBe Some(Allele("1"))
  }
  it should "work with Int objects and Int alleles" in {
    // XXX this is a very simple 1:1 mapping from bases to alleles
    val transcriber = PlainTranscriber[Int,Int]{bs => Allele(bs.head)}
    val hox = Location("hox", 0, 1)
    val bs = Sequence(Seq(1,2))
    val allele = transcriber(bs,hox)
    allele shouldBe Some(Allele(1))
  }
}
