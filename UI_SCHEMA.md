# 🎨 UI Schema Documentation - Dynamic Forms

## Overview

The UI Schema system allows you to control the layout, grouping, and conditional visibility of form fields without changing your code. Simply configure the JSON!

---

## 🎯 Features Implemented

### 1. **Layout Control** 📐
Control how fields are arranged on the screen.

**Vertical Layout** (default):
```json
{
  "uiSchema": {
    "layout": "vertical"
  }
}
```

**Grid Layout** (multi-column):
```json
{
  "uiSchema": {
    "layout": "grid",
    "columns": 2,
    "spacing": 16
  }
}
```

---

### 2. **Field Grouping** 📦
Organize fields into collapsible sections.

```json
{
  "uiSchema": {
    "groups": [
      {
        "title": "Personal Information",
        "collapsible": true,
        "defaultExpanded": true,
        "fields": ["username", "email", "password", "age"]
      },
      {
        "title": "Professional Details",
        "collapsible": false,
        "fields": ["company", "position", "skills"]
      }
    ]
  }
}
```

**Features:**
- ✅ Collapsible groups with expand/collapse animation
- ✅ Visual grouping with cards
- ✅ Custom titles for each group
- ✅ Control default expanded state

---

### 3. **Conditional Visibility** 🎭
Show/hide fields based on other field values.

```json
{
  "uiSchema": {
    "rules": [
      {
        "field": "creditCard",
        "effect": "SHOW",
        "condition": {
          "field": "paymentMethod",
          "operator": "EQUALS",
          "value": "credit"
        }
      },
      {
        "field": "bankAccount",
        "effect": "HIDE",
        "condition": {
          "field": "age",
          "operator": "LESS_THAN",
          "value": 18
        }
      }
    ]
  }
}
```

**Supported Effects:**
- `SHOW` - Show field when condition is true
- `HIDE` - Hide field when condition is true
- `ENABLE` - Enable field when condition is true
- `DISABLE` - Disable field when condition is true

**Supported Operators:**
- `EQUALS` - Field value equals specified value
- `NOT_EQUALS` - Field value does not equal specified value
- `CONTAINS` - Field value contains substring
- `GREATER_THAN` - Numeric comparison
- `LESS_THAN` - Numeric comparison
- `IS_EMPTY` - Field is empty or blank
- `NOT_EMPTY` - Field has value

---

### 4. **Field Ordering** 📑
Control the order fields appear in the form.

```json
{
  "uiSchema": {
    "fieldOrder": ["email", "username", "password", "age"]
  }
}
```

---

## 📝 Complete Example

```json
{
  "type": "object",
  "properties": {
    "username": { "type": "string", "title": "Username" },
    "email": { "type": "string", "format": "email", "title": "Email" },
    "password": { "type": "string", "format": "password", "title": "Password" },
    "age": { "type": "integer", "title": "Age" },
    "country": { 
      "type": "string", 
      "enum": ["Israel", "USA", "UK"], 
      "title": "Country" 
    },
    "hasExperience": { "type": "boolean", "title": "Have Experience?" },
    "yearsExperience": { "type": "integer", "title": "Years" },
    "bio": { "type": "string", "maxLength": 500, "title": "Bio" }
  },
  "required": ["username", "email", "password"],
  
  "uiSchema": {
    "layout": "grid",
    "columns": 2,
    "spacing": 16,
    
    "groups": [
      {
        "title": "Account Information",
        "collapsible": true,
        "defaultExpanded": true,
        "fields": ["username", "email", "password"]
      },
      {
        "title": "Personal Details",
        "collapsible": true,
        "defaultExpanded": true,
        "fields": ["age", "country", "hasExperience", "yearsExperience"]
      },
      {
        "title": "About You",
        "collapsible": false,
        "fields": ["bio"]
      }
    ],
    
    "fieldOrder": [
      "username", "email", "password",
      "age", "country",
      "hasExperience", "yearsExperience",
      "bio"
    ],
    
    "rules": [
      {
        "field": "yearsExperience",
        "effect": "SHOW",
        "condition": {
          "field": "hasExperience",
          "operator": "EQUALS",
          "value": true
        }
      },
      {
        "field": "bio",
        "effect": "ENABLE",
        "condition": {
          "field": "age",
          "operator": "GREATER_THAN",
          "value": 18
        }
      }
    ]
  }
}
```
