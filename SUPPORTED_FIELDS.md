# 📋 Supported Field Types - Dynamic Forms

## Overview

This project supports a wide variety of field types that are automatically detected from JSON Schema.

---

## 🎯 Supported Fields

### 1. TEXT - Basic Text Input
**JSON Schema:**
```json
{
  "type": "string",
  "title": "Username",
  "minLength": 3,
  "maxLength": 20
}
```

**UI Component:** `OutlinedTextField`  
**Validation:** minLength, maxLength, pattern  
**Keyboard:** Default text  

---

### 2. NUMBER - Numeric Input
**JSON Schema:**
```json
{
  "type": "integer",
  "title": "Age",
  "minimum": 18,
  "maximum": 120
}
```

**UI Component:** `OutlinedTextField` with numeric keyboard  
**Validation:** minimum, maximum  
**Keyboard:** Number pad  

---

### 3. BOOLEAN - Checkbox
**JSON Schema:**
```json
{
  "type": "boolean",
  "title": "Subscribe to newsletter",
  "default": false
}
```

**UI Component:** `Checkbox`  
**Validation:** -  
**Default Value:** false  

---

### 4. DROPDOWN - Single Select ✅
**JSON Schema:**
```json
{
  "type": "string",
  "enum": ["Israel", "USA", "UK", "Canada"],
  "title": "Country"
}
```

**UI Component:** `ExposedDropdownMenuBox` (Material 3)  
**Validation:** must be one of enum values  
**Required:** options list  
**Features:**
- Material 3 exposed dropdown
- Touch-friendly interface
- Keyboard navigation support

---

### 5. EMAIL - Email Address ✨
**JSON Schema:**
```json
{
  "type": "string",
  "format": "email",
  "title": "Email Address"
}
```

**UI Component:** `OutlinedTextField` with email keyboard  
**Validation:** email format (NetworkNT)  
**Keyboard:** Email keyboard (@, .)  
**Auto-detect:** `format: "email"`  

---

### 6. URL - Website Address ✨
**JSON Schema:**
```json
{
  "type": "string",
  "format": "uri",
  "title": "Website"
}
```

**UI Component:** `OutlinedTextField` with URI keyboard  
**Validation:** URI format  
**Keyboard:** URI keyboard  
**Auto-detect:** `format: "uri"` or `format: "url"`  

---

### 7. PASSWORD - Password Field ✨
**JSON Schema:**
```json
{
  "type": "string",
  "format": "password",
  "title": "Password",
  "minLength": 8
}
```

**UI Component:** `OutlinedTextField` with password transformation  
**Validation:** minLength, pattern  
**Features:**  
- 👁️ Show/Hide password toggle
- Hidden text by default
- Password keyboard
- Visual transformation

**Auto-detect:** `format: "password"`  

---

### 8. DATE - Date Input ✨
**JSON Schema:**
```json
{
  "type": "string",
  "format": "date",
  "title": "Birth Date"
}
```

**UI Component:** `OutlinedTextField` (simplified)  
**Validation:** date format (YYYY-MM-DD)  
**Placeholder:** "YYYY-MM-DD"  
**Keyboard:** Numeric  
**Auto-detect:** `format: "date"`  

> 💡 **Note:** Basic implementation - can be upgraded to native DatePicker  

---

### 9. TIME - Time Input ✨
**JSON Schema:**
```json
{
  "type": "string",
  "format": "time",
  "title": "Meeting Time"
}
```

**UI Component:** `OutlinedTextField` (simplified)  
**Validation:** time format (HH:MM)  
**Placeholder:** "HH:MM"  
**Keyboard:** Numeric  
**Auto-detect:** `format: "time"`  

> 💡 **Note:** Basic implementation - can be upgraded to native TimePicker  

---

### 10. TEXTAREA - Multi-line Text ✨
**JSON Schema:**
```json
{
  "type": "string",
  "title": "Description",
  "maxLength": 500
}
```

**UI Component:** `OutlinedTextField` with multiple lines  
**Validation:** maxLength  
**Lines:** 3-6 (scrollable)  
**Keyboard:** Default text  
**Auto-detect:** `maxLength > 200`  

---

### 11. MULTISELECT - Multiple Selection ✨
**JSON Schema:**
```json
{
  "type": "array",
  "items": {
    "type": "string",
    "enum": ["React", "Angular", "Vue", "Svelte"]
  },
  "title": "Technologies"
}
```

**UI Component:** `ExposedDropdownMenuBox` with checkboxes  
**Validation:** each item must be from enum  
**Features:**
- ✅ Multiple selection
- Shows count: "3 selected"
- Checkbox per option
- Material 3 design

**Auto-detect:** `type: "array"` + `items.enum`  

---

## 🔍 Auto-detection Rules

The system automatically detects field types based on:

### Priority 1: Array Types
```
type: "array" + items.enum → MULTISELECT
```

