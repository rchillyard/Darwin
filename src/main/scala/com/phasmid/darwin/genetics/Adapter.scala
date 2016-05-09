package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/9/16.
  */
sealed trait Adapter[T,X] extends ((Factor,Trait[T])=>Adaptation[X]) {

}
