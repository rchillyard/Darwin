package com.phasmid.darwin.util

import scala.util.{Failure, Success, Try}

/**
  * Created by scalaprof on 4/12/16.
  */
object MonadOps {
  def sequence[X](xy: Try[X]): Either[Throwable, X] =
    xy match {
      case Success(s) => Right(s)
      case Failure(e) => Left(e)
    }

  def optionLift[X, Y](f: X => Y)(xo: Option[X]): Option[Y] = xo map f

  def optionFlatLift[X, Y](f: X => Option[Y])(xo: Option[X]): Option[Y] = xo flatMap f

  def tryLift[X, Y](f: X => Y)(xt: Try[X]): Try[Y] = xt map f

  def trial[X, Y](f: X => Y)(x: => X): Try[Y] = tryLift(f)(Try(x))
}