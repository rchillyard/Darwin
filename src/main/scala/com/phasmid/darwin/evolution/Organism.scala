package com.phasmid.darwin.evolution

import com.phasmid.darwin.genetics.Reproduction
import com.phasmid.laScala.Version
import com.phasmid.laScala.fp.Named

/**
  * TODO do something with the name
  * @param name
  * @param generation
//  * @param ev$1
  * @tparam R
  * @tparam V
  */
case class Organism[R : Reproduction, V](name: String, generation: Version[V]) extends Named {

}
