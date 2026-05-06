# Photo Spot Saver

A lightweight Android app that saves photo spots with GPS coordinates and compass direction.  
Never forget where you parked, found that great view, or spotted something interesting.

## Features

- **Quick Capture** — Tap once to take a photo + save location & compass bearing automatically
- **Live Compass** — See real-time compass direction overlay while framing your shot
- **Category Tags** — Pick a category before capture (Parking, Food, Nature, Photo Op, Travel, Work, Shopping, General)
- **Map Preview** — Each spot shows a mini OpenStreetMap tile thumbnail right in the list
- **Spot List** — Browse all saved spots with photo + map thumbnails, coordinates, and timestamps
- **Category Filter** — Filter your spots list by category using chip selectors
- **Detail View** — Full photo, wide map preview, editable note, and category changer
- **Navigation** — Tap "Navigate Here" to open Google Maps directions to any saved spot
- **Share** — Send spot coordinates and a Maps link to anyone
- **Notes** — Add/edit a text note on any spot after capture
- **Home Screen Widget** — Shows your last saved spot (photo, location, bearing) with a one-tap navigate button
- **Delete** — Tap to remove spots you no longer need

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **CameraX** for camera capture
- **FusedLocationProvider** for high-accuracy GPS
- **Accelerometer + Magnetometer** for compass bearing
- **Room** for local SQLite persistence
- **Coil** for image loading (photos + map tiles)
- **Jetpack Glance** for home screen widget
- **Navigation Compose** for screen routing
- **OpenStreetMap tiles** for map previews (no API key needed)

## Setup

1. Open the project in **Android Studio Hedgehog** or newer
2. Sync Gradle
3. Run on a physical device (camera + GPS required)
4. Long-press home screen → Widgets → "Photo Spot Saver" to add the widget

## Permissions

The app requests:
- `CAMERA` — to take photos
- `ACCESS_FINE_LOCATION` — to tag spots with GPS coordinates
- `INTERNET` — to load map preview tiles from OpenStreetMap

## Categories

| Emoji | Tag | Use case |
|-------|-----|----------|
| 📍 | General | Default catch-all |
| 🅿️ | Parking | Where you parked |
| 🍕 | Food & Drink | Restaurants, cafes, bars |
| 🌿 | Nature | Scenic views, parks, trails |
| 🛍️ | Shopping | Stores, malls |
| 📸 | Photo Op | Great photo locations |
| ✈️ | Travel | Airports, stations, landmarks |
| 💼 | Work | Office, meeting spots |

## Project Structure

```
app/src/main/java/com/spotphoto/saver/
├── data/
│   ├── PhotoSpot.kt          # Room entity + SpotCategory enum
│   ├── PhotoSpotDao.kt       # Database queries (with category filter)
│   ├── PhotoSpotDatabase.kt  # Room DB singleton + migrations
│   └── PhotoSpotRepository.kt
├── ui/
│   ├── components/
│   │   ├── MapPreview.kt         # OSM tile-based map preview
│   │   └── CategoryChips.kt     # Filter + picker chip rows
│   ├── screens/
│   │   ├── CameraScreen.kt       # Camera + compass + category selector
│   │   ├── SpotsListScreen.kt    # Saved spots with map thumbnails
│   │   ├── SpotDetailScreen.kt   # Full details + map + navigate/share
│   │   └── PermissionScreen.kt   # First-run permission request
│   └── theme/
│       └── Theme.kt              # Material 3 theming
├── util/
│   ├── LocationHelper.kt    # FusedLocation wrapper
│   └── CompassHelper.kt     # Sensor-based compass
├── widget/
│   ├── LastSpotWidget.kt     # Glance widget (last saved spot)
│   └── WidgetUpdater.kt     # Triggers widget refresh
├── Navigation.kt            # Nav graph
├── MainViewModel.kt         # App state + category filtering
├── PhotoSpotApp.kt          # Application class
└── MainActivity.kt          # Entry point
```

## License

MIT — do whatever you want with it.
