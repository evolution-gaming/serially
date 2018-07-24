package com.evolutiongaming.concurrent.serially

import akka.actor.ActorRefFactory

import scala.concurrent.Future

trait StateVar[S] {

  def value(): S

  def apply[SS](f: S => (S, SS)): Future[SS]

  /**
    * @return previous value
    */
  final def set(value: S): Future[S] = getAndUpdate(_ => value)

  final def update(f: S => S): Future[S] = updateAndGet(f)

  final def updateAndGet(f: S => S): Future[S] = {
    apply { value =>
      val valueNew = f(value)
      (valueNew, valueNew)
    }
  }

  final def getAndUpdate(f: S => S): Future[S] = {
    apply { value =>
      val valueNew = f(value)
      (valueNew, value)
    }
  }

  final def withValue(f: S => Unit): Future[S] = {
    update { value =>
      f(value)
      value
    }
  }
}

object StateVar {

  def apply[S](state: S)(implicit factory: ActorRefFactory): StateVar[S] = {
    val serially = Serially()
    apply(state, serially)
  }

  def apply[S](state: S, serially: Serially): StateVar[S] = {

    @volatile var s = state

    new StateVar[S] {

      def value() = s

      def apply[SS](f: S => (S, SS)): Future[SS] = {
        serially {
          val (ss, result) = f(s)
          s = ss
          result
        }
      }

      override def toString = s"StateVar($s)"
    }
  }
}