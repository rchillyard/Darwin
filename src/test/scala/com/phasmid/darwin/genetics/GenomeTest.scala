package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Adenine, Cytosine, Guanine, Thymine}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class GenomeTest extends FlatSpec with Matchers {

  "transcriber" should "work" in {
    val transcriber = new Transcriber {
      // XXX this is a simple 1:1 mapping from bases to alleles
      def transcribe[Base](bs: Strand[Base])(locus: Locus): Allele = bs.locate(locus) match
      {
        case Seq(Cytosine) => Allele("C")
        case Seq(Guanine) => Allele("G")
        case Seq(Adenine) => Allele("A")
        case Seq(Thymine) => Allele("T")
      }
    }
    val karyotype = Seq(Chromosome("test", false, Seq(Locus(0,1))))
    val g = Genome("test",karyotype,2,transcriber)
  }

}
