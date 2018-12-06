package com.evolutiongaming.concurrent.serially

import java.util.concurrent.atomic.AtomicBoolean

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}
import com.evolutiongaming.concurrent.FutureHelper._

import scala.concurrent.{Future, Promise}
import scala.util.Try
import scala.util.control.NoStackTrace

trait Serially {

  def apply[T](f: => T): Future[T]

  def stop(): Future[Unit]
}

object Serially {

  private val StoppedFailure = Future.failed(Stopped)

  def apply(name: Option[String] = None)(implicit factory: ActorRefFactory): Serially = {

    case class Func(f: () => Unit)
    case class StopPrepare(promise: Promise[Unit])
    case class StopCommit(promise: Promise[Unit])

    def actor() = new Actor {
      def receive: Receive = {
        case Func(f)              => f()
        case StopPrepare(promise) => self.tell(StopCommit(promise), ActorRef.noSender)
        case StopCommit(promise)  => promise.success(()); context.stop(self);
      }
    }

    val props = Props(actor())

    val ref = name match {
      case None       => factory.actorOf(props)
      case Some(name) => factory.actorOf(props, name)
    }

    val stopped = new AtomicBoolean(false)

    def running[T](f: => Future[T]) = {
      if (stopped.get()) StoppedFailure else f
    }

    new Serially {

      def apply[T](f: => T): Future[T] = {
        running {
          val promise = Promise[T]()
          val ff = () => {
            val result = Try(f)
            promise.complete(result)
            ()
          }
          ref.tell(Func(ff), ActorRef.noSender)
          promise.future
        }
      }

      def stop() = {
        running {
          if (stopped.compareAndSet(false, true)) {
            val promise = Promise[Unit]()
            ref.tell(StopPrepare(promise), ActorRef.noSender)
            promise.future
          } else {
            StoppedFailure
          }
        }
      }
    }
  }


  def now: Serially = new Serially {

    def apply[T](f: => T): Future[T] = Future.fromTry(Try(f))

    def stop() = Future.unit
  }


  case object Stopped extends RuntimeException with NoStackTrace
}