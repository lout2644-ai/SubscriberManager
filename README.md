# Subscriber Manager - إدارة المشتركين

Android application source project for local/offline subscriber management.

## Main features
- Local SQLite database; no account or server required for normal use.
- Arabic RTL interface.
- Add/edit/delete subscriber.
- Subscription types: monthly, 3 months, 6 months, yearly, custom days.
- Payment/subscription date defaults to the current date for new subscribers.
- Renewal starts from the actual payment date, never from the old expiry date.
- Automatic expiry date and remaining-day calculation.
- Statuses: فعال, تنبيه, ينتهي خلال يومين, ينتهي اليوم, منتهي.
- Search by name, contact, username, or subscription type.
- Renewal history per subscriber.
- Password/new-password, old/new notes, and panel information fields.
- JSON backup containing subscribers and renewal history.
- JSON restore.
- CSV export/import compatible with spreadsheet software.
- Local notification scheduling for 7, 2, 1, and 0 days before expiry when notification permission is granted.
- Boot receiver re-schedules notifications after device restart.

## Build
Open this folder as an Android project in Android Studio or an Android build environment that supports:
- Android Gradle Plugin 8.7.3
- Gradle 8.9
- JDK 17
- Android SDK 35

The project intentionally uses Android framework widgets and SQLiteOpenHelper instead of third-party libraries, so dependency count is minimal.

## Important
The build environment used to package this source did not contain an Android SDK/Gradle installation, so an APK was not compiled here. The source project is complete and intended to be built by Android Studio/AppStudio/GitHub Codespaces.

## Package
com.twostor.subscribermanager
