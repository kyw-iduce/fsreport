package com.permutive

import com.permutive.analytics.Utils.Utils.subString
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, parser}
import io.circe.generic.auto._

package object analytics {

  case class CreateVisit(id: String, userId: String, documentId: String, createdAt: String)

  case class UpdateVisit(id: String, engagedTime: Double, completion: Double, updatedAt: String)

  case class CreateMessage(messageType: String, visit: CreateVisit)

  object CreateMessage {
    def apply(jsonstr: String): Option[CreateMessage] = {
      parser.parse(jsonstr) match {
        case Right(value) => value.as[CreateMessage].right.toOption
        case _ => None
      }
    }

    implicit val cmDecoder: Decoder[CreateMessage] = deriveDecoder
  }

  case class UpdateMessage(messageType: String, visit: UpdateVisit)

  object UpdateMessage {
    def apply(message: String): Option[UpdateMessage] = {
      parser.parse(message) match {
        case Right(value) => value.as[UpdateMessage].right.toOption
        case _ => None
      }
    }

    implicit val upDecoder: Decoder[UpdateMessage] = deriveDecoder
  }

  case class ROW(documentId: String, startTime: String, endTime: String, visits: Int, uniques: Int, time: Double, completion: Double) {

    def toMyList(): List[String] = {
      List(subString(documentId, 12), startTime, endTime, visits.toString, uniques.toString, time.toString, completion.toString)
    }
  }

}