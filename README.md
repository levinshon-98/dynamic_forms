# 🎯 Dynamic Forms - Tondo

Android app that renders dynamic forms from JSON Schema with real-time validation.

## 📱 Features

- ✅ **Dynamic Form Rendering** - Generates UI from JSON Schema
- ✅ **Real-time Validation** - Uses NetworkNT JSON Schema Validator
- ✅ **Multiple Field Types** - Text, Number, Boolean, Dropdown
- ✅ **API + Fallback** - Loads schema from server, falls back to local
- ✅ **Modern UI** - Jetpack Compose with Material 3
- ✅ **Smooth Animations** - Loading states, transitions
- ✅ **Schema Viewer** - View raw JSON Schema
- ✅ **Payload Preview** - See form data in real-time

## 🏗️ Architecture

```
MVVM + Clean Architecture
├── data/           # Repository, Models, Parser
├── ui/
│   ├── screens/    # Welcome, Form, Success
│   ├── components/ # Reusable UI components
│   ├── theme/      # Tondo brand colors
│   └── viewmodel/  # State management
```

## 🚀 How to Run

1. Open project in Android Studio
2. Sync Gradle
3. Run on emulator or device (API 24+)

```bash
cd dynamic_forms_project
./gradlew assembleDebug
```

## 📡 API

**Endpoint:** `https://shonhost.co.il/getFormScheme`

Falls back to `assets/fallback_schema.json` if API fails.

## 🔧 Tech Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Language |
| Jetpack Compose | UI Framework |
| Material 3 | Design System |
| NetworkNT JSON Schema | Validation |
| Retrofit/OkHttp | Networking |
| Coroutines | Async |
| Navigation Compose | Navigation |

## 📋 Supported Validations

- `required` - Required fields
- `minLength` / `maxLength` - String length
- `minimum` / `maximum` - Number range
- `format` - email, uri, etc.
- `enum` - Dropdown options

## 🎨 Screenshots

```
[Welcome] → [Form] → [Success]
   │           │         │
 Logo      Validation  Payload
 Load      Real-time   Display
```

## 🔮 Future Improvements

1. **UI Schema Support** - Custom layouts (Horizontal, Groups)
2. **Rules & Conditions** - Show/Hide fields conditionally
3. **More Field Types** - Date picker, File upload
4. **Offline Mode** - Cache schemas locally
5. **Unit Tests** - Comprehensive test coverage

## 📝 Author

Built for Tondo - Take-Home Assignment

