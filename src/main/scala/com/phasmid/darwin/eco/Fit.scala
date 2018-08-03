package com.phasmid.darwin.eco

import com.phasmid.laScala.fuzzy.Probability
import com.phasmid.laScala.values.Rational

import scala.util.Try

/**
  * Trait which defines the properties of an Individual, the kind of thing that makes up the members of a Colony, for instance.
  */
trait Fit[Z] {
  /**
    * Method to get the fitness of the given individual in the given environment
    *
    * @param z the organism whose fitness we are interested in
    * @param env the Environment in which this Individual thrives
    * @return the Fitness of the Individual in the ecology, wrapped in Try
    */
  def fitness[T, X](z: Z, env: Environment[T, X]): Try[Fitness]

  /**
    * Method which determines if a particular Fitness value will be considered sufficiently fit to survive this generation
    *
    * @param f a Fitness value
    * @return true if f represents a sufficiently fit value to survive to the next generation
    */
  def isFit(f: Fitness): Boolean

  /**
    * Method which determines if a particular Fitness value will be considered sufficiently fit to survive this generation
    *
    * NOTE that if there is a logic error in the calculation of fitness, we may throw an exception here
    *
    * @param z the organism whose fitness we are interested in
    * @param env the Environment in which this Individual thrives
    * @return true if f represents a sufficiently fit value to survive to the next generation
    *         @throws Exception if there is a logic error
    */
  def isFit[T, X](z: Z, env: Environment[T, X]): Boolean = (fitness(z, env) map isFit _).get
}

/**
  * Trait which can be used as a type class to determine if an individual organism of type Z survives (i.e. has a sufficiently high fitness in the current environment).
  * @tparam Z type of an object whose survivability is needed.
  */
trait Survival[Z] {
  def survives(z: Z): Boolean

  /**
    * This constant determines the proportion of non-survivors who can yet produce offspring.
    *
    * @return a Probability such that  between 0 and 1 which should be the result of invoking Probability.biasedCoinFlip
    */
  val posthumousProgeny: Probability[Boolean, Rational[Int]]
}