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

import org.clapper.classutil.{ClassFinder, ClassInfo}
import org.slf4j.LoggerFactory

import scala.reflect.ClassTag
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

/**
  * PluginManager
  *
  * Created by scalaprof on 8/25/16.
  *
  * @param list the list of Files to search for plugins
  * @param max  (defaults to None) if provided, specifies the maximum number of plugins we wish to find.
  * @tparam P the type of Plugin we wish to manage. P must extend Plugin.
  *           NOTE: in order for a plugin of type P to be recognized,
  *           it must be defined such that the first "with" trait is the same as P
  *           (if there is no "with", then the class must directly extend P).
  */
case class PluginManager[P <: Plugin : ClassTag](list: List[File], max: Option[Int] = None) {

  private val pt: ClassTag[P] = implicitly[ClassTag[P]]
  private val pc: Class[P] = pt.runtimeClass.asInstanceOf[Class[P]]
  private val pn = pc.getCanonicalName

  private val logger = LoggerFactory.getLogger(getClass)

  logger.info(s"PluginManager for plugins of type: $pn")

  // NOTE that we are getting the class loader which loaded this class.
  val classLoader = new URLClassLoader(list map { f => f.toURI.toURL }, getClass.getClassLoader)

  def getPlugin(name: String): Option[P] = {
    ensure()
    logger.info(s"Fetching plugin $name.")
    pluginMap.get(name)
  }

  def plugins: Iterator[String] = {ensure(); pluginMap.keys.iterator}

  def clear(): Unit = {
    pluginMap = null
  }

  private[plugin] var pluginMap: Map[String, P] = _

  private def init() {
    logger.debug(s"init PluginManager with list: $list")
    // NOTE: this ClassFinder is very slow! Probably could do better by implementing my own
    val cs: Stream[ClassInfo] = ClassFinder(list).getClasses.filter(_.implements(pn))
    val cs_ = max match {
      case Some(n) => cs.take(n).toList
      case _ => cs.toList
    }
    logger.debug(s"found ${cs_.size} classes which implement $pn")
    val tuples: Seq[(String, P)] = for (info <- cs_; n = info.name; _ = logger.debug(s"registering plugin: $n found in ${info.location}")) yield n -> classLoader.loadClass(n).newInstance().asInstanceOf[P]
    pluginMap = tuples.toMap
    for (k <- pluginMap.keys) logger.info(s"found plugin $k with name: ${pluginMap(k).name}")
  }

  private def ensure(): Unit = {
    if (pluginMap == null) init()
  }
}

object PluginManager {
  def apply[P <: Plugin : ClassTag](directory: String, max: Option[Int]): PluginManager[P] = apply[P](getPluginFiles(directory), max)

  def apply[P <: Plugin : ClassTag](directory: String): PluginManager[P] = apply(directory, None)

  private def getPluginFiles(directory: String): List[File] = getPluginFiles(new File(directory))

  private def getPluginFiles(d: File): List[File] = if (d.exists && d.isDirectory)
    d.listFiles.filter(_.isFile).toList
  else
    List[File]()
}