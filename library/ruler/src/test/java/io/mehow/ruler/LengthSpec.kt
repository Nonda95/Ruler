package io.mehow.ruler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import io.mehow.ruler.ImperialLengthUnit.Foot
import io.mehow.ruler.ImperialLengthUnit.Inch
import io.mehow.ruler.ImperialLengthUnit.Mile
import io.mehow.ruler.ImperialLengthUnit.Yard
import io.mehow.ruler.SiLengthUnit.Gigameter
import io.mehow.ruler.SiLengthUnit.Kilometer
import io.mehow.ruler.SiLengthUnit.Megameter
import io.mehow.ruler.SiLengthUnit.Meter
import io.mehow.ruler.SiLengthUnit.Micrometer
import io.mehow.ruler.SiLengthUnit.Millimeter
import io.mehow.ruler.SiLengthUnit.Nanometer
import kotlin.Long.Companion.MAX_VALUE
import kotlin.Long.Companion.MIN_VALUE

class LengthSpec : BehaviorSpec({
  Given("two lengths") {
    When("they are added") {
      Then("unit of the left operand is preserved") {
        checkAll(
            DistanceGenerator(
                min = Distance.create(MIN_VALUE / 2 + 1, 500_000_000),
                max = Distance.create(MAX_VALUE / 2 - 1, 500_000_000)
            ),
            DistanceUnitGenerator,
            DistanceGenerator(
                min = Distance.create(MIN_VALUE / 2 + 1, 500_000_000),
                max = Distance.create(MAX_VALUE / 2 - 1, 500_000_000)
            ),
            DistanceUnitGenerator
        ) { distance1, unit1, distance2, unit2 ->
          val length1 = DistanceUnitGenerator.createLength(distance1, unit1)
          val length2 = DistanceUnitGenerator.createLength(distance2, unit2)

          val newDistance = length1 + length2

          newDistance.unit shouldBe length1.unit
        }
      }
    }

    When("they are subtracted") {
      Then("unit of the left operand is preserved") {
        checkAll(
            DistanceGenerator(
                min = Distance.ofMeters(MIN_VALUE / 2 + 1),
                max = Distance.ofMeters(MAX_VALUE / 2 - 1)
            ),
            DistanceUnitGenerator,
            DistanceGenerator(
                min = Distance.ofMeters(MIN_VALUE / 2 + 1),
                max = Distance.ofMeters(MAX_VALUE / 2 - 1)
            ),
            DistanceUnitGenerator
        ) { distance1, unit1, distance2, unit2 ->
          val length1 = DistanceUnitGenerator.createLength(distance1, unit1)
          val length2 = DistanceUnitGenerator.createLength(distance2, unit2)

          val newDistance = length1 - length2

          newDistance.unit shouldBe length1.unit
        }
      }
    }
  }

  checkSiUnit(Nanometer)

  checkSiUnit(Micrometer)

  checkSiUnit(Millimeter)

  checkSiUnit(Meter)

  checkSiUnit(Kilometer)

  checkSiUnit(Megameter)

  checkSiUnit(Gigameter)

  checkImperialUnit(Inch, 11)

  checkImperialUnit(Foot, 2)

  checkImperialUnit(Yard, 1759)

  checkImperialUnit(Mile, 1_000_000)
})

