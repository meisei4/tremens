# Habit Tracker with Compose Multiplatform

## Overview

As a practical study in Kotlin I am trying to make a Habit Tracker app built with Jetpack Compose/Kotlin Multiplatform. The app will allow users to add/remove, track, and visualize personal habits/behavior. I specifically started it as a way to track my own alcoholism I guess (along with positive habits such as exercise and language study). It is a clone of the JetBrains' multiplatform Compose repository template, which you can find here: https://github.com/JetBrains/compose-multiplatform-ios-android-template).

## Structure 
- all custom code so far is primarily in the shared -> commonMain directory
- still not sure how to organize auxiliary/business logic or test module

## Gradle info stuff

- Uses the `kotlin("multiplatform")` plugin to enable multiplatform capabilities (might have to also configure in Android Studio?)
- Android-specific configurations are handled by `com.android.library`.
- Jetpack Compose for multiplatform is integrated using `org.jetbrains.compose`.
- The iOS targets (`iosX64`, `iosArm64`, `iosSimulatorArm64`) are defined with static binaries for better performance.
- Dependencies are declared in `commonMain`, and platform-specific dependencies are in `androidMain`.


## Testing Considerations

- **Unit Testing**: Core functions should be tested using JUnit.
- **UI Testing**: Compose testing library might be employed for UI testing?
- **Multiplatform Testing**: find out if there is some form of multiplatform testing feature that can be used for platform-specific logic/performance.

## License

MIT License - see [LICENSE.md](LICENSE.md) for details.
