package com.permutive.analytics

import java.nio.file.{Path, Paths}
import java.util.concurrent.Executors
import cats.effect._
import cats.implicits._
import com.permutive.analytics.Utils.Utils.{show, toRows, withInhourOf}
import com.permutive.analytics.messaging.Queue
import fs2._
import org.joda.time.LocalDateTime

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  private val blockingExecutionContext =
    Resource.make(IO(
      ExecutionContext.fromExecutorService(
        Executors.newFixedThreadPool(2))))(ec => IO(ec.shutdown()))

  def app(path: Path, start: LocalDateTime): Stream[IO, String] =
    Stream.resource(blockingExecutionContext).flatMap { blockingEC =>
      Queue.linesFromFile[IO](path, blockingEC, 4096).messages
        .takeWhile {
          withInhourOf(start, _)
        }
    }

  def run(args: List[String]): IO[ExitCode] = {
    val path =
          args.headOption
            .map(s => Paths.get(s))
            .getOrElse(throw new Exception("Please provide a valid input file"))

    // this will be pulled into config ...
    val start = LocalDateTime.parse("2015-05-18T22:30:49.254")

    // Let's loop over a day
    val daily = Range(0, 24).map { h =>

      val rawMessages =
        app(path, start.plusHours(h)).compile.toList.unsafeRunSync

      val rows = toRows(rawMessages, start)

      rows.map(_.toMyList())
    }

    val title = List("Document", "Start-time", "End-time", "Visits", "Uniques", "Time", "Completion")

    // Output report to the console
    daily.foreach { data =>
      println(show(Seq(title) ++ data))
    }
    IO().as(ExitCode.Success)
  }
}