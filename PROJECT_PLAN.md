# 🎯 תכנון פרויקט - Dynamic Forms | Tondo

## 📱 סקירה כללית

אפליקציית Android שמציגה טופס דינמי מתוך JSON Schema, מבצעת ולידציות אוטומטיות, ושולחת את הנתונים לשרת.

---

## 🎨 עיצוב ומיתוג

### לוגו Tondo
- ✅ צירפת את הלוגו של החברה
- נשתמש בו במסך הפתיחה
- צבעי המותג: ורוד-סגול-כחול (gradient מהלוגו)
- עיצוב מודרני, נקי, מינימליסטי
- אנימציות קלילות ועדינות

### UI/UX Principles
- **Material Design 3** - עיצוב מודרני
- **Micro-animations** - תנועות עדינות וטבעיות
- **Clear feedback** - משוב ברור למשתמש
- **Accessibility** - נגישות מלאה

---

## 🏗️ ארכיטכטורה

### MVVM + Clean Architecture

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  ┌───────────┐  ┌──────────────────┐   │
│  │  Screens  │  │  ViewModels      │   │
│  │ (Compose) │◄─┤  (State Mgmt)    │   │
│  └───────────┘  └──────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│            Domain Layer                 │
│  ┌──────────────────────────────────┐  │
│  │  Validation Engine               │  │
│  │  (NetworkNT JSON Schema)         │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│             Data Layer                  │
│  ┌─────────────┐  ┌─────────────────┐  │
│  │ Repository  │  │  Data Sources   │  │
│  │             │  ├─────────────────┤  │
│  │             │◄─┤ Remote (API)    │  │
│  │             │  │ Local (Assets)  │  │
│  └─────────────┘  └─────────────────┘  │
└─────────────────────────────────────────┘
```

---

## 📂 מבנה תיקיות

```
app/src/main/
├── java/com/example/dynamic_forms_project/
│   ├── data/
│   │   ├── models/
│   │   │   ├── JsonSchema.kt          # JSON Schema data classes
│   │   │   ├── SchemaProperty.kt      # Property definitions
│   │   │   └── FormField.kt           # Field representation
│   │   ├── repository/
│   │   │   └── SchemaRepository.kt    # Data source management
│   │   └── remote/
│   │       └── ApiService.kt          # Retrofit API interface
│   │
│   ├── domain/
│   │   ├── validation/
│   │   │   ├── SchemaValidator.kt     # Schema validation (NetworkNT)
│   │   │   └── DataValidator.kt       # Data validation (NetworkNT)
│   │   └── usecase/
│   │       └── LoadSchemaUseCase.kt   # Business logic
│   │
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── WelcomeScreen.kt       # מסך פתיחה
│   │   │   ├── FormScreen.kt          # מסך הטופס
│   │   │   └── SuccessScreen.kt       # מסך הצלחה
│   │   ├── components/
│   │   │   ├── fields/
│   │   │   │   ├── DynamicTextField.kt
│   │   │   │   ├── DynamicNumberField.kt
│   │   │   │   ├── DynamicCheckbox.kt
│   │   │   │   └── DynamicDropdown.kt
│   │   │   ├── ErrorText.kt           # הצגת שגיאות
│   │   │   ├── LoadingButton.kt       # כפתור עם אנימציה
│   │   │   └── JsonViewer.kt          # הצגת JSON
│   │   ├── navigation/
│   │   │   └── NavGraph.kt            # Compose Navigation
│   │   ├── theme/
│   │   │   ├── Color.kt               # צבעי Tondo
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   └── viewmodel/
│   │       └── FormViewModel.kt       # State management
│   │
│   ├── utils/
│   │   ├── NetworkUtils.kt
│   │   └── JsonUtils.kt
│   │
│   └── MainActivity.kt
│
├── res/
│   ├── drawable/
│   │   └── tondo_logo.png             # 🎨 הלוגו של החברה
│   ├── values/
│   │   ├── colors.xml                 # צבעים מהלוגו
│   │   ├── strings.xml
│   │   └── themes.xml
│   └── raw/
│       └── fallback_schema.json       # Schema מקומי לfallback
│
└── AndroidManifest.xml
```

---

## 🌊 זרימת המשתמש (User Flow)

### Screen 1: Welcome Screen
```
┌────────────────────────────────┐
│                                │
│     [Tondo Logo]               │
│                                │
│  Dynamic Form Builder          │
│                                │
│  ┌──────────────────────────┐ │
│  │    🔄 Load Form          │ │
│  └──────────────────────────┘ │
│                                │
└────────────────────────────────┘
```

**אינטראקציה:**
- לחיצה על "Load Form" → טעינה מ-API
- אנימציית loading
- אם API נכשל → Fallback לקובץ מקומי
- מעבר לForm Screen

---

### Screen 2: Form Screen
```
┌────────────────────────────────┐
│ [<] Dynamic Form      [📄] [📋]│ ← Icons לSchema & Payload
├────────────────────────────────┤
│                                │
│  Username *                    │
│  ┌──────────────────────────┐ │
│  │ Enter username...        │ │
│  └──────────────────────────┘ │
│  ⚠️ Minimum 3 characters       │ ← Real-time error
│                                │
│  Age *                         │
│  ┌──────────────────────────┐ │
│  │ 25                       │ │
│  └──────────────────────────┘ │
│  ✅                            │ ← Valid indicator
│                                │
│  Email *                       │
│  ┌──────────────────────────┐ │
│  │ user@example.com         │ │
│  └──────────────────────────┘ │
│                                │
│  ☐ Subscribe to newsletter    │
│                                │
│  ┌──────────────────────────┐ │
│  │    ✓ Submit              │ │ ← Enabled only if valid
│  └──────────────────────────┘ │
└────────────────────────────────┘
```

**פיצ'רים:**
- ✅ Real-time validation (תוך כדי הקלדה)
- ✅ שגיאות בצבע אדום מתחת לשדה
- ✅ אייקון V ירוק כשתקין
- ✅ כפתור Submit מושבת עד שהכל תקין
- ✅ אייקון 📄 - הצגת Schema ב-Dialog
- ✅ אייקון 📋 - הצגת Payload נוכחי ב-Dialog

---

### Screen 3: Success Screen
```
┌────────────────────────────────┐
│                                │
│         ✅                      │
│                                │
│    Form Submitted!             │
│                                │
│  Your data has been sent       │
│  successfully                  │
│                                │
│  ┌──────────────────────────┐ │
│  │  View Payload            │ │
│  └──────────────────────────┘ │
│                                │
│  ┌──────────────────────────┐ │
│  │  Submit Another Form     │ │
│  └──────────────────────────┘ │
│                                │
└────────────────────────────────┘
```

**אינטראקציה:**
- אנימציית checkmark מופיעה
- הצגת הPayload בקוד JSON מסודר
- אפשרות לחזור לטופס חדש

---

## 🎨 אנימציות

### 1. Welcome Screen
```kotlin
// Fade in של הלוגו
logo.animate()
    .alpha(1f)
    .setDuration(800)
    
