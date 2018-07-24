package com.evolutiongaming.concurrent.serially

import akka.actor.ActorRefFactory
import com.evolutiongaming.concurrent.CurrentThreadExecutionContext
import com.evolutiongaming.concurrent.FutureHelper._

import scala.concurrent.Future
import scala.util.{Success, Try}

trait SeriallyAsync extends Serially {

  def async[T](f: => Future[T]): Future[T]

  def stop(): Future[Unit]

  final def apply[T](f: => T): Future[T] = async(f.future)
}

object SeriallyAsync {

  def apply(name: Option[String] = None)(implicit factory: ActorRefFactory): SeriallyAsync = {
    val serially = Serially(name)
    apply(serially)
  }

  def apply(serially: Serially): SeriallyAsync = {

    var future = Future.unit

    implicit val ec = CurrentThreadExecutionContext
    val tryUnit = Success(())
    val tryToUnit = (_: Try[_]) => tryUnit

    new SeriallyAsync {

      def async[T](f: => Future[T]) = {
        serially {
          val result = if (future.isCompleted) f else future.flatMap(_ => f)
          future = FutureOps(result).transform(tryToUnit)
          result
        }.flatten
      }

      def stop() = serially.stop()
    }
  }


  def now: SeriallyAsync = new SeriallyAsync {

    def async[T](f: => Future[T]) = f

    def stop() = Future.unit
  }
}