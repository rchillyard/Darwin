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

import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass}

/**
  * This module contains several traits and classes which define aspects of an object's
  * appearance and identification.
  */

/**
  * This trait defines the concept of something that can be audited in a form which is readable (because Auditable
  * extends Renderable).
  */
trait Auditable extends Renderable {

  /** previously extended also from LazyLogging **/

  /**
    * Render this object (top-level) and log it to the Audit log.
    */
  def audit(): Unit = {
    import com.phasmid.darwin.base.Audit._

    Audit.debug(this.render())
  }
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

/**
  * This trait defines, for a case class, an object which is not only Identifiable but, on invocation of render,
  * will either render the name only or will render it as a case class (that's to say as a tuple).
  *
  * For such a class, it is not necessary (although allowable) to define render.
  *
  * @tparam T the underlying type of the case class
  */
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

/**
  * This trait defines an Auditable object which renders itself simply by invoking toString.
  */
trait Plain extends Auditable {
  // NOTE: it's OK for render to invoke toString but it's never OK for toString to invoke render!!
  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = toString
}

/**
  * This abstract class extends Auditable and automatically invokes audit() on construction.
  * The name Identifying is intended to imply that it self-identifies when constructed.
  */
abstract class Identifying extends Auditable {
  audit()
}

object Identifying {
  def unapply(arg: Identifying): Option[Renderable] = Some(arg)
}


