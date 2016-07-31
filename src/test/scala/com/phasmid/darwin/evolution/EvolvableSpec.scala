package com.phasmid.darwin.evolution

import com.phasmid.laScala.values.Rational
import com.phasmid.laScala.{LongRNG, RNG, Shuffle}
import org.scalatest.{FlatSpec, Inside, Matchers}

/**
  * Created by scalaprof on 7/25/16.
  */
class EvolvableSpec extends FlatSpec with Matchers with Inside {

  case class MockEvolvable(members: Iterable[Int], go: Option[Generation[Int]]) extends BaseEvolvable[Int, Int, Long](members, go) {

    def *(fraction: Rational): Iterator[Int] = Shuffle(random.value)(members.toSeq).take((fraction * members.size).floor.toInt).toIterator

    def build(members: Iterator[Int], go: Option[Generation[Int]]): BaseEvolvable[Int, Int, Long] = MockEvolvable(members.toSeq, go)

    def evaluateFitness(x: Int): Boolean = x % 2 == 0

    def random: RNG[Long] = LongRNG(0)

    def offspring: Iterator[Int] = members.toIterator filter { _ > 3 } map { _ + 100 }

    def get: Int = ???
  }

  "MockEvolvable" should "evolve" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Some(NumberedGeneration(0)))
    evolvable.go should matchPattern { case Some(NumberedGeneration(0)) => }
    val next = evolvable.next
    next should matchPattern { case MockEvolvable(_, _) => }
    inside(next) {
      case MockEvolvable(members, go) =>
        go should matchPattern { case Some(NumberedGeneration(1)) => }
        members shouldBe Stream(2, 8, 108, 113)
    }

  }
}
