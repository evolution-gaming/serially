package com.evolutiongaming.concurrent.serially

import com.evolutiongaming.concurrent.CurrentThreadExecutionContext
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future, Promise, TimeoutException}
import scala.util.control.NoStackTrace

class SeriallyAsyncSpec extends AnyWordSpec with ActorSpec with Matchers with ScalaFutures {

  "SeriallyAsync" should {

    "process tasks serially" in new Scope {
      var value = 0
      val promise = Promise[Int]()
      serially { value = promise.future.futureValue }
      val future: Future[Unit] = serially { value = 2 }
      intercept[TimeoutException] { Await.result(future, 100.millis) }
      promise.success(1)
      Await.result(future, timeout.duration)
      value shouldEqual 2
    }

    "process async tasks serially" in new Scope {
      var value = 0
      val promise = Promise[Int]()
      serially.async { promise.future.map(value = _)(CurrentThreadExecutionContext) }
      val future: Future[Unit] = serially { value = 2 }
      intercept[TimeoutException] { Await.result(future, 100.millis) }
      promise.success(1)
      Await.result(future, timeout.duration)
      value shouldEqual 2
    }

    "not fail on exceptions" in new Scope {
      val future1: Future[Nothing] = serially { throw TestException }

      intercept[TestException.type] { Await.result(future1, timeout.duration) }

      val future2: Future[Int] = serially { 1 }
      Await.result(future2, timeout.duration) shouldEqual 1
    }

    "fail tasks when stopped" in new Scope {
      val promise = Promise[Int]()
      val result0: Future[Int] = serially { promise.future.futureValue }
      val result1: Future[Int] = serially { 1 }
      val resultStop: Future[Unit] = serially.stop()
      val result2: Future[Int] = serially { 2 }

      promise.success(0)

      result0.futureValue shouldEqual 0
      result1.futureValue shouldEqual 1
      resultStop.futureValue

      intercept[Serially.Stopped.type] { Await.result(result2, timeout.duration) }

      val result3: Future[Int] = serially { 3 }

      intercept[Serially.Stopped.type] { Await.result(result3, timeout.duration) }
    }
  }

  private trait Scope extends ActorScope {
    val serially: SeriallyAsync = SeriallyAsync()(system)
  }

  case object TestException extends RuntimeException with NoStackTrace
}