private fun BehaviorSpec.checkSiUnit(unit: SiLengthUnit) {
  Given("$unit distance") {
    When("I auto unit it") {
      Then("it uses $unit as a unit") {
        checkAll(DistanceGenerator(Distance.of(1, unit), Distance.of(999, unit))) { distance ->
          val length = distance.toLength(SiLengthUnit.values().random()).withAutoUnit()

          length.unit shouldBeSameInstanceAs unit
        }

        checkAll(DistanceGenerator(Distance.of(-999, unit), Distance.of(-1, unit))) { distance ->
          val length = distance.toLength(SiLengthUnit.values().random()).withAutoUnit()

          length.unit shouldBeSameInstanceAs unit
        }
      }
    }

    When("I get measured distance") {
      Then("It converts original distance correctly") {
        checkAll(Arb.long(min = -1_000L, max = 1_000L)) { value ->
          val distance = Distance.of(value, unit)
          val length = distance.toLength(unit)

          length.measure shouldBeEqualComparingTo value.toBigDecimal()
        }
      }
    }

    Then("I can coerce length in range") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        SiLengthUnit.values().filter { it > unit && it < Gigameter }.forEach {
          length.coerceUnitIn(it..Gigameter).unit shouldBeSameInstanceAs it
        }
      }
    }

    Then("I can coerce length in min and max") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        SiLengthUnit.values().filter { it > unit && it < Gigameter }.forEach {
          length.coerceUnitIn(it, Gigameter).unit shouldBeSameInstanceAs it
        }
      }
    }

    Then("I can coerce length to min") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        SiLengthUnit.values().filter { it > unit }.forEach {
          length.coerceUnitAtLeastTo(it).unit shouldBeSameInstanceAs it
        }

        SiLengthUnit.values().filter { it <= unit }.forEach {
          length.coerceUnitAtLeastTo(it).unit shouldBeSameInstanceAs unit
        }
      }
    }

    Then("I can coerce length to max") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        SiLengthUnit.values().filter { it < unit }.forEach {
          length.coerceUnitAtMostTo(it).unit shouldBeSameInstanceAs it
        }

        SiLengthUnit.values().filter { it >= unit }.forEach {
          length.coerceUnitAtMostTo(it).unit shouldBeSameInstanceAs unit
        }
      }
    }
  }
}

private fun BehaviorSpec.checkImperialUnit(
  unit: ImperialLengthUnit,
  maxRange: Long,
) {
  Given("$unit distance") {
    When("I auto unit it") {
      Then("it uses $unit as a unit") {
        checkAll(
            DistanceGenerator(Distance.of(1, unit), Distance.of(maxRange, unit))
        ) { distance ->
          val length = distance.toLength(ImperialLengthUnit.values().random()).withAutoUnit()

          length.unit shouldBeSameInstanceAs unit
        }

        checkAll(
            DistanceGenerator(Distance.of(-maxRange, unit), Distance.of(-1, unit))
        ) { distance ->
          val length = distance.toLength(ImperialLengthUnit.values().random()).withAutoUnit()

          length.unit shouldBeSameInstanceAs unit
        }
      }
    }

    When("I get measured distance") {
      Then("It converts original distance correctly") {
        checkAll(Arb.long(min = -1_000L, max = 1_000L)) { value ->
          val distance = Distance.of(value, unit)
          val length = distance.toLength(unit)

          length.measure shouldBeEqualComparingTo value.toBigDecimal()
        }
      }
    }

    Then("I can coerce length in range") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        ImperialLengthUnit.values().filter { it > unit && it < Mile }.forEach {
          length.coerceUnitIn(it..Mile).unit shouldBeSameInstanceAs it
        }
      }
    }

    Then("I can coerce length in min and max") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        ImperialLengthUnit.values().filter { it > unit && it < Mile }.forEach {
          length.coerceUnitIn(it, Mile).unit shouldBeSameInstanceAs it
        }
      }
    }

    Then("I can coerce length to min") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        ImperialLengthUnit.values().filter { it > unit }.forEach {
          length.coerceUnitAtLeastTo(it).unit shouldBeSameInstanceAs it
        }

        ImperialLengthUnit.values().filter { it <= unit }.forEach {
          length.coerceUnitAtLeastTo(it).unit shouldBeSameInstanceAs unit
        }
      }
    }

    Then("I can coerce length to max") {
      checkAll(DistanceGenerator()) { distance ->
        val length = distance.toLength(unit)

        ImperialLengthUnit.values().filter { it < unit }.forEach {
          length.coerceUnitAtMostTo(it).unit shouldBeSameInstanceAs it
        }

        ImperialLengthUnit.values().filter { it >= unit }.forEach {
          length.coerceUnitAtMostTo(it).unit shouldBeSameInstanceAs unit
        }
      }
    }
  }
}
