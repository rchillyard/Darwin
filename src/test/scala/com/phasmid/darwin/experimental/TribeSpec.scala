///*
// * DARWIN Genetic Algorithms Framework Project.
// * Copyright (c) 2003, 2005, 2007, 2009, 2011, 2016, 2017. Phasmid Software
// *
// * Originally, developed in Java by Rubecula Software, LLC and hosted by SourceForge.
// * Converted to Scala by Phasmid Software and hosted by github at https://github.com/rchillyard/Darwin
// *
// *      This file is part of Darwin.
// *
// *      Darwin is free software: you can redistribute it and/or modify
// *      it under the terms of the GNU General Public License as published by
// *      the Free Software Foundation, either version 3 of the License, or
// *      (at your option) any later version.
// *
// *      This program is distributed in the hope that it will be useful,
// *      but WITHOUT ANY WARRANTY; without even the implied warranty of
// *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *      GNU General Public License for more details.
// *
// *      You should have received a copy of the GNU General Public License
// *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.phasmid.darwin.experimental
//
//import com.phasmid.darwin.base.IdentifierName
//import com.phasmid.darwin.eco._
//import com.phasmid.darwin.genetics._
//import com.phasmid.darwin.genetics.dna.Base
//import com.phasmid.darwin.plugin.Listener
//import com.phasmid.darwin.run.Species
//import com.phasmid.darwin.visualization.{Avagen, Avatar, Visualizer}
//import com.phasmid.laScala.Version
//import com.phasmid.laScala.fp.{NamedFunction3, Streamer}
//import org.scalatest.{FlatSpec, Inside, Matchers}
//
//import scala.util.{Failure, Success, Try}
//
///**
//  * Created by scalaprof on 7/25/16.
//  */
//class TribeSpec extends FlatSpec with Matchers with Inside {
//
//  behavior of "Tribe"
//
//  trait Simple[A] extends Genetic[A] {
//    override def isFemale(a: A): Boolean = a.hashCode() %2 == 0
//  }
//
//  implicit object SimpleString extends Simple[String] {
//    override def progeny(m: String, f: String): String = s"$f-$m"
//  }
//  it should "produce progeny" in {
//    val xs = List[String]("a","b","c","d")
//    val tribe = Tribe(xs)
//    tribe.progeny shouldBe List("b-a", "d-c")
//  }
//
//}
