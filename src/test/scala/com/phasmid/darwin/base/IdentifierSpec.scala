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

package com.phasmid.darwin.base

import org.scalatest.{FlatSpec, Matchers}

class IdentifierSpec extends FlatSpec with Matchers {

  behavior of "Identifier"
  it should "yield name" in {
    val id = new Identifier() {
      def name = "Hello"
    }
    id.name shouldBe "Hello"
  }

  behavior of "UID"
  it should "work" in {
    val n = 128L
    val x = UID(n)
    x.id shouldBe n
  }

  it should "be copyable" in {
    val n = 128L
    val x = UID(n)
    val y = IdentifierStrUID("test", x)
    val z = UID(y)
    z.id shouldBe n
  }

  behavior of "IdentifierStringUID"
  it should "work" in {
    val name = "test"
    val n = 128L
    val x = IdentifierStrUID(name, UID(n))
    x.name shouldBe s"$name${UID.sep}0000000000000080"
    x.id.id shouldBe n
  }
}
