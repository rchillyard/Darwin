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

package com.phasmid.darwin.evolution

/**
  * Defines the concept of a supreme being which has the power to create living
  * objects out of nothing, and of course kill them at will.
  *
  * It's a convenient way to get our evolution simulations started, that is to
  * say when we want to play "God".
  *
  * @author Robin Hillyard
  *
  * Created on Jan 5, 2010 as Theological
  *
  */
trait Theocratic[B, Repr] {

  /**
    * Method to cull all members of an Evolvable so we can make a fresh start.
    * The complementary method to
    * {@link #seedMembers()}.
    */
  def cullMembers(): Repr

  /**
    * Method to seed a this
    * {@link Evolvable} which a certain number of
    * members.
    */
  def seedMembers(size: Int, random: RNG[B]): Repr
}
