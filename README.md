# Habit Tracker with Kotlin Multiplatform

## Overview

As a practical study in Kotlin (And MVVM design pattern) I am trying to make an Habit Tracker app with built with Jetpack Compose/Kotlin Multiplatform. The app will allow users to add/remove update, and in general track, personal habits/behavior. I specifically started it as a way to track my own alcoholism I guess (along with positive habits such as exercise and language study). It is initial a clone of the JetBrains' multiplatform Compose repository template, which you can find here: https://github.com/JetBrains/compose-multiplatform-ios-android-template).

## Structure 
- the main custom logic is found in:
```
  shared
  └─ src
  └─ commonMain
     └─ kotlin
        ├─ datasources
        │  └─ HabitLocalDataSource.kt
        └─ mvvm
           ├─ AppView.kt
           ├─ MainModel
           └─ MainViewModel
```

## Features
- **Custom Habit Tracking**: ADD, REMOVE, and UPDATE custom habits given a 5 day history of statuses
- **Local Persistence**: abits and status tracking are stored in a local db on the device (android location: ```~/data/data/com.myapplication.MyApplication/databases```)

## Setup/gradle dependencies
- clone the repository and open it in Android Studio
- uses the `kotlin("multiplatform")` plugin to enable multiplatform (thus requires configuration in Android Studio)
- dependencies are declared in `commonMain`, and platform-specific dependencies are in `androidMain`.
- still working on test module's android independence, but having a hard time getting around not using JUnit


## Database
- uses sqldelight multiplatform for persisting Habits and Tracking data
- simple RDB structure
Habit table definition:
```
CREATE TABLE Habit (
    HabitID INTEGER PRIMARY KEY,
    Name TEXT NOT NULL UNIQUE
);
```
example:
| HabitID | Name     |
|---------|----------|
| 1       | Jog      |
| 2       | Drink    |
| 3       | Read     |

Tracking table definition
```
CREATE TABLE Tracking (
    TrackingID INTEGER PRIMARY KEY,
    HabitID INTEGER NOT NULL,
    Date INTEGER NOT NULL,
    UNIQUE(HabitID, Date),
    FOREIGN KEY (HabitID) REFERENCES Habit(HabitID)
);
```

example:
| TrackingID | HabitID | Date       |
|------------|---------|------------|
| 1          | 1       | 2023-11-01 |
| 2          | 2       | 2023-11-01 |
| 3          | 1       | 2023-11-02 |


## Testing Considerations

- **Unit Testing**: only Model logic should be test (perhaps using JUnit, otherwise find kotlin specific framework).
- **Integration Testing**: maybe add database persistence test cases just as practice, other wise unit/mock tests only
- **UI Testing**: Compose testing library might be employed for UI testing? 
- **Multiplatform Testing**: find out if there is some form of multiplatform testing feature that can be used for platform-specific logic/performance.

## License

MIT License - see [LICENSE.md](LICENSE.md) for details.
