package com.evolutiongaming.concurrent.serially

import akka.actor.ActorRefFactory

import scala.concurrent.Future

trait StateVar[T] {

  def value(): T

  def apply[TT](f: T => (T, TT)): Future[TT]

  /**
    * @return previous value
    */
  def set(value: T): Future[T] = getAndUpdate(_ => value)

  def update(f: T => T): Future[T] = updateAndGet(f)

  def updateAndGet(f: T => T): Future[T] = {
    apply { value =>
      val valueNew = f(value)
      (valueNew, valueNew)
    }
  }

  def getAndUpdate(f: T => T): Future[T] = {
    apply { value =>
      val valueNew = f(value)
      (valueNew, value)
    }
  }

  def withValue(f: T => Unit): Future[T] = {
    update { value =>
      f(value)
      value
    }
  }
}

object StateVar {

  def apply[T](initial: T)(implicit factory: ActorRefFactory): StateVar[T] = {
    val serially = Serially()
    apply(initial, serially)
  }

  def apply[T](initial: T, serially: Serially): StateVar[T] = {

    @volatile var state = initial

    new StateVar[T] {

      def value() = state

      def apply[TT](f: T => (T, TT)): Future[TT] = {
        serially {
          val (stateNew, result) = f(state)
          state = stateNew
          result
        }
      }

      override def toString = s"StateVar($state)"
    }
  }
}