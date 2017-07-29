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

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 8/25/16.
  */
class PluginSpec extends FlatSpec with Matchers {
  behavior of "getPlugin"

  it should "work with PluginManager finding TestPlugin with explicit path" in {
    val pm = PluginManager[EvolvablePlugin](List(new File("."), new File("src/test/resources/plugins/pepperedmoth_2.11-1.0.0-SNAPSHOT.jar")))
    pm.getPlugin("com.phasmid.darwin.plugin.TestPlugin") should matchPattern { case Some(_) => }
    val po = pm.getPlugin("com.phasmid.darwin.plugin.TestPlugin")
    po should matchPattern { case Some(_) => }
    for (p <- po) {
      p.name shouldBe "Test plugin"
      p.version shouldBe "1.0"
      p.init("Hello")
      p.status shouldBe Initialized
      p.start()
      p.status shouldBe Running
      p.stop()
      p.status shouldBe Initialized
      p.destroy()
      p.status shouldBe Undefined
    }
  }

  it should "work with PluginManager finding TestPlugin with implicit path" in {
    val pm = PluginManager[EvolvablePlugin]("src/test/resources/plugins/")
    val mp = pm.getPlugin("MockPlugin")
    mp should matchPattern { case None => }
    val po = pm.getPlugin("com.phasmid.darwin.plugin.TestPlugin")
    po should matchPattern { case Some(_) => }
    for (p <- po) p.name shouldBe "Test plugin"
  }

  it should "return the exact same plugin when re-asked" in {
    val pm = PluginManager[EvolvablePlugin]("src/test/resources/plugins/")
    val po = pm.getPlugin("com.phasmid.darwin.plugin.TestPlugin")
    po should matchPattern { case Some(_) => }
    pm.getPlugin("com.phasmid.darwin.plugin.TestPlugin").get shouldBe po.get
  }

  behavior of "clear and plugins"

  it should "work" in {
    val pm = PluginManager[EvolvablePlugin]("src/test/resources/plugins/")
    pm.pluginMap shouldBe null
    val po = pm.getPlugin("TestPlugin")
    pm.plugins.size shouldBe 1
    pm.clear
    pm.pluginMap shouldBe null
    // Apparently, this will carry on working as before
    for (p <- po) {
      p.name shouldBe "Test Plugin"
      p.init("Hello")
      p.status shouldBe Initialized
      p.start()
      p.stop()
      p.destroy()
    }
  }
}

class MockPlugin extends AbstractPlugin("MockPlugin", "1.0") {

  def doStop: Unit = {
    println("MockPlugin stopping")
  }

  def doStart: Unit = {
    println("MockPlugin starting")
  }

  def doDestroy: Unit = {
    println("MockPlugin being destroyed")
  }

  def doInit: Unit = {
    println(s"MockPlugin initializing with configuration $configuration")
    configuration.foreach(p => println("additional init parameter: $p"))
  }
}