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

import com.phasmid.darwin.base.{Identifiable, Plain}


/**
  * Class to define the concept of Fitness. Fitness applies in each of the two aspects of evolution:
  * Survival of the Fittest and Sexual Selection.
  *
  * Note that we do not make it a case class because we want to distinguish between creating new
  * instances of Fitness with/without checking the requirement.
  *
  * In Survival, fitness is a measure of the viability of an organism's phenotype adapting to an environment.
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
  * In Sexual Selection, fitness is a measure of the probability of a mate being chosen from amongst other potential mates.
  *
  * Created by scalaprof on 5/5/16.
  */
class Fitness private(val x: Double) extends (() => Double) with Ordering[Fitness] with Plain {

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
    case Fitness(y) => math.abs(x - y) < 1E-10
    case _ => false
  }

  override def hashCode(): Int = x.hashCode()

  /**
    * Used by render()
    *
    * @return a String based on x
    */
  override def toString(): String = s"Fitness($x)"

  def compare(f1: Fitness, f2: Fitness): Int = f1.x compareTo f2.x

}

/**
  * Viability represents overall fitness when there are many individual fitness values for an Adaptatype.
  * As discussed above, the basic model is that combined fitness is the product of each individual fitness.
  * Thus viability is could be defined simply as the product of each fitness.
  * Yet, this may result in some very small fitness values when an Adaptatype has many adaptations.
  * Thus it is generally better to use the geometric mean, by taking the nth-root of the product.
  *
  * Viability can also be used in the case where there is more than one sexually-selective trait. However,
  * in most simple systems, we typically stick to one such trait and thus Viability in that context will be
  * based on one Fitness only. You can think of it then as the Viability of a pair as mates capable of producing live offspring.
  * The probability of their progeny surviving is covered by environmental (survival) Fitness.
  *
  * CONSIDER We could allow some variation on this theme by using implicits.
  * However, in this particular situation, it's quite tricky because the Fitness and Viability both extend no-param
  * functions which yield a result. The use of apply makes it very difficult to implement an implicit mechanism.
  *
  * @param fs a sequence of Fitness values
  */
case class Viability(fs: Seq[Fitness]) extends (() => Fitness) {

  /**
    * Method to add a Fitness to this Viability
    *
    * @param f the Fitness value to add
    * @return a new Viability with f added
    */
  def +(f: Fitness) = Viability(fs :+ f)

  /**
    * Method to yield a Fitness value.
    * If the Viability is empty, then we assume a perfectly fit (viable) result.
    *
    * @return the Fitness value which corresponds to the geometric mean of all Fitness values.
    */
  def apply: Fitness = Viability.geometricMean(fs)

  override def toString(): String = s"Viability($fs)"
}

object Viability {
  def create(fs: Fitness*): Viability = Viability(fs)

  private def geometricMean(fs: Seq[Fitness]) = if (fs.isEmpty) Fitness.viable
  else Fitness(math.exp(math.log(fs.foldLeft(Fitness.viable)(_ & _)()) / fs.length))
}

/**
  * This case class defines a X=>T=>Fitness function and a shape factor.
  * The way to think about these FunctionShapes is that we are comparing the trait value (t)
  * against the eco value (x).
  * For the delta function, for instance, if t>x, then viable, otherwise nonViable.
  *
  * @param name the name of the shape function
  * @param f    the (T,X)=>Fitness
  * @tparam T the underlying type of the trait
  * @tparam X the underlying type of the ecofactor
  */
