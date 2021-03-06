# Ruler

![Ruler](images/ruler_logo.svg){: style="width:400px" }

Distance measurements library for Kotlin and Android.

## TLDR

Define a distance and manipulate it.

```kotlin
// Distance is dimensionless.
val distanceFromMeters: Distance = Distance.ofMeters(100)
val distanceFromYards: Distance = Distance.ofYards(50)

// Length has a unit attached to it
val metersLength: Length<SiLengthUnit.Meter> = distanceFromMeters.toLength(SiLengthUnit.Meter)
val inchesLength: Length<ImperialLengthUnit.Inch> = distanceFromMeters.toLength(ImperialLengthUnit.Inch)

// metersLength and inchesLength represent the same distance but with a different units attached to them
check(metersLength - inchesLength == Length.ofMeters(0))
```

Android artifact allows to show a user formatted distances and lengths using appropriate Locale if available.

```kotlin
fun main(context: Context) {
  val distance = Distance.ofMeters(100)
  val length = distance.toLength(Meter)

  // Assumes en_US Locale on a device.

  // Prints "109yd 1ft 1in"
  val humanReadableDistance: String = distance.format(context)

  // Prints "109yd 1ft 1in"
  val humanReadableLength: String = length.format(context)
}
```

## Requirements

Laboratory requires [Java 8 bytecode](https://developer.android.com/studio/write/java8-support) support. You can enable it with the following configuration in a `build.gradle` file.

```groovy
android {
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}
```

Also, you have to enable [default methods generation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-default/) by Kotlin compiler. You can do this by adding a compiler flag in a `build.gradle` file.

```groovy
android {
  kotlinOptions {
    freeCompilerArgs += "-Xjvm-default=enable"
  }
}
```

## Get Ruler

Ruler is published to [Maven Central Repository](https://search.maven.org/search?q=io.mehow.ruler).

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation "io.mehow.ruler:ruler:0.6.0"
}
```

Snapshots of the development version are available on [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/io/mehow/ruler/).

Here is the list of all available artifacts that Laboratory library provides.

- **`io.mehow.ruler:ruler:0.6.0`**: Core of the library. Defines classes and interfaces that represent distances.
- **`io.mehow.ruler:ruler-android:0.6.0`**: Provides distance formatters for Android platform that can present them to a user in a meaningful way.

## Attribution

Logo icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [www.flaticon.com](https://www.flaticon.com/).

## License

    Copyright 2020 Michał Sikora

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