// Slide up של הכפתור
button.animate()
    .translationY(0f)
    .alpha(1f)
    .setDuration(600)
```

### 2. Form Screen
```kotlin
// Shimmer effect תוך כדי טעינה
// Error shake animation כשיש שגיאה
// Success ripple כשהשדה תקין
```

### 3. Submit Button
```kotlin
// Loading spinner על הכפתור
// Scale animation בלחיצה
// Success checkmark animation
```

### 4. Success Screen
```kotlin
// Confetti animation (קלה)
// Checkmark scale animation
// Fade in של הטקסט
```

---

## 🎨 Theme & Colors (מהלוגו)

```kotlin
// צבעים מלוגו Tondo
val TondoPink = Color(0xFFE91E63)
val TondoPurple = Color(0xFF9C27B0)
val TondoBlue = Color(0xFF3F51B5)

val TondoGradient = Brush.horizontalGradient(
    colors = listOf(TondoPink, TondoPurple, TondoBlue)
)

// Material 3 Theme
lightColorScheme(
    primary = TondoPurple,
    secondary = TondoPink,
    tertiary = TondoBlue,
    // ...
)
```

---

## 📡 API Integration

### Endpoint
```
GET https://shonhost.co.il/getFormScheme
```

### Response (Expected)
```json
{
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "minLength": 3,
      "maxLength": 20,
      "title": "Username"
    },
    "age": {
      "type": "integer",
      "minimum": 18,
      "maximum": 120,
      "title": "Age"
    },
    "email": {
      "type": "string",
      "format": "email",
      "title": "Email Address"
    },
    "subscribe": {
      "type": "boolean",
      "title": "Subscribe to newsletter",
      "default": false
    }
  },
  "required": ["username", "age", "email"]
}
```

### Fallback Schema
```
assets/fallback_schema.json
```
- אותו מבנה כמו ה-API
- נטען אוטומטית אם ה-API נכשל
- מאפשר עבודה offline

---

## 🔧 Dependencies

### build.gradle.kts (Project)
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
}
```

