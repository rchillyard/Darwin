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

import com.phasmid.laScala.fp.Streamer
import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass, Version}


trait Identifier {
  /**
    * Provide the name of an object, primarily for rendering/debugging purposes.
    *
    * @return the name
    */
  def name: String

  override def toString: String = name
}

abstract class Identified(id: Identifier) extends Identifier {
  def name: String = id.name
}

trait Auditable extends Renderable /** with LazyLogging **/ {
  import Audit._
  def audit() : Unit = {
    Audit.log(this.render())
  }
}

abstract class Identifying extends Auditable {
  audit()
}

object Identifying {
  def unapply(arg: Identifying): Option[Renderable] = Some(arg)
}

trait Identifiable extends Auditable with Identifier {
  /**
    * This method will normally be overridden, especially if the concrete class is a case class.
    *
    * @param indent the indent
    * @param tab    the tabulator
    * @return the rendered String
    */
  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = {
    val prefix = this match {
      case p: Product => p.productPrefix
      case _ => getClass.getSimpleName
    }
    s"$prefix:$name"
  }
}

trait CaseIdentifiable[T] extends Identifiable {

  import scala.reflect.runtime.universe._

  def render(indent: Int)(implicit tab: (Int) => Prefix, typeTag: TypeTag[T]): String =
    this match {
      // If we have already rendered this via audit mechanism, then we use super.render
      case Identifying(_) => super.render(indent)(tab)
      case _ =>
        // Otherwise, if we this object is nested within another, we use super.render
        if (indent > 0) super.render(indent)(tab)
        else RenderableCaseClass(this.asInstanceOf[T]).render(indent)(tab)
    }

}

trait Plain extends Auditable {
  // NOTE: it's OK for render to invoke toString but it's never OK for toString to invoke render!!
  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = toString
}

case class RandomName[V](prefix: String, generation: Version[V], id: Id) extends Identifier {
  def name: String = s"$prefix-$generation-$id"
}

object RandomName {
  //  implicit def randomName[V](r: Random[Long], prefix: String, generation: Version[V]): RandomName[V] = apply(prefix, generation, r())
}

case class Id(id: Long) {
  // CONSIDER improving this...
  override def toString: String = ("000000000000000" + id.toHexString) takeRight 16
}

object Id {

  import scala.language.implicitConversions

  implicit def randomId(ls: Streamer[Long]): Id = Id(ls())
}