package com.evolutiongaming.concurrent.serially

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise, TimeoutException}
import scala.util.control.NoStackTrace

class SeriallySpec extends WordSpec with ActorSpec with Matchers with ScalaFutures {

  "Serially" should {

    "process tasks serially" in new Scope {
      var value = 0
      val promise = Promise[Int]()
      serially { value = promise.future.futureValue }
      val future = serially { value = 2 }
      intercept[TimeoutException] { Await.result(future, 100.millis) }
      promise.success(1)
      Await.result(future, timeout.duration)
      value shouldEqual 2
    }

    "not fail on exceptions" in new Scope {
      val future1 = serially { throw TestException }

      intercept[TestException.type] { Await.result(future1, timeout.duration) }

      val future2 = serially { 1 }
      Await.result(future2, timeout.duration) shouldEqual 1
    }

    "fail tasks when stopped" in new Scope {
      val promise = Promise[Int]()
      val result0 = serially { promise.future.futureValue }
      val result1 = serially { 1 }
      val resultStop = serially.stop()
      val result2 = serially { 2 }

      promise.success(0)

      result0.futureValue shouldEqual 0
      result1.futureValue shouldEqual 1
      resultStop.futureValue

      intercept[Serially.Stopped.type] { Await.result(result2, timeout.duration) }

      val result3 = serially { 3 }

      intercept[Serially.Stopped.type] { Await.result(result3, timeout.duration) }
    }
  }

  private trait Scope extends ActorScope {
    val serially = Serially()
  }

  case object TestException extends RuntimeException with NoStackTrace
}

