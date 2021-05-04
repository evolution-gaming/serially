# Serially
[![Build Status](https://github.com/evolution-gaming/serially/workflows/CI/badge.svg)](https://github.com/evolution-gaming/serially/actions?query=workflow%3ACI)
[![Coverage Status](https://coveralls.io/repos/github/evolution-gaming/serially/badge.svg?branch=master)](https://coveralls.io/github/evolution-gaming/serially?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9ac9217e2baf498ebfe996e163d849b1)](https://www.codacy.com/app/evolution-gaming/serially?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=evolution-gaming/serially&amp;utm_campaign=Badge_Grade)
[![Version](https://img.shields.io/badge/version-click-blue)](https://evolution.jfrog.io/artifactory/api/search/latestVersion?g=com.evolutiongaming&a=serially_2.13&repos=public)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

This library contains `Serially.scala` which allows to run tasks serially
The behavior is somehow similar to what actors propose, however it provides typesafety.
Also it is easy to write tests using `Serially.now` to avoid unnecessary concurrency.

## `Serially` example 

This example explains how we can ensure that there are no concurrent updates to `var state`

```scala
val system = ActorSystem() // yes, we have dependency on akka
val serially = Serially()

var state: Int = 0

// this runs sequentially, like message handling in actors 
serially {
 state = state + 1
}

// you also can expose computation result as Future[T]
val stateBefore: Future[Int] = serially {
  val stateBefore = state
  state = state + 1
  stateBefore
} 
```

## `StateVar` example

Basically the same as on previous example with less code

```scala
val system = ActorSystem()
val serially = Serially()
val state = StateVar(0, serially)
val result: Future[String] = state { before => 
  val after = before + 1
  (after, "ok")
} 
```


## Setup

```scala
addSbtPlugin("com.evolution" % "sbt-artifactory-plugin" % "0.0.2")

libraryDependencies += "com.evolutiongaming" %% "serially" % "1.0.6"
```