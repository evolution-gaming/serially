# Serially [![Build Status](https://travis-ci.org/evolution-gaming/serially.svg)](https://travis-ci.org/evolution-gaming/serially) [![Coverage Status](https://coveralls.io/repos/evolution-gaming/serially/badge.svg)](https://coveralls.io/r/evolution-gaming/serially) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/9ac9217e2baf498ebfe996e163d849b1)](https://www.codacy.com/app/evolution-gaming/serially?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=evolution-gaming/serially&amp;utm_campaign=Badge_Grade) [ ![version](https://api.bintray.com/packages/evolutiongaming/maven/serially/images/download.svg) ](https://bintray.com/evolutiongaming/maven/serially/_latestVersion) [![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

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
resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")

libraryDependencies += "com.evolutiongaming" %% "serially" % "1.0"
```