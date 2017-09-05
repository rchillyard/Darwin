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

package com.phasmid.darwin.plugin

import java.time.{Duration, LocalDateTime}
import java.util.concurrent.TimeUnit

import com.phasmid.darwin.run.Darwin
import com.phasmid.laScala.fp.Args
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

/**
  * Created by scalaprof on 7/26/17.
  */
class DarwinSpec extends FlatSpec with Matchers {

  behavior of "Darwin"
  it should "work from plugins directory" in {
    import scala.concurrent.ExecutionContext.Implicits.global
    val a1 = Args("Test Harness", "PT0.1S", "src/test/resources/plugins/", "5")
    val (name: String, a2) = a1.get(classOf[String]).get
    val (interval: String, a3) = a2.get(classOf[String]).get
    val (plugins: String, a4) = a3.get(classOf[String]).get
    val (max: String, a5) = a4.get(classOf[String]).get
    a5.isEmpty shouldBe true
    val darwin = Darwin(name, Duration.parse(interval), Try(max.toLong).toOption, plugins)
    darwin.interval shouldBe Duration.ofMillis(100L)
    darwin.max shouldBe Some(5L)
    darwin.run()
  }
  it should "work by adding plugin directly" in {
    import scala.concurrent.ExecutionContext.Implicits.global
    val a1 = Args("Test Harness", "PT0.1S", "src/test/resources/", "2")
    val (name: String, a2) = a1.get(classOf[String]).get
    val (interval: String, a3) = a2.get(classOf[String]).get
    val (plugins: String, a4) = a3.get(classOf[String]).get
    val (max: String, a5) = a4.get(classOf[String]).get
    a5.isEmpty shouldBe true
    // TODO understand why changing the initialDelay affects the value of plugin.count
    val darwin = Darwin(name, Duration.parse(interval), Try(max.toLong).toOption, plugins, FiniteDuration(1, TimeUnit.SECONDS))
    val plugin = new TestPlugin
    darwin.addPlugin(plugin)
    darwin.interval shouldBe Duration.ofMillis(100L)
    darwin.max shouldBe Some(2L)
    darwin.run()
    plugin.count shouldBe 1
  }

  class TestPlugin extends AbstractPlugin("Darwin Test plugin", "1.0") with EvolvablePlugin {

    var count = 0

    protected def doInit(): Unit = println(s"$name initialized")

    protected def doStart(): Unit = println(s"$name started")

    protected def doStop(): Unit = println(s"$name stopped")

    protected def doDestroy(): Unit = println(s"$name destroyed")

    def actionDuration: Duration = Duration.ofMillis(200L)

    def act(generation: Long): Unit = {
      count += 1
      for (l <- listeners) l.receive(this, s"generation $generation at ${LocalDateTime.now()}")
    }

    def addListener(x: Listener): Unit = listeners += x

    val listeners: mutable.MutableList[Listener] = mutable.MutableList[Listener]()
  }

}