Change Log
==========

Version 0.5.3 *(2020-06-14)*
----------------------------

* Update Kotlin to 1.3.72.
* Fix a bug with library test not running as a part of 'check' task.
* Fix a bug with long to double conversion when computing distances.

Version 0.5.2 *(2020-04-15)*
----------------------------

* Add Arabic translations.
* Fix a bug with no inches displayed for 0 length.

Version 0.5.1 *(2020-03-01)*
----------------------------

* Fix wrong unit displayed for feet.
* Remove 'totalMeters' property that approximates distance.

Version 0.5.0 *(2020-02-29)*
----------------------------

* Add support for floored formatting of any length units.
* Allow to set globally if UK should use imperial or SI units.

Version 0.4.1 *(2020-02-07)*
----------------------------

* Add support for negative distances.
* Add support for floored SI unit lengths formatting.

Version 0.4.0 *(2020-02-05)*
----------------------------

* Add Ruler class as a central point for distance and length formatting.
* And imperial unit formatter.

Version 0.3.2 *(2020-02-03)*
----------------------------

* Fix issue with high precision Float and Double multiplication.

Version 0.3.1 *(2020-02-03)*
----------------------------

* Make library JDK 7 compatible.

Version 0.3.0 *(2020-02-03)*
----------------------------

* Swap names of Distance and Length classes.
* Use Long as underlying Distance primitive. It is more suitable for application that have UI interaction and distance of ~975 light years seems sufficient for most day-to-day applications.
* Add Distance (old Length) formatter for Android.
* Add basic math operators like multiplication and division.
* Add Double factories to Distance (old Length).

Version 0.2.0 *(2020-02-03)*
----------------------------

* Make formatting API more flexible.

Version 0.1.0 *(2020-02-02)*
----------------------------

* Initial release.