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

import com.phasmid.laScala.Version
import com.phasmid.laScala.fp.Streamer

/**
  * This module contains several traits and classes which define aspects of an object's
  * appearance and identification.
  */

/**
  * This trait defines the concept of an identifier, with a name which is a String.
  *
  */
trait Identifier {
  /**
    * Provide the name of an object, primarily for rendering/debugging purposes.
    *
    * @return the name
    */
  def name: String

  override def toString: String = name
}

case class IdentifierName(name: String) extends Identifier

/**
  * CONSIDER do we really need this? It's just a convenience
  *
  * Abstract class which implements Identifier but delegates its identification to the value of id.
  *
  * @param id the Identifier
  */
abstract class Identified(id: Identifier) extends Identifier {
  def name: String = id.name
}

/**
  * CONSIDER do we really need this? It's just a convenience
  *
  * Abstract class which implements Identifier but delegates its identification to the value of id.
  *
  * @param id the Identifier
  */
abstract class SelfIdentified(id: Identifier) extends Identified(id) with SelfAuditing {

  import Audit._

  audit()
}

/**
  * This class represents an UID which is based on a (typically) random Long number.
  *
  * @param id a Long which hopefully, is unique.
  */
case class UID(id: Long) {
  // CONSIDER improving this...
  override def toString: String = ("000000000000000" + id.toHexString) takeRight 16
}

/**
  * This class represents a randomly-chosen name based on the current generation (version).
  *
  * @param prefix     the prefix (which tends to identify the type of the object owning this IdentifierStrVerUID).
  * @param generation the generation that this object belongs to (objects which persist throughout do not normally
  *                   use this type of identifier).
  * @param id         an UID
  * @tparam V the underlying Version type of the generation.
  */
case class IdentifierStrVerUID[V](prefix: String, generation: Version[V], id: UID) extends Identifier {
  def name: String = s"$prefix-$generation-$id"
}

/**
  * Trait which defines that there is an UID called id
  */
trait HasId {
  def id: UID
}

/**
  * This class represents a randomly-chosen name.
  *
  * @param prefix the prefix (which tends to identify the type of the object owning this IdentifierStrVerUID).
  * @param id     an UID
  */
case class IdentifierStrUID(prefix: String, id: UID) extends Identifier with HasId {
  def name: String = s"$prefix${UID.sep}$id"
}

object IdentifierStrUID {
  def apply(id: UID, prefix: String): Identifier = apply(prefix, UID(id.id))
}

object UID {
  def apply(id: Identifier): UID = id match {
    case x: HasId => apply(x.id.id)
    case _ => apply(1L) // TODO randomize this
  }

  import scala.language.implicitConversions

  implicit def randomId(ls: Streamer[Long]): UID = UID(ls())

  val sep = ":"
}

