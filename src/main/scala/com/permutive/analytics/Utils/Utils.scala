package com.permutive.analytics.Utils

import com.permutive.analytics._
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

object Utils {

  def subString(s: String, len: Int): String = {
    s.substring(0, len) + "..."
  }

  def withInhourOf(start: LocalDateTime, msg: String): Boolean = {

    val end = start.plusHours(1)

    if (msg.contains("create")) {
      val cm = CreateMessage(msg).get.visit.createdAt.dropRight(1)
      val createdAt = LocalDateTime.parse(cm)
      0 <= createdAt.compareTo(start) && createdAt.compareTo(end) < 0
    } else {
      val um = UpdateMessage(msg).get.visit.updatedAt.dropRight(1)
      val updatedAt = LocalDateTime.parse(um)
      0 <= updatedAt.compareTo(start) && updatedAt.compareTo(end) < 0
    }
  }

  def show(table: Seq[Seq[Any]]): String = {
    if (table.isEmpty) ""
    else {
      // Get column widths based on the maximum cell width in each column (+2 for a one character padding on each side)
      val colWidths = table.transpose.map(_.map(cell => if (cell == null) 0 else cell.toString.length).max + 2)
      // Format each row
      val rows = table.map(_.zip(colWidths).map { case (item, size) => (" %-" + (size - 1) + "s").format(item) }
        .mkString("|", "|", "|"))
      // Formatted separator row, used to separate the header and draw table borders
      val separator = colWidths.map("-" * _).mkString("+", "+", "+")
      // Put the table together and return
      (separator +: rows.head +: separator +: rows.tail :+ separator).mkString("\n")
    }
  }


  def toRows(rawMessages: List[String], start: LocalDateTime): List[ROW] = {

    // start by partition the messages into creation and update
    val (createRaw, updateRaw) = rawMessages.partition(_.contains("create"))

    // all visit update messages
    val ums = updateRaw.map(UpdateMessage(_))

    // all valid visit create messages
    val cms = createRaw.map(CreateMessage(_))
      .filter { m =>
        ums.groupBy(_.get.visit.id).get(m.get.visit.id).fold(false) { _ => true }
      }

    // all documents
    val docs = cms.map(_.get.visit.documentId).distinct

    // visitors for a given document
    val visitors = cms.groupBy(m => m.get.visit.documentId)

    // each visitId has one latest update
    val lastUpdateOf =
      ums.groupBy(_.get.visit.id).mapValues(v => v.maxBy(_.get.visit.updatedAt))

    //  get time and completion of the last update
    val timeAndCompletion =
      lastUpdateOf.mapValues(mu => (mu.get.visit.engagedTime, mu.get.visit.completion))

    val fmt = DateTimeFormat.forPattern("yyyy-mm-dd'T'HH:MM:SSS")

    val to = start.plusHours(1)

    // put the row together for each document
    val raws = docs.map { docId =>

      val visitorOf = visitors(docId)
      val visits = visitorOf.size
      val uniques = visitorOf.distinct.size

      // sum up the time and completion over all visitors
      val (time, completion) = visitorOf.map { user => timeAndCompletion(user.get.visit.id)
      }.unzip match {
        case (times, completions) => (times.sum, completions.sum)
      }

      ROW(docId, start.toString(fmt), to.toString(fmt), visits, uniques, time, completion)
    }
    raws.sortBy(_.documentId)
  }
}