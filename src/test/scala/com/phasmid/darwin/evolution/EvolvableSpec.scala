package com.phasmid.darwin.evolution

import com.phasmid.laScala.values.Rational
import com.phasmid.laScala.{LongRNG, RNG, Shuffle}
import org.scalatest.{FlatSpec, Inside, Matchers}

/**
  * Created by scalaprof on 7/25/16.
  */
class EvolvableSpec extends FlatSpec with Matchers with Inside {

  case class MockEvolvable(members: Iterable[Int], go: Option[Generation[Int]]) extends BaseEvolvable[Int, Int, Long](members, go) {

    def build(members: Iterator[Int], go: Option[Generation[Int]]): BaseEvolvable[Int, Int, Long] = MockEvolvable(members.toSeq, go)

    def evaluateFitness(x: Int): Boolean = x % 2 == 0

    def random: RNG[Long] = LongRNG(0)

    def offspring: Iterator[Int] = members.toIterator filter { _ > 3 } map { _ + 100 }

    def get: Int = go.get.get

    def shuffle: Iterable[Int] = Shuffle(random.value)(iterator.toSeq)
  }

  "MockEvolvable" should "shuffle properly" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), None)
    evolvable.shuffle shouldBe Stream(1, 1, 3, 2, 13, 5, 8)
  }
  it should "build properly" in {
    val evolvable = MockEvolvable(Seq(), None)
    val x = evolvable.build(Seq(1, 1, 3, 2, 13, 5, 8).iterator,Some(NumberedGeneration(0)))
    x.iterator.toSeq shouldBe Seq(1, 1, 3, 2, 13, 5, 8)
    x.get shouldBe 0
  }
  it should "yield 3 offspring" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), None)
    evolvable.offspring.toSeq shouldBe Seq(105,108,113)
  }
  it should "yield 2 survivors -- the even numbers" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), None)
    evolvable.survivors.toSeq shouldBe Seq(2,8)
  }
  it should "yield 5 from * 2/3" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13, 21), None)
    (evolvable*Rational(2,3)).toSeq shouldBe Seq(1, 21, 3, 5, 13)
  }
  it should "retain 3 after subtracting result of * 2/3" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13, 21), None)
    val x = evolvable*Rational(2,3)
    (evolvable - x).toSeq shouldBe Seq(2, 8)
  }
  it should "get random value 0" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), None)
    evolvable.random.value shouldBe 0L
  }
  it should "evolve" in {
    val evolvable = MockEvolvable(Seq(1, 1, 2, 3, 5, 8, 13), Some(NumberedGeneration(0)))
    evolvable.go should matchPattern { case Some(NumberedGeneration(0)) => }
    val next = evolvable.next
    next should matchPattern { case MockEvolvable(_, _) => }
    inside(next) {
      case me @ MockEvolvable(members, go) =>
        go should matchPattern { case Some(NumberedGeneration(1)) => }
        members shouldBe Stream(2, 8, 108, 113)
        me.get shouldBe 1
    }
  }
}