### Priority 2: Enum
```
enum: [...] → DROPDOWN
```

### Priority 3: Format (for strings)
```
format: "email"    → EMAIL
format: "uri"      → URL
format: "password" → PASSWORD
format: "date"     → DATE
format: "time"     → TIME
```

### Priority 4: Length (for strings)
```
maxLength > 200 → TEXTAREA
else           → TEXT
```

### Priority 5: Type
```
type: "integer" / "number" → NUMBER
type: "boolean"            → BOOLEAN
type: "string"             → TEXT (default)
```

---

## 📝 Complete Schema Example

```json
{
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "title": "Username",
      "minLength": 3,
      "maxLength": 20
    },
    "email": {
      "type": "string",
      "format": "email",
      "title": "Email Address"
    },
    "password": {
      "type": "string",
      "format": "password",
      "title": "Password",
      "minLength": 8
    },
    "website": {
      "type": "string",
      "format": "uri",
      "title": "Website"
    },
    "age": {
      "type": "integer",
      "title": "Age",
      "minimum": 18,
      "maximum": 120
    },
    "birthDate": {
      "type": "string",
      "format": "date",
      "title": "Date of Birth"
    },
    "country": {
      "type": "string",
      "enum": ["Israel", "USA", "UK", "Canada", "Other"],
      "title": "Country"
    },
    "bio": {
      "type": "string",
      "title": "Biography",
      "maxLength": 500
    },
    "skills": {
      "type": "array",
      "items": {
        "type": "string",
        "enum": ["JavaScript", "Python", "Java", "Kotlin", "Go"]
      },
      "title": "Skills"
    },
    "subscribe": {
      "type": "boolean",
      "title": "Subscribe to newsletter",
      "default": false
    }
  },
  "required": ["username", "email", "password", "age"]
}
```

---

## ✅ Validation Support

All fields support:

- ✅ **required** - Mandatory fields
- ✅ **minLength / maxLength** - String length constraints
- ✅ **minimum / maximum** - Numeric range
- ✅ **pattern** - Regex validation
- ✅ **format** - email, uri, date, time
- ✅ **enum** - Valid value list
- ✅ **default** - Default value

Validation is powered by **NetworkNT JSON Schema Validator 1.3.0**

---

## 🎨 UI Features

All fields include:

- 🏷️ **Label** with required indicator (*)
- ⚠️ **Error messages** in red below the field
- ✅ **Success indicator** (green checkmark) when valid
- 🎹 **Keyboard type** optimized for each field
- 📱 **Material 3 Design** - Modern UI
- ♿ **Accessibility** - Full support

---

## 🚀 Adding New Field Types

Want to add a new field type?

### Steps:

1. **Add to FieldType enum** (`FormField.kt`)
   ```kotlin
   enum class FieldType {
       // ... existing
       YOUR_NEW_TYPE
   }
   ```

2. **Update parseType** (`SchemaParser.kt`)
   ```kotlin
   when (format) {
       "your-format" -> FieldType.YOUR_NEW_TYPE
   }
   ```

3. **Create component** (`DynamicField.kt`)
   ```kotlin
   @Composable
   private fun YourNewComponent(...) {
       // Implementation
   }
   ```

4. **Update when statement** (`DynamicField.kt`)
   ```kotlin
   when (field.type) {
       FieldType.YOUR_NEW_TYPE -> YourNewComponent(...)
   }
   ```

5. **Update this documentation!**

---

## 📊 Statistics

- **Total Supported Fields:** 11
- **Basic Fields:** 4 (TEXT, NUMBER, BOOLEAN, DROPDOWN)
- **Advanced Fields:** 7 (EMAIL, URL, PASSWORD, DATE, TIME, TEXTAREA, MULTISELECT)
- **Validation:** NetworkNT JSON Schema V7
- **UI Framework:** Jetpack Compose Material 3

---

## 🔮 Future Enhancements

Ideas for improvements:

- [ ] **Native DatePicker** (Material 3)
- [ ] **Native TimePicker** (Material 3)
- [ ] **Slider** (for numbers with min/max)
- [ ] **Radio Group** (alternative to DROPDOWN)
- [ ] **File Upload**
- [ ] **Color Picker**
- [ ] **Rich Text Editor**
- [ ] **Phone Number** with country code
- [ ] **Address** (autocomplete)
- [ ] **Tags/Chips** input
- [ ] **Star Rating**
- [ ] **Image Upload** with preview

---

## 🛠️ Implementation Details

### Code Structure

```
FormField.kt          - Data model with FieldType enum
SchemaParser.kt       - Auto-detection logic
DynamicField.kt       - UI components for each type
```

### Dependencies

- **Jetpack Compose** - Material 3 components
- **NetworkNT** - JSON Schema validation
- **Jackson** - JSON parsing
- **OkHttp** - Network requests

---

**Version:** 1.0  
**Last Updated:** January 11, 2026  
**Project:** Dynamic Forms | Tondo

