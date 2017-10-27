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

/**
  * NamedFunction class. Could equally be called NamedFunction1
  *
  * TODO move this module to LaScala
  * TODO add other aritys
  *
  * @param name the name of this function
  * @param f    the function itself
  * @tparam T the input type
  * @tparam R the result type
  */
class NamedFunction[-T, +R](val name: String, val f: T => R) extends (T => R) {
  override def apply(t: T) = f(t)

  override def toString: String = NamedFunction.toString(name, 1)

  override def compose[A](g: (A) => T): A => R = g match {
    case NamedFunction(w, gf) => new NamedFunction(s"$name&&&$w", f.compose(gf))
    case _ => new NamedFunction(s"$name&&&$g", f.compose(g))
  }

  override def andThen[A](g: (R) => A): T => A = g match {
    case NamedFunction(w, gf) => new NamedFunction(s"$name&&&$w", f.andThen(gf))
    case _ => new NamedFunction(s"$name&&&$g", f.andThen(g))
  }
}

/**
  * NamedFunction0 class.
  *
  * @param name the name of this function
  * @param f    the function itself
  * @tparam R the result type
  */
class NamedFunction0[+R](val name: String, val f: () => R) extends (() => R) with Identifier {
  override def apply() = f()

  override def toString: String = NamedFunction.toString(name, 0)
}

/**
  * NamedFunction2 class.
  *
  * @param name the name of this function
  * @param f    the function itself
  * @tparam T1 the input type
  * @tparam T2 the input type
  * @tparam R  the result type
  */
class NamedFunction2[-T1, -T2, +R](val name: String, val f: (T1, T2) => R) extends ((T1, T2) => R) with Identifier {
  override def apply(t1: T1, t2: T2) = f(t1, t2)

  override def toString: String = NamedFunction.toString(name, 2)

  override def curried = new NamedFunction(s"$name!!!", f.curried)

  override def tupled = new NamedFunction(s"$name###", f.tupled)
}

/**
  * NamedFunction3 class.
  *
  * @param name the name of this function
  * @param f    the function itself
  * @tparam T1 the input type
  * @tparam T2 the input type
  * @tparam T3 the input type
  * @tparam R  the result type
  */
class NamedFunction3[-T1, -T2, -T3, +R](val name: String, val f: (T1, T2, T3) => R) extends ((T1, T2, T3) => R) with Identifier {
  override def apply(t1: T1, t2: T2, t3: T3) = f(t1, t2, t3)

  override def toString: String = NamedFunction.toString(name, 3)

  override def curried = new NamedFunction(s"$name!!!", f.curried)

  override def tupled = new NamedFunction(s"$name###", f.tupled)
}

object NamedFunction {
  def toString(name: String, arity: Int): String = s"<function$arity: $name>"

  def unapply[T, R](arg: NamedFunction[T, R]): Option[(String, T => R)] = Some(arg.name, arg.f)
}

object NamedFunction0 {
  def unapply[R](arg: NamedFunction0[R]): Option[(String, () => R)] = Some(arg.name, arg.f)
}

object NamedFunction2 {
  def unapply[T1, T2, R](arg: NamedFunction2[T1, T2, R]): Option[(String, (T1, T2) => R)] = Some(arg.name, arg.f)
}

object NamedFunction3 {
  def unapply[T1, T2, T3, R](arg: NamedFunction3[T1, T2, T3, R]): Option[(String, (T1, T2, T3) => R)] = Some(arg.name, arg.f)
}
