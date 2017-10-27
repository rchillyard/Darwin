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

import com.phasmid.laScala.fp.Named
import com.phasmid.laScala.{Prefix, Renderable, RenderableCaseClass}
import org.slf4j.{Logger, LoggerFactory}

import scala.language.implicitConversions

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
  def audit()(implicit auditFunc: String => Audit, isEnabledFunc: Audit => Boolean): Unit = {
    Audit.debug(render())(auditFunc, isEnabledFunc)
  }

}

trait Identifiable extends Auditable with Named {
  /**
    * This method will normally be overridden, especially if the concrete class is a case class.
    *
    * @param indent the indent
    * @param tab    the tabulator
    * @return the rendered String, which will not include any newlines, which is why tab and indent are not passed in.
    */
  override def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = Identifiable.render(this)

}

object Identifiable {
  /**
    * Method to render an object such that the resulting string will not span multiple lines
    *
    * NOTE: it's OK for render to invoke toString but it's never OK for toString to invoke render!!
    *
    * @param x the object to render
    * @return the result of rendering x
    */
  def render(x: Any): String = x match {
    case i: Identifiable => renderName(x, i.name)
    case _ => x.toString
  }

  /**
    * Method to render an object such that the resulting string will not span multiple lines
    *
    * @param x the object to render
    * @param s the identifier for this object
    * @return the result of rendering x
    */
  def renderName(x: Any, s: String): String = {
    val prefix = x match {
      case p: Product => p.productPrefix
      case _ => getClass.getSimpleName
    }
    s"$prefix:$s"
  }

}

import scala.reflect.runtime.universe._

/**
  * This abstract class is the sister of Identifying and is therefore self-auditing.
  *
  * TODO eliminate this class because at present, it contributes nothing useful (in fact, it doesn't work).
  *
  * It defines, for a case class, an object which is not only Identifiable but, on invocation of render,
  * will either render the name only or will render it as a case class.
  *
  * For such a class, it is not necessary (although allowable) to define render.
  *
  * NOTE: if you require the logic of CaseIdentifiable for a class which has a more significant abstract class as its
  * parent, then you should extend that other class and override render in a similar manner to what is shown below.
  *
  * TODO rename this as CaseAuditable
  *
  * @tparam T the underlying type of the case class
  */
abstract class CaseIdentifiable[T: TypeTag] extends SelfAuditing {

  import Audit._

  audit()

  /**
    * TODO this does not appear to work correctly
    *
    * @param indent the number of tabs to indent
    * @param tab    the tab function
    * @return the rendered object
    */
  override def render(indent: Int)(implicit tab: (Int) => Prefix): String = CaseIdentifiable.renderAsCaseClass(this)(indent)
}

object CaseIdentifiable {
  /**
    * Object-method to render the given object as a Case Class object unless nested (indent>0) in which case we use
    *  Identifiable.render
    *
    * @param t      the object to be rendered
    * @param indent the indentation
    * @param tab    the tabulator
    * @tparam T the type of the object to be rendered
    * @return the String
    */
  def renderAsCaseClass[T: TypeTag](t: T)(indent: Int)(implicit tab: (Int) => Prefix): String = {
    if (indent > 0) Identifiable.render(t)
    else RenderableCaseClass(t).render(indent)(tab)
  }

}

/**
  *
  * This trait defines an Auditable object which renders itself simply by invoking toString.
  */
trait Plain extends Auditable {
  /**
    * This method will normally be overridden, especially if the concrete class is a case class.
    *
    * NOTE: it's OK for render to invoke toString but it's never OK for toString to invoke render!!
    *
    * @param indent the indent
    * @param tab    the tabulator
    * @return the rendered String
    */
  def render(indent: Int = 0)(implicit tab: (Int) => Prefix): String = toString
}

trait SelfAuditing extends Auditable

/**
  * This abstract class extends Auditable and automatically invokes audit() on construction.
  * The name Identifying is intended to imply that it self-identifies when constructed.
  */
abstract class Identifying extends SelfAuditing {

  private implicit val logger: Logger = Identifying.logger
  //  import Audit._
  /**
    * This is the default audit function.
    * NOTE that if the logger parameter is null, then no logging is performed, unless internalLog has been reset.
    * ...This (setting logger to null) is another way to turn off logging.
    * NOTE however that if you do turn logger off, the default (implicit) isEnabledFunc will also return false, so that will have to be overridden.
    *
    * @param s the message to be output when auditing
    *          //    * @param logger an (implicit) logger (if null, then this method does tries the last resort: internalLog)
    * @return a Audit (that's to say nothing)
    */
  implicit def auditFunc(s: String): Audit = if (logger != null) Audit(logger.debug(s)) else Audit()

  implicit def isEnabledFunc(x: Audit): Boolean = logger != null && logger.isDebugEnabled
  audit()

  // CONSIDER building a registry for these types of object to warn if a name has already been used.
  // Will need to call finally on the object, to detach it from the registry.
}

object SelfAuditing {
  def unapply(arg: SelfAuditing): Option[Renderable] = Some(arg)
}

object Identifying {
  var logger: Logger = LoggerFactory.getLogger(getClass)

  def setLogger(l: Logger): Unit = {
    if (logger != null) logger = l
    else logger = LoggerFactory.getLogger(getClass)
  }
}

