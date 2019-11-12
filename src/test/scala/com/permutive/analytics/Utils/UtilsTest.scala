package com.permutive.analytics.Utils

import com.permutive.analytics.Utils.Utils.withInhourOf
import org.joda.time.LocalDateTime
import org.scalatest.{FunSuite, Matchers}

class UtilsTest extends FunSuite with Matchers {

  test("Update message that falls within the interval") {
    // Given
    val updateMsg = """{"messageType":"VisitUpdate","visit":{"id":"68a03cb4-9e5b-43d2-b2d9-4d808e192b62","engagedTime":10,"completion":0.47596153846153844,"updatedAt":"2015-05-18T22:50:49.254"}}"""
    val time = "2015-05-18T22:30:39.254"
    val in = LocalDateTime.parse(time)
    val hourLater = in.plusHours(1)

    // When, then
    withInhourOf(in, updateMsg) shouldBe true
  }

  test("Create message that falls within the interval") {
    // Given
    val createMsg = """{"messageType":"VisitCreate","visit":{"id":"3a98e4f3-49cf-48a1-b63a-3eeaa2443f26","userId":"630adcc1-e302-40a6-8af2-d2cd84e71720","documentId":"b61d8914-560f-4985-8a5f-aa974ad0c7ab","createdAt":"2015-05-18T22:50:49.254Z"}}"""
    val time = "2015-05-18T22:30:39.254"
    val in = LocalDateTime.parse(time)
    val hourLater = in.plusHours(1)

    // When, then
    withInhourOf(in, createMsg) shouldBe true
  }

  test("Create message that falls without the interval") {
    // Given
    val createMsg = """{"messageType":"VisitCreate","visit":{"id":"3a98e4f3-49cf-48a1-b63a-3eeaa2443f26","userId":"630adcc1-e302-40a6-8af2-d2cd84e71720","documentId":"b61d8914-560f-4985-8a5f-aa974ad0c7ab","createdAt":"2015-05-18T22:50:49.254Z"}}"""
    val time = "2015-05-15T22:30:39.254"
    val in = LocalDateTime.parse(time)
    val hourLater = in.plusHours(1)

    // When, then
    withInhourOf(in, createMsg) shouldBe false
  }


}