### build.gradle.kts (Module: app)
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // JSON Schema Validator ⭐
    implementation("com.networknt:json-schema-validator:1.5.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    
    // Kotlinx Serialization (לפרסור שלנו)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coil (לטעינת הלוגו)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Timber (Logging)
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

---

## 📋 State Management

### FormUiState
```kotlin
sealed class FormUiState {
    object Initial : FormUiState()
    object Loading : FormUiState()
    
    data class FormReady(
        val schema: JsonSchemaModel,
        val formData: Map<String, Any?>,
        val errors: Map<String, String>,
        val isValid: Boolean
    ) : FormUiState()
    
    data class Submitting(
        val schema: JsonSchemaModel,
        val formData: Map<String, Any?>,
        val progress: Float  // לאנימציה
    ) : FormUiState()
    
    data class Success(
        val payload: String  // JSON final
    ) : FormUiState()
    
    data class Error(
        val message: String
    ) : FormUiState()
}
```

---

## 🎯 Field Types Support

### בשלב ראשון (Level 1):
1. ✅ **String** - TextField רגיל
2. ✅ **Integer/Number** - TextField עם מקלדת מספרים
3. ✅ **Boolean** - Checkbox
4. ✅ **Enum (String)** - Dropdown/Select

### Validation Support:
- ✅ `required` - שדה חובה
- ✅ `minLength` / `maxLength` - אורך מחרוזת
- ✅ `minimum` / `maximum` - טווח מספרים
- ✅ `pattern` - Regex
- ✅ `format` - email, uri, date, etc.

---

## 🎨 UI Components Design

### 1. DynamicTextField
```
┌────────────────────────────────┐
│ Username *                     │ ← Label + Required indicator
├────────────────────────────────┤
│ Enter your username...         │ ← Placeholder
│ john_doe█                      │ ← Value
└────────────────────────────────┘
⚠️ Minimum 3 characters           ← Error message (red)
```

### 2. DynamicNumberField
```
┌────────────────────────────────┐
│ Age *                          │
├────────────────────────────────┤
│ 25                             │ ← Numeric keyboard
└────────────────────────────────┘
✅ Valid                          ← Success indicator (green)
```

### 3. DynamicCheckbox
```
☐ Subscribe to newsletter        ← Unchecked
☑ Subscribe to newsletter        ← Checked
```

### 4. DynamicDropdown
```
┌────────────────────────────────┐
│ Country *                      │
├────────────────────────────────┤
│ Israel                      ▼  │ ← Selected value
└────────────────────────────────┘
```

---

## 🚀 סדר ביצוע המשימות

### Phase 1: Foundation 
1. ✅ Setup dependencies
2. ✅ Create data models
3. ✅ Setup Retrofit API
4. ✅ Create SchemaRepository with fallback
5. ✅ Integrate NetworkNT validator

### Phase 2: ViewModels & Logic 
6. ✅ Create FormViewModel
7. ✅ Implement state management
8. ✅ Connect validation engine

### Phase 3: UI Components
9. ✅ Create theme with Tondo colors
10. ✅ Build field components
11. ✅ Build error display component
12. ✅ Build loading button

### Phase 4: Screens 
13. ✅ WelcomeScreen with logo
14. ✅ FormScreen with fields
15. ✅ SuccessScreen with payload
16. ✅ Setup navigation

### Phase 5: Polish
17. ✅ Add animations
18. ✅ Add Schema viewer dialog
19. ✅ Add Payload viewer dialog
20. ✅ Testing

### Phase 6: Documentation
21. ✅ Write README
22. ✅ Add code comments


---

## 📝 Example Schema (assets/fallback_schema.json)

```json
{
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "minLength": 3,
      "maxLength": 20,
      "title": "Username",
      "description": "Your unique username"
    },
    "age": {
      "type": "integer",
      "minimum": 18,
      "maximum": 120,
      "title": "Age"
    },
    "email": {
      "type": "string",
      "format": "email",
      "title": "Email Address"
    },
    "country": {
      "type": "string",
      "enum": ["Israel", "USA", "UK", "Canada", "Other"],
      "title": "Country"
    },
    "subscribe": {
      "type": "boolean",
      "title": "Subscribe to newsletter",
      "default": false
    }
  },
  "required": ["username", "age", "email"]
}
```

---

## 🎯 Success Criteria

### Functional Requirements
- ✅ טעינה מ-API עם fallback
- ✅ רינדור דינמי של שדות
- ✅ ולידציה real-time
- ✅ הצגת שגיאות ברורה
- ✅ Submit רק כשתקין
- ✅ הצגת Schema והPayload

### Non-Functional Requirements
- ✅ עיצוב מודרני ונקי
- ✅ אנימציות חלקות
- ✅ ביצועים טובים
- ✅ קוד נקי וקריא
- ✅ MVVM מובנה היטב

### Bonus Points
- ✅ שימוש בלוגו Tondo
- ✅ צבעים מותאמים למותג
- ✅ אנימציות עדינות
- ✅ UX מעולה

---

## 🔍 Testing Strategy

### Manual Testing
1. ✅ טעינה מAPI (סימולציה)
2. ✅ Fallback כשAPI נכשל
3. ✅ Validation של כל סוג שדה
4. ✅ Required fields
5. ✅ Submit flow מלא
6. ✅ Schema viewer
7. ✅ Payload viewer

### Edge Cases
- ❌ API timeout
- ❌ Schema לא תקין
- ❌ ערכים חריגים
- ❌ שדות ריקים
- ❌ רשת לא זמינה

