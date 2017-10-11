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

import com.phasmid.laScala.fp.FP
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.Logger

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.{Failure, Success}

/**
  * This is basically a clone of SpySpec (from LaScala) but with adjustments, e.g. "Spy" -> "Audit" in all its forms.
  */
//noinspection TypeAnnotation
class AuditSpec extends FlatSpec with Matchers {

  behavior of "Audit.audit"
  it should "work with implicit (logger) (with default logger) audit func" in {
    import Audit._
    Audit.auditing = true
    (for (i <- 1 to 2) yield Audit.audit("i", i)) shouldBe List(1, 2)
    // you should see log messages written to console (assuming your logging level, i.e. logback-test.xml, is set to DEBUG)
  }
  it should "work with implicit (logger) audit func (with logger for this class) on the map2 function" in {
    implicit val logger = Audit.getLogger(getClass)
    Audit.auditing = true
    val x = Some(1)
    FP.map2(x, x)(_ + _)
    (for (i <- 1 to 2) yield Audit.audit("i", i)) shouldBe List(1, 2)
    // you should see log messages written to console (assuming your logging level, i.e. logback-test.xml, is set to DEBUG)
  }
  it should "work with implicit (logger) audit func but with custom logger" in {
    Audit.auditing = true
    implicit val logger = org.slf4j.LoggerFactory.getLogger("myLogger")
    (for (i <- 1 to 2) yield Audit.audit("i", i)) shouldBe List(1, 2)
    // you should see log messages written to console (assuming your logging level, i.e. logback-test.xml, is set to DEBUG)
  }
  it should "work with explicit audit func" in {
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    val is = for (i <- 1 to 2) yield Audit.audit("i", i)
    is shouldBe List(1, 2)
    auditMessage shouldBe "explicit audit: i: 1\nexplicit audit: i: 2\n"
  }
  it should "work with explicit audit func when exception is thrown" in {
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    try Audit.audit("division by zero", 1 / 0)
    catch { case _: Exception => }
    auditMessage shouldBe "explicit audit: division by zero: <<Exception thrown: / by zero>>\n"
  }
  it should "work with explicit custom println audit func" in {
    import Audit._
    implicit def auditFunc(s: String): Audit = Audit(println(s))

    Audit.auditing = true
    val is = for (i <- 1 to 2) yield Audit.audit("myAudit: i", i)
    is shouldBe List(1, 2)
    // you should see messages written to console with "mySPy" prefix
  }
  it should "work with explicit provided println audit func" in {
    import Audit._
    implicit val auditFunc = Audit.getPrintlnAuditFunc()
    Audit.auditing = true
    val is = for (i <- 1 to 2) yield Audit.audit("i", i)
    is shouldBe List(1, 2)
    // you should see messages written to console with "audit" prefix
  }
  it should "not evaluate the argument (noAudit)" in {
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""
    var auditAuditMessage: String = ""

    def f(s: => String): String = {auditAuditMessage = s; s}

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    val is = Audit.noAudit(for (i <- 1 to 2) yield Audit.audit(f("i"), i))
    is shouldBe List(1, 2)
    auditMessage shouldBe ""
    auditAuditMessage shouldBe ""
  }
  it should "not expand the message when auditing is off (global)" in {
    import Audit._
    Audit.auditing = false
    var auditMessage: String = ""
    var auditAuditMessage: String = ""

    def f(s: => String): String = {auditAuditMessage = s; s}

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    val is = for (i <- 1 to 2) yield Audit.audit(f("i"), i)
    is shouldBe List(1, 2)
    auditMessage shouldBe ""
    auditAuditMessage shouldBe ""
  }
  it should "not expand the message when auditing is off (local)" in {
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""
    var auditAuditMessage: String = ""

    def f(s: => String): String = {auditAuditMessage = s; s}

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    val is = for (i <- 1 to 2) yield Audit.audit(f("i"), i, b = false)
    is shouldBe List(1, 2)
    auditMessage shouldBe ""
    auditAuditMessage shouldBe ""
  }
  it should "work with {}-style message" in {
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"Hello: $s\n")

    val is = for (i <- 1 to 2) yield Audit.audit("{} is the value for i", i)
    is shouldBe List(1, 2)
    auditMessage shouldBe "Hello: 1 is the value for i\nHello: 2 is the value for i\n"
  }
  it should "use internalLog when logger is set to null" in {
    //    import Audit._
    Audit.auditing = true
    implicit val logger: Logger = null

    implicit def isEnabledFunc(x: Audit)(implicit logger: Logger): Boolean = true

    val sb = new StringBuilder
    Audit.internalLog = { s => sb.append(s) }
    Audit.audit("hello", "world")
    sb.toString shouldBe "audit: hello: world"
  }
  it should "work with a Success" in {
    implicit val logger = Audit.getLogger(getClass)
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"Hello: $s\n")

    Audit.audit("success", Success(1))
    auditMessage shouldBe "Hello: success: Success: 1\n"
  }
  it should "work with a failure" in {
    implicit val logger = Audit.getLogger(getClass)
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"Hello: $s\n")

    Audit.audit("failure", Failure(new Exception("junk")))
    auditMessage shouldBe "Hello: failure: Failure(java.lang.Exception: junk)\n"
  }
  it should "work with a Future" in {
    implicit val logger = Audit.getLogger(getClass)
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"Hello: $s\n")
    import scala.concurrent.ExecutionContext.Implicits.global
    Audit.audit("success", Future(1))
    auditMessage shouldBe "Hello: success: to be provided in the future\n"
  }
  it should "work with a Stream" in {
    implicit val logger = Audit.getLogger(getClass)
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"Hello: $s\n")

    Audit.audit("Stream", Stream(1, 2, 3, 4, 5, 6))
    auditMessage shouldBe "Hello: Stream: [Stream showing at most 5 items] List(1, 2, 3, 4, 5)\n"
  }
  behavior of "Audit.log"
  it should "work with explicit audit func" in {
    // NOTE: we really do need import Audit._ here
    import Audit._
    Audit.auditing = true
    var auditMessage: String = ""

    implicit def auditFunc(s: String): Audit = Audit(auditMessage += s"explicit audit: $s\n")

    Audit.debug("my log message")
    auditMessage shouldBe "explicit audit: my log message: ()\n"
  }
}
