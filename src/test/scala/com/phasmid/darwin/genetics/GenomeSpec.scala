package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.{Adenine, Cytosine, Guanine, Thymine}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class GenomeSpec extends FlatSpec with Matchers {

  "Genome transcriber" should "work" in {
    val transcriber = new Transcriber {
      // XXX this is a simple 1:1 mapping from bases to alleles
      def transcribe[Base](bs: Strand[Base])(locus: Locus): Allele = bs.locate(locus) match
      {
        case Seq(Cytosine) => Allele("C")
        case Seq(Guanine) => Allele("G")
        case Seq(Adenine) => Allele("A")
        case Seq(Thymine) => Allele("T")
        case x => throw new GeneticsException(s"cannot transcribe $x")
      }
    }
    implicit val bools = Seq(false,true)
    val karyotype = Seq(Chromosome("test", isSex = false, Seq(Locus("hox",0,1))))
    val g = Genome("test",karyotype,true,transcriber)
    val bsss = Seq(Seq(Strand(Seq(Cytosine,Guanine)),Strand(Seq(Adenine,Guanine))))
    val gt: Genotype[Boolean] = g.transcribe(bsss)
  }

  "Transcriber transcriber" should "work" in {
    val transcriber = new Transcriber {
      // XXX this is a simple 1:1 mapping from bases to alleles
      def transcribe[Base](bs: Strand[Base])(locus: Locus): Allele = bs.locate(locus) match
      {
        case Seq(Cytosine) => Allele("C")
        case Seq(Guanine) => Allele("G")
        case Seq(Adenine) => Allele("A")
        case Seq(Thymine) => Allele("T")
        case x => throw new GeneticsException(s"cannot transcribe $x")
      }
    }
    val hox = Locus("hox", 0, 1)
    val bs = Strand(Seq(Cytosine,Guanine))
    val allele = transcriber.transcribe(bs)(hox)
  }

}
