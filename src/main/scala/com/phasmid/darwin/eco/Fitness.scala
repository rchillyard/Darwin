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

package com.phasmid.darwin.eco

/**
  * Class to define the concept of Fitness.
  * Note that we do not make it a case class because we want to distinguish between creating new
  * instances of Fitness with/without checking the requirement.
  *
  * Fitness is a measure of the viability of an organism's phenotype adapting to an environment.
  * It's a Double value and should be in the range 0..1
  *
  * If a Fitness value is f, then the likelihood of an organism surviving one generation is f,
  * assuming that there are no other applicable fitness values other than 1.
  *
  * Thus, for an environment with many applicable fitness values, the overall likelihood of surviving one generation
  * is P(f1, f2, ..., fN) where P stands for the product.
  *
  * NOTE: this behavior is encoded in the & operator.
  *
  * Created by scalaprof on 5/5/16.
  */
class Fitness private(val x: Double) extends (() => Double) {

  /**
    * Method to yield the underlying value of this Fitness as a Double
    *
    * @return the value of x
    */
  def apply(): Double = x

  /**
    * Method to define the behavior of combining two Fitness values.
    *
    * @param other the other fitness value
    * @return a new instance of Fitness with a value which is the product of both x values
    */
  def &(other: Fitness): Fitness = new Fitness(x * other.x)

  /**
    * Method to define the behavior of negating (complementing) this Fitness.
    * NOTE: this is not set up for unary application so you will have to use the form f.-
    *
    * @return the complementary Fitness
    */
  def - : Fitness = new Fitness(1 - x)

  /**
    * Method to define the behavior of reducing this Fitness by a factor.
    *
    * @param y the reduction factor
    * @return the reduced Fitness
    */
  def /(y: Double): Fitness = {
    require(y >= 1, s"the reduction factor in / method was not at least 1: $y")
    new Fitness(x / y)
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case Fitness(y) => x == y
    case _ => false
  }

  override def hashCode(): Int = x.hashCode()

  override def toString(): String = s"Fitness($x)"

}

case class Viability(fs: Seq[Fitness]) extends (() => Fitness) {

  /**
    * Method to add a Fitness to this Viability
    *
    * @param f the Fitness value to add
    * @return a new Viability with f added
    */
  def +(f: Fitness) = Viability(fs :+ f)

  /**
    * Method to yield a Fitness value
    *
    * @return the Fitness value which corresponds to applying all Fitness values together
    */
  def apply: Fitness = fs.foldLeft(Fitness.viable)(_ & _)

  override def toString(): String = s"Viability($fs)"
}

object Viability {
  def create(fs: Fitness*): Viability = Viability(fs)
}

/**
  * This case class defines a X=>T=>Fitness function and a shape factor.
  * The way to think about these FunctionShapes is that we are comparing the trait value (t)
  * against the eco value (x).
  * For the delta function, for instance, if t>x, then viable, otherwise nonViable.
  *
  * @param shape the shape of the function
  * @param f     the (T,X)=>Fitness
  * @tparam T the underlying type of the trait
  * @tparam X the underlying type of the ecofactor
  */
case class FunctionShape[X, T](shape: String, f: X => T => Fitness)

object Fitness {
  val viable: Fitness = new Fitness(1)
  val nonViable: Fitness = viable.-
  val tossup: Fitness = new Fitness(0.5)

  def apply(x: Double): Fitness = {
    require(x >= 0.0 && x <= 1.0, s"invalid Fitness: $x must be in range 0..1")
    new Fitness(x)
  }

  def unapply(f: Fitness): Option[Double] = Some(f.x)
}

object FunctionShape {

  import Fitness.{nonViable, viable}
  //  implicit val spyLogger = Spy.getLogger(getClass)
  // TODO make these more generic
  /**
    * Delta function: if x >= t then viable else nonViable
    */
  val delta: FunctionShape[Double, Double] = FunctionShape[Double, Double]("delta", { x => t => if (t >= x) viable else nonViable })
  val inverseDelta: FunctionShape[Double, Double] = FunctionShape[Double, Double]("delta-inv", { x => t => if (t < x) viable else nonViable })
  val logistic: FunctionShape[Double, Double] = FunctionShape[Double, Double]("logistic", { x => t => Fitness(fLogistic(t - x)) })
  val inverseLogistic: FunctionShape[Double, Double] = FunctionShape[Double, Double]("logistic-inv", { x => t => Fitness(fLogistic(x - t)) })

  val deltaInt: FunctionShape[Int, Double] = FunctionShape[Int, Double]("delta-I", { x => t => if (t >= x) viable else nonViable })
  val inverseDeltaInt: FunctionShape[Int, Double] = FunctionShape[Int, Double]("delta-inv-I", { x => t => if (t < x) viable else nonViable })
  val logisticInt: FunctionShape[Int, Double] = FunctionShape[Int, Double]("logistic-I", { x => t => Fitness(fLogistic(t - x)) })
  val inverseLogisticInt: FunctionShape[Int, Double] = FunctionShape[Int, Double]("logistic-inv-I", { x => t => Fitness(fLogistic(x - t)) })

  private def fLogistic(x: Double): Double = 1 / (1 + math.exp(-x))

}