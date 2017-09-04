/*
 * DARWIN Genetic Algorithms Framework Project.
 * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
 *
 * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
 * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
 *
 *      This file is part of Darwin.
 *
 *      Darwin is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class GeneSpec extends FlatSpec with Matchers {

  /**
    * A diploid gene which extends Gene[Boolean,String]
    *
    * @param location the locus on the chromosome where this gene can be found
    * @param alleles  the two alleles of this (diploid) gene
    */
  case class GeneDiploidString(location: Location, alleles: (Allele[String], Allele[String])) extends Gene[Boolean, String] {
    def apply(p: Boolean): Allele[String] = if (p) alleles._1 else alleles._2

    val name: String = location.name

    override def distinct: Seq[Allele[String]] = Seq(alleles._1)

    override def locus: Locus[String] = UnknownLocus(Location("test", 0, 1))
  }

  "Gene" should "have 2 Alleles" in {
    val location = Location("hox", 0, 1)
    val x = GeneDiploidString(location, (Allele("x"), Allele("y")))
    x(true) shouldBe Allele("x")
  }
}