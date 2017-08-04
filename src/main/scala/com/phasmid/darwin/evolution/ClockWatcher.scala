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

import java.time.Duration

/**
  * Created by scalaprof on 7/26/17.
  */
trait ClockWatcher {

  /**
    * Method to determine the duration between successive actions (generations) of this ClockWatcher.
    *
    * @return the duration between successive actions
    */
  def actionDuration: Duration

  /**
    * Method to react to a new tick of the clock.
    *
    * @param sequence the sequence # of this tick
    * @param interval the time between successive ticks
    */
  def onTick(sequence: Long, interval: Duration): Unit = {
    val elapsed: Duration = interval.multipliedBy(sequence)
    val (x, y) = BigInt(elapsed.toNanos) /% actionDuration.toNanos
    if (y == 0) act(x.longValue())
  }

  /**
    * Method to react to a new generation.
    *
    * @param x Generation (action) sequence number
    */
  def act(x: Long): Unit

}