case class FunctionShape[X, T](name: String, f: X => T => Fitness) extends Identifiable

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

  import Numeric.IntIsIntegral

  /**
    * Generic method to construct a FunctionShape based on two parametric types: X and T, and two functions.
    *
    * @param f    a function which, given two Double values, yields a Fitness (examples are Dirac delta function or logistic function)
    * @param g    a function which, given a Fitness, yields a Fitness (examples are identity and the negation method)
    * @param name the name of the shape
    * @tparam X the underlying eco factor type
    * @tparam T the underlying trait type
    * @return FunctionShape object
    */
  def apply[X: Numeric, T: Numeric](f: (Double, Double) => Fitness, g: Fitness => Fitness, name: String): FunctionShape[X, T] = FunctionShape[X, T](name, { x: X => t: T => g(f(implicitly[Numeric[T]].toDouble(t), implicitly[Numeric[X]].toDouble(x))) })

  /**
    * Generic method to construct a FunctionShape based on two parametric types: X and T, two functions and a constant.
    *
    * @param f    a function which, given two Double values, yields a Fitness (examples are Dirac delta function or logistic function)
    * @param g    a function which, given a Fitness, yields a Fitness (examples are identity and the negation method)
    * @param k    the constant which, applied to a sigmoid function (such as logistic), varies its shape -- a value of zero would result in
    *             the Dirac delta function; a value of +infinity would result in half for all inputs (i.e. no discrimination).
    * @param name the name of the shape
    * @tparam X the underlying eco factor type
    * @tparam T the underlying trait type
    * @return FunctionShape object
    */
  def apply[X: Numeric, T: Numeric](f: Double => (Double, Double) => Fitness, g: Fitness => Fitness, k: Double, name: String): FunctionShape[X, T] = FunctionShape[X, T](name, { x: X => t: T => g(f(k)(implicitly[Numeric[T]].toDouble(t), implicitly[Numeric[X]].toDouble(x))) })

  /**
    * This is the standard value for the logistic function.
    */
  val k = 1

  /**
    * Following are the "usual" four shape functions: Dirac and Logistic (regular and inverted).
    * They are "usual" because the FunctionShape is based on Double, Double.
    * If you need other shapes, simply build a FunctionShape in a similar manner to here.
    */
  val shapeDirac: FunctionShape[Double, Double] = FunctionShape(dirac, identity, "shapeDirac")
  val shapeDiracInv: FunctionShape[Double, Double] = FunctionShape(dirac, _.-, "shapeDirac-i")
  val shapeLogistic: FunctionShape[Double, Double] = FunctionShape(logistic, identity, k, "shapeLogistic")
  val shapeLogisticInv: FunctionShape[Double, Double] = FunctionShape(logistic, _.-, k, "shapeLogistic-i")

  /**
    * Following are the Int/Double values of the four shape functions: Dirac and Logistic (regular and inverted).
    */
  val shapeDirac_I: FunctionShape[Int, Double] = FunctionShape(dirac, identity, "shapeDirac")
  val shapeDiracInv_I: FunctionShape[Int, Double] = FunctionShape(dirac, _.-, "shapeDirac-i")
  val shapeLogistic_I: FunctionShape[Int, Double] = FunctionShape(logistic, identity, k, "shapeLogistic")
  val shapeLogisticInv_I: FunctionShape[Int, Double] = FunctionShape(logistic, _.-, k, "shapeLogistic-i")

  /**
    * Method to compare x1 with x2 and determine viability.
    * The shape of this function is a Dirac "delta" function.
    *
    * @param x1 the first parameter
    * @param x2 the second parameter
    * @return viable if x1>=x2 otherwise, nonViable
    */
  def dirac(x1: Double, x2: Double): Fitness = if (x1 >= x2) viable else nonViable

  /**
    * Method to compare x1 with x2 using a shapeLogistic function.
    * The shape of this function is a sigmoid function.
    *
    * @param k  the scale factor for the logistic function: if k = 1, we get the "standard" logistic function;
    *           if k = +infinity, then all results would be essentially one half, if k = 0, the logistic function behaves
    *           identically with the dirac function.
    * @param x1 the first parameter
    * @param x2 the second parameter
    * @return approximately 1 if x1 >> x2, approximately 0 if x1 << x2, and exactly 1/2 if x1--x2
    */
  def logistic(k: Double)(x1: Double, x2: Double): Fitness = Fitness(logisticFunction(x1 - x2, k))

  private def logisticFunction(x: Double, k: Double) = 1 / (1 + math.exp(-x / k))

}