package com.phasmid.darwin.evolution

import com.phasmid.darwin.eco.{Environment, Fit, Fitness, Survival}
import com.phasmid.laScala.fuzzy.Probability
import com.phasmid.laScala.values.Rational
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.util.{Success, Try}

class EvoluteSpec extends FlatSpec with Matchers with Inside {

  trait FitMember extends Fit[MockMember] {
    def fitness[T, X](z: MockMember, env: Environment[T, X]): Try[Fitness] = Success(Fitness(1.0))

    def isFit(f: Fitness): Boolean = f.x > 0.5
  }
  implicit object FitMember extends FitMember

  trait ReproductiveMember extends Reproductive[MockMember] {
    def progeny(zs: Seq[MockMember]): Seq[MockMember] = Seq(MockMember("Alice"),MockMember("Bob"))
  }
  implicit object ReproductiveMember extends ReproductiveMember

  trait SurvivalMember extends Survival[MockMember] {
    def survives(z: MockMember): Boolean = z.name != "test"

    /**
      * This constant determines the proportion of non-survivors who can yet produce offspring.
      *
      * @return a Probability such that  between 0 and 1 which should be the result of invoking Probability.biasedCoinFlip
      */
    val posthumousProgeny: Probability[Boolean, Rational[Int]] = Probability.biasedCoinFlip(Rational(1,3))
  }
  implicit object SurvivalMember extends SurvivalMember

  import Random.RandomizableLong
  implicit val rng = RNG[Long](0L)

  behavior of "Evolute"

  it should "implement build" in {
    val member = MockMember("test")
    val target = Evolute_Colony(Seq[MockMember]())
    val z: Evolute[MockMember] = target.build(List(member))
    z.iterator.next() shouldBe member
  }

  it should "implement next" in {
    val member = MockMember("Adam")
    val target = Evolute_Colony(Seq[MockMember](member))
    target.next() shouldBe Success(Evolute_Colony(Seq(MockMember("Adam"), MockMember("Alice"), MockMember("Bob"))))
  }
}

case class MockMember(name: String) {

}