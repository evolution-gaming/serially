package com.evolutiongaming.concurrent.serially

import akka.actor.ActorRefFactory
import com.evolutiongaming.concurrent.CurrentThreadExecutionContext
import com.evolutiongaming.concurrent.FutureHelper._

import scala.concurrent.Future

trait AsyncVar[S] extends StateVar[S] {

  def async[SS](f: S => Future[(S, SS)]): Future[SS]

  final def apply[SS](f: S => (S, SS)): Future[SS] = async(s => f(s).future)
}

object AsyncVar {

  def apply[S](state: S)(implicit factory: ActorRefFactory): AsyncVar[S] = {
    val serially = SeriallyAsync()
    apply(state, serially)
  }

  def apply[S](state: S, serially: SeriallyAsync): AsyncVar[S] = {

    implicit val ec = CurrentThreadExecutionContext

    @volatile var s = state

    new AsyncVar[S] {

      def value() = s

      def async[SS](f: S => Future[(S, SS)]): Future[SS] = {
        serially.async[SS] {
          for {
            (ss, result) <- f(s)
          } yield {
            s = ss
            result
          }
        }
      }

      override def toString = s"AsyncVar($s)"
    }
  }
}
