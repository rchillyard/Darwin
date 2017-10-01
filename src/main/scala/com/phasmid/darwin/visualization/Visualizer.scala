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

package com.phasmid.darwin.visualization

import com.phasmid.darwin.evolution.{Evolvable, Individual}
import com.phasmid.darwin.plugin.Listener
import com.phasmid.darwin.run.{CreateAvatar, KillAvatar}

import scala.collection.mutable

case class Visualizer[T, X](avagen: Avagen[T, X], listener: Listener) {

  def visualize[Z <: Individual[T, X]](e: Evolvable[Z]): Unit = {
    // TODO no, we need to do this when an individual is born
    for (i <- e) createAvatar(i)
  }

  private val hashMap = new mutable.HashMap[String, Avatar]()

  def createAvatar(i: Individual[T, X]): Unit = {
    val avatar = avagen(i)
    hashMap.put(i.name, avatar)
    listener.receive(this, CreateAvatar(avatar))
  }

  def updateAvatar(i: Individual[T, X]): Unit = {
    hashMap.get(i.name) match {
      case Some(a) =>
        listener.receive(this, KillAvatar(a))
        createAvatar(i)
      case None => println(s"logic error re: $i")
    }
  }

  def destroyAvatar(i: Individual[T, X]): Unit = {
    hashMap.get(i.name) match {
      case Some(a) =>
        listener.receive(this, KillAvatar(a))
      case None => println(s"logic error re: $i")
    }
  }
}
