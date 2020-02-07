package io.mehow.ruler

import io.kotlintest.matchers.numerics.shouldBeZero
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.kotlintest.specs.BehaviorSpec
import java.math.RoundingMode.DOWN
import kotlin.Long.Companion.MAX_VALUE
import kotlin.Long.Companion.MIN_VALUE

class DistanceSpec : BehaviorSpec({
  Given("zero distance") {
    val zeroDistance = Distance.Zero

    Then("it should not have any meters") {
      zeroDistance.metersPart shouldBe 0L
    }

    Then("it should not have any nanometers") {
      zeroDistance.nanosPart.shouldBeZero()
    }

    When("I add it to itself") {
      val newDistance = zeroDistance + zeroDistance

      Then("it is still zero") {
        newDistance shouldBe zeroDistance
      }
    }

    When("I subtract it from itself") {
      val newDistance = zeroDistance - zeroDistance

      Then("it is still zero") {
        newDistance shouldBe zeroDistance
      }
    }

    When("I add a distance to it") {
      Then("it should be equal to that distance") {
        assertAll(DistanceGenerator()) { distance ->
          val newDistance = zeroDistance + distance
          newDistance shouldBe distance
        }
      }
    }
  }

  Given("min distance") {
    val minDistance = Distance.Min

    Then("it should have minimum value of meters") {
      minDistance.metersPart shouldBe MIN_VALUE
    }

    Then("it should have no nanometers") {
      minDistance.nanosPart shouldBe 0L
    }

    When("I subtract even one nanometer from it") {
      Then("it fails") {
        shouldThrow<ArithmeticException> { minDistance - Distance.create(nanometers = 1) }
      }
    }

    When("I add zero distance to it") {
      val newDistance = minDistance + Distance.Zero

      Then("it stays the same") {
        newDistance shouldBe minDistance
      }
    }

    When("I add any distance from it") {
      Then("it does not fail") {
        assertAll(DistanceGenerator(Distance.Zero)) { distance ->
          shouldNotThrowAny { minDistance + distance }
        }
      }
    }
  }

  Given("max distance") {
    val maxDistance = Distance.Max

    Then("it should have maximum value of meters") {
      maxDistance.metersPart shouldBe MAX_VALUE
    }

    Then("it should have maximum value of nanometers") {
      maxDistance.nanosPart shouldBe 999_999_999
    }

    When("I add even one nanometer to it") {
      Then("it fails") {
        shouldThrow<ArithmeticException> { maxDistance + Distance.create(nanometers = 1) }
      }
    }

    When("I add zero distance to it") {
      val newDistance = maxDistance + Distance.Zero

      Then("it stays the same") {
        newDistance shouldBe maxDistance
      }
    }

    When("I remove any distance from it") {
      Then("it does not fail") {
        assertAll(DistanceGenerator(Distance.Zero)) { distance ->
          shouldNotThrowAny { maxDistance - distance }
        }
      }
    }
  }

  Given("two distances") {
    When("they are added") {
      Then("nanometer parts should carry over for overflow") {
        assertAll(LongGenerator(0L, 999_999_999), LongGenerator(0L, 999_999_999)) { nano1, nano2 ->
          val totalNanometers = nano1 + nano2
          val shouldCarryOver = totalNanometers >= 1_000_000_000
          val distance = Distance.create(nanometers = nano1) + Distance.create(nanometers = nano2)

          if (shouldCarryOver) distance shouldBe Distance.create(1, totalNanometers - 1_000_000_000)
          else distance shouldBe Distance.create(0, totalNanometers)
        }
      }
    }

    When("they are subtracted") {
      Then("nanometer parts should carry over for overflow") {
        assertAll(LongGenerator(0L, 999_999_999), LongGenerator(0L, 999_999_999)) { nano1, nano2 ->
          val nanoDiff = nano1 - nano2
          val shouldCarryOver = nanoDiff < 0
          val distance = Distance.create(1, nano1) - Distance.create(0, nano2)

          if (shouldCarryOver) distance shouldBe Distance.create(0, 1_000_000_000 + nanoDiff)
          else distance shouldBe Distance.create(1, nanoDiff)
        }
      }
    }

    Then("they can be compared to each other") {
      forAll(
          LongGenerator(MIN_VALUE, MAX_VALUE),
          LongGenerator(0L, 999_999_999),
          LongGenerator(MIN_VALUE, MAX_VALUE),
          LongGenerator(0L, 999_999_999)
      ) { meter1, nano1, meter2, nano2 ->
        val distance1 = Distance.create(meter1, nano1)
        val distance2 = Distance.create(meter2, nano2)

        return@forAll when {
          meter1 > meter2 -> distance1 > distance2
          meter1 == meter2 && nano1 > nano2 -> distance1 > distance2
          meter1 == meter2 && nano1 == nano2 -> distance1.compareTo(distance2) == 0
          else -> distance1 < distance2
        }
      }
    }
  }

  Given("a distance") {
    When("I multiply it by a natural number") {
      Then("the result is correct") {
        assertAll(
            DistanceGenerator(Distance.ofKilometers(-1_000), Distance.ofKilometers(1_000)),
            LongGenerator(0, 500_000)
        ) { distance, multiplicant ->
          val meters = distance.exactTotalMeters * multiplicant.toBigDecimal()
          val storedMeters = meters.toBigInteger().longValueExact()
          val nanometers = (meters - storedMeters.toBigDecimal()) * 1_000_000_000.toBigDecimal()
          val expected = Distance.create(storedMeters, nanometers.toLong())

          val multipliedDistance = distance * multiplicant

          multipliedDistance shouldBe expected
        }
      }
    }

    When("I multiply it by a real number") {
      Then("the result is correct") {
        assertAll(
            DistanceGenerator(Distance.ofKilometers(-1_000), Distance.ofKilometers(1_000)),
            DoubleGenerator(0.0, 500_000.0)
        ) { distance, multiplicant ->
          val meters = distance.exactTotalMeters * multiplicant.toBigDecimal()
          val nanos = meters.movePointRight(9).toBigInteger()
          val divRem = nanos.divideAndRemainder(1_000_000_000.toBigInteger())
          check(divRem[0].bitLength() <= 63) { "Exceeded duration capacity: $nanos" }
          val expected = Distance.create(divRem[0].toLong(), divRem[1].toLong())

          val multipliedDistance = distance * multiplicant

          multipliedDistance shouldBe expected
        }
      }
    }

    When("I divide it by a natural number") {
      Then("the result is correct") {
        assertAll(
            DistanceGenerator(Distance.ofKilometers(-1_000), Distance.ofKilometers(1_000)),
            LongGenerator(1, 500_000)
        ) { distance, divisor ->
          val meters = distance.exactTotalMeters.divide(divisor.toBigDecimal(), DOWN)
          val nanos = meters.movePointRight(9).toBigIntegerExact()
          val divRem = nanos.divideAndRemainder(1_000_000_000.toBigInteger())
          check(divRem[0].bitLength() <= 63) { "Exceeded duration capacity: $nanos" }
          val expected = Distance.create(divRem[0].toLong(), divRem[1].toLong())

          val multipliedDistance = distance / divisor

          multipliedDistance shouldBe expected
        }
      }
    }

    When("I divide it by a real number") {
      Then("the result is correct") {
        assertAll(
            DistanceGenerator(Distance.ofKilometers(-1_000), Distance.ofKilometers(1_000)),
            DoubleGenerator(0.000_001, 500_000.0)
        ) { distance, divisor ->
          val meters = distance.exactTotalMeters.divide(divisor.toBigDecimal(), DOWN)
          val nanos = meters.movePointRight(9).toBigInteger()
          val divRem = nanos.divideAndRemainder(1_000_000_000.toBigInteger())
          check(divRem[0].bitLength() <= 63) { "Exceeded duration capacity: $nanos" }
          val expected = Distance.create(divRem[0].toLong(), divRem[1].toLong())

          val multipliedDistance = distance / divisor

          multipliedDistance shouldBe expected
        }
      }
    }
  }
})
