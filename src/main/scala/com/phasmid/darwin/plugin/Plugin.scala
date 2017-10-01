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

/**
  * Fundamental Plugin trait which implements the four basic lifecycle methods together with:
  * the name method;
  * the status method;
  * the "tick" method which is used to synchronize evolutionary events;
  * the addListener method which is used to express interest in events from this plugin;
  *
  *
  * Created by scalaprof on 7/27/17.
  */
trait Plugin {

  /**
    * An identifier.
    *
    * @return the name of this plugin as a String
    */
  def name: String

  /**
    * The version.
    *
    * @return the version of this plugin as a String
    */
  def version: String

  /**
    * Method to retrieve the status of this plugin
    *
    * @return the current status
    */
  def status: Status

  /**
    * Method which must be implemented in order to properly initialize this Plugin
    */
  protected def doInit(): Unit

  /**
    * Method which must be implemented in order to start this Plugin running
    */
  protected def doStart(): Unit

  /**
    * Method which must be implemented in order to stop this Plugin running
    */
  protected def doStop(): Unit

  /**
    * Method which must be implemented in order to destroy this Plugin
    */
  protected def doDestroy(): Unit

  def init(parameters: Any*)

  def start()

  def stop()

  def destroy()
}



sealed trait Status

case object Undefined extends Status

case object Initialized extends Status

case object Running extends Status

case class Error(e: java.lang.Throwable) extends Status

class PluginException(s: String, cause: java.lang.Throwable) extends Exception(s, cause)

abstract class AbstractPlugin(val name: String, val version: String) extends Plugin {
  final def status: Status = status_

  /**
    * Method to initialize this Plugin. The actual initialization is implemented by doInit.
    *
    * @param parameters the configuration for this Plugin: a set of parameters of arbitrary type
    */
  final def init(parameters: Any*): Unit = {
    if (marker == null) {
      marker = ""
      if (configuration_ == null)
        configuration_ = parameters
      transition(Undefined, doInit, "initialized", Initialized)
    }
    else throw new RuntimeException("cannot reinitialize used Plugin")
  }

  /**
    * Method to start this Plugin running. The actual code to be run is defined by doStart.
    */
  final def start(): Unit = {
    transition(Initialized, doStart, "started", Running)
  }

  /**
    * Method to stop this Plugin running. The actual code to be run is defined by doStop.
    */
  final def stop(): Unit = {
    transition(Running, doStop, "stopped", Initialized)
  }

  /**
    * Method to destroy this Plugin. The actual code to be run is defined by doDestroy.
    */
  final def destroy(): Unit = {
    transition(Initialized, doDestroy, "destroyed", Undefined)
    configuration_ = null
  }

  protected def configuration: Seq[Any] = configuration_

  /**
    * The configuration of this plugin
    */
  private[plugin] var configuration_ : Seq[Any] = _

  /**
    * The current status of this plugin
    */
  private[plugin] var status_ : Status = Undefined

  private var marker: Any = _

  /**
    * Private method to implement a state transition of this Plugin.
    *
    * @param requiredStatus the status required in order to perform the transition
    * @param f              the function to invoke once the status has been established
    * @param op             the name of this operation (for exception text only)
    * @param newStatus      the status that the plugin will assume after the transition
    */
  private final def transition(requiredStatus: Status, f: () => Unit, op: String, newStatus: Status): Unit = {
    status match {
      case `requiredStatus` =>
        try {
          f()
          status_ = newStatus
        }
        catch {
          case e: Exception => status_ = Error(e)
        }
      case Initialized => throw new PluginException(s"plugin $name $version cannot be $op because it is in state: $status", null)
      case Running => throw new PluginException(s"plugin $name $version cannot be $op because it is in state: $status", null)
      case Undefined => throw new PluginException(s"plugin $name $version cannot be $op because it is in state: $status", null)
      case Error(e) => throw new PluginException(s"plugin $name $version cannot be $op because it is in an error state: ", e)
    }
  }


}

object Plugin {
}


