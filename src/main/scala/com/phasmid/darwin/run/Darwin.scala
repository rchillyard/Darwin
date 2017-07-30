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

package com.phasmid.darwin.run

import java.net.URI
import java.time.{Duration, LocalDateTime}
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Scheduler}
import com.phasmid.darwin.plugin._
import com.phasmid.laScala.fp.Args
import org.slf4j.LoggerFactory

import scala.collection.{immutable, mutable}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by scalaprof on 7/27/17.
  */
case class Darwin(name: String, interval: Duration, max: Option[Long], plugins: String)(implicit ec: ExecutionContext) extends Listener {
  private val logger = LoggerFactory.getLogger(getClass)
  private val pm = PluginManager[EvolvablePlugin]("src/test/resources/plugins/")
  private val ps: mutable.MutableList[EvolvablePlugin] = mutable.MutableList()
  for (k <- pm.plugins.toList; p <- pm.getPlugin(k)) addPlugin(p)
  private var count: Option[Long] = max
  def ok: Boolean =  (count getOrElse 1L) > 0

  val myRunnable = new Runnable {
    override def run(): Unit = {
      ps foreach (_.onTick(LocalDateTime.now()))
      count = for (x <- count) yield x-1
    }
  }

  def addPlugin(p: EvolvablePlugin) = {
    ps.+=(p)
    p.addListener(this)
  }

  def run(): Unit = {
    implicit def toFiniteDuration(d: java.time.Duration): FiniteDuration = FiniteDuration(d.toNanos,TimeUnit.NANOSECONDS)

    val actorSystem = ActorSystem.create(s"Darwin-${name replace(' ', '_')}")
    actorSystem.scheduler.schedule(FiniteDuration(2,TimeUnit.SECONDS),interval,myRunnable)
    while (ok) Thread.sleep(100)
    logger.info(s"Darwin $name exiting")
    actorSystem.terminate()
  }

  def receive(sender: AnyRef, msg: Any): Unit = {
    logger.debug(s"$name received $msg")
    // TODO we should forward this message to all plugins except the source plugin
  }
}

object Darwin extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  val a1 = Args(args)
  val (name: String, a2) = a1.get(classOf[String]).get
  val (interval: String, a3) = a2.get(classOf[String]).get
  val (plugins: String, a4) = a3.get(classOf[String]).get
  val (max: String, a5) = a4.get(classOf[String]).get
  Darwin(name, Duration.parse(interval), Try(max.toLong).toOption, plugins).run()
}