package com.phasmid.darwin.genetics

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 5/6/16.
  */
class EcologySpec extends FlatSpec with Matchers {

  val adapter: Adapter[Double, Double] = new AbstractAdapter[Double, Double] {
    def matchFactors(f: Factor, t: Trait[Double]): Option[(Double, String)] = f match {
      case Factor("elephant grass") => t.characteristic.name match {
        case "height" => Some(t.value, "delta-inv")
        case _ => None
      }
    }
  }

  def fitnessFunction(t: Double, shape: String, x: Double): Fitness = shape match {
    case "delta" => if (t >= x) Fitness(1) else Fitness(0)
    case "delta-inv" => if (t < x) Fitness(1) else Fitness(0)
    case _ => throw new GeneticsException(s"ecoFitness does not implement shape $shape")
  }

  "apply" should "work" in {
    val height = Characteristic("height")
    val phenotype: Phenotype[Double] = Phenotype(Seq(Trait(height, 2.0)))
    val elephantGrass = Factor("elephant grass")
    val ecology: Ecology[Double, Double] = Ecology("test", Map("height" -> elephantGrass), fitnessFunction, adapter)
    val adaptatype: Adaptatype[Double] = ecology(phenotype)
    val adaptations = adaptatype.adaptations
    adaptations.size shouldBe 1
    adaptations.head should matchPattern { case Adaptation(Factor("elephant grass"), _) => }
    val ff = adaptations.head.ecoFitness
    val fitness = ff(EcoFactor(elephantGrass, 1.6))
    fitness should matchPattern { case Some(Fitness(_)) => }
    fitness.get.x shouldBe 0.0
  }
}
