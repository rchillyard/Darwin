package com.phasmid.darwin.genetics

/**
  * Created by scalaprof on 5/5/16.
  */
case class Phenome(name: String, characteristics: Seq[Characteristic]) extends Identifier

case class Characteristic(name: String) extends Identifier
