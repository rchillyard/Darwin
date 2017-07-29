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

import java.util

import com.phasmid.darwin.plugin.Listener

/**
  * Created by scalaprof on 7/26/17.
  */
trait Evolver {
  /**
    * Add an evolvable object which undergoes a new generation once every tick
    * of the clock.
    *
    * @param evolvable
    * an { @link Evolvable} object, typically a { @link Taxon}.
    */
  def addEvolvable(evolvable: Evolvable[_]): Unit

  /**
    * Add an evolvable object which undergoes a new generation once every
    * <code>ticks</code> ticks of the clock.
    *
    * @param evolvable
    * an { @link Evolvable} object, typically a { @link Taxon}.
    * @param ticks
    * the number of ticks of the clock per generation.
    */
  def addEvolvable(evolvable: Evolvable[_], ticks: Int): Unit

  /**
    * Add a listener to the evolution process.
    *
    * @param listener
    * @return true if the listener was added
    */
  def addListener(listener: Listener): Boolean

  /**
    * Method which is called before all user-interface components get
    * destroyed. When an
    * {@link EvolutionaryApplet} is employed as the
    * user-interface, this method is called ny the
    * {@link Applet#stop()}
    * method.
    */
  def cleanup(): Unit

  /**
    * @return the clockWatcher
    */
  def getClockWatcher: ClockWatcher

  /**
    * @return the set of { @link Evolvable} objects.
    */
  def getEvolvableKeys: util.Set[Evolvable[_]]

  /**
    * Method which is called after all user-interface issues have been dealt
    * with. When an {@link EvolutionaryApplet} is employed as the
    * user-interface, this method is called by the {@link Applet#start()}
    * method.
    */
  def init(): Unit

  /**
    * Increment the clock by one tick, firing new generations as appropriate.
    *
    * @return true if there is more evolution to do.
    * @throws EvolutionException under some cicumstances
    */
  @throws[EvolutionException]
  def next: Boolean

  /**
    * @param evolvable an evolvable
    */
  def removeEvolvable(evolvable: Evolvable[_]): Unit

  /**
    * Seed the currently registered evolvables by calling
    * {@link Evolvable#seedMembers()} on each one.
    */
  def seedEvolvables(): Unit

  /**
    * @param clockWatcher
    * the clockWatcher to set
    */
  def setClockWatcher(clockWatcher: ClockWatcher): Unit

  /**
    * //    * @param listener
    *
    * @return true if successful
    */
  //  def addVisualizableListener(listener: VisualizableListener): Boolean


}
