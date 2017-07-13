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

package com.phasmid.darwin.genetics.dna

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */

class DNASpec extends FlatSpec with Matchers {

  "base names" should "be correct" in {
    Cytosine.name shouldBe "C"
    Guanine.name shouldBe "G"
    Thymine.name shouldBe "T"
    Adenine.name shouldBe "A"
  }
  "bases" should "derive from String" in {
    Base('C') shouldBe Cytosine
    Base('T') shouldBe Thymine
    Base('G') shouldBe Guanine
    Base('A') shouldBe Adenine
    Base('X') shouldBe Invalid('X')
  }
  "base pairs" should "be correct" in {
    Cytosine.pair shouldBe Guanine
    Thymine.pair shouldBe Adenine
  }

}