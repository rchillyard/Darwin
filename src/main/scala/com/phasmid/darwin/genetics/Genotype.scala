package com.phasmid.darwin.genetics

import com.phasmid.darwin.genetics.dna.Base

/**
  * This class represents a genotype: the genetic material of a particular organism.
  *
  * W is normally a Boolean to distinguish alleles in a diploid arrangement.
  * But if you want to have a triploid arrangement (or any other ploidy) then you might
  * want to use something different for W, such bs Int.
  *
 * @author scalaprof
 */
case class Genotype[P](genome: Genome[P], genes: Seq[Gene[P]])

trait Gene[P] extends (P=>Allele)

case class Allele(name: String) extends Identifier
