package com.evolutiongaming.concurrent.serially

import org.scalatest.{Matchers, WordSpec}

import scala.util.Success

class AsyncVarSpec extends WordSpec with Matchers {

  "AsyncVar" should {

    "apply" in new Scope {
      val result = state { before =>
        val after = before + 1
        (after, before)
      }
      result.value shouldEqual Some(Success(0))
      state.value() shouldEqual 1
    }

    "value" in new Scope {
      state.value() shouldEqual 0
    }

    "update" in new Scope {
      val result = state.update(_ + 1)
      result.value shouldEqual Some(Success(1))
      state.value() shouldEqual 1
    }

    "updateAndGet" in new Scope {
      val result = state.updateAndGet(_ + 1)
      result.value shouldEqual Some(Success(1))
      state.value() shouldEqual 1
    }

    "getAndUpdate" in new Scope {
      val result = state.getAndUpdate(_ + 1)
      result.value shouldEqual Some(Success(0))
      state.value() shouldEqual 1
    }

    "withValue" in new Scope {
      var result = 1
      state.withValue(result = _)
      result shouldEqual 0
    }

    "set" in new Scope {
      val result = state.set(1)
      result.value shouldEqual Some(Success(0))
      state.value() shouldEqual 1
    }

    "toString" in new Scope {
      state.toString shouldEqual "AsyncVar(0)"
    }
  }

  private trait Scope {
    val serially = SeriallyAsync.now
    val state = AsyncVar(0, serially)
  }
}