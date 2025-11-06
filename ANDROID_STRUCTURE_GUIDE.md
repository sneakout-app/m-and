# Android/Kotlin Project Structure Guide

## Overview

This is a standard Android project using **Jetpack Compose** and **Kotlin**. Here's how the project is organized:

## Directory Structure

```
app/src/main/java/com/example/openfoodmaps/
├── MainActivity.kt           # Entry point of your app
├── data/                      # Data layer
│   ├── model/                 # Your ORM objects/data models
│   │   ├── User.kt
│   │   ├── Restaurant.kt
│   │   ├── Review.kt
│   │   └── Relations.kt       # Models for joined relationships
│   ├── repository/            # Data access layer (create later)
│   │   └── (repositories for fetching/storing data)
│   └── local/                 # Local database (if using Room)
│       └── (Room entities, DAOs)
├── domain/                    # Business logic layer (optional but recommended)
│   ├── usecase/               # Use cases (business operations)
│   └── (domain models if different from data models)
├── ui/                        # UI layer
│   ├── screens/               # Composable screens
│   ├── components/            # Reusable UI components
│   └── theme/                 # App theming
└── util/                      # Utility classes
    └── (helpers, extensions, constants)
```

## Key Concepts

### 1. **Package Structure**
- Packages in Kotlin organize related code
- Convention: Use reverse domain notation (`com.example.openfoodmaps`)
- Each package typically represents a layer or feature

### 2. **Data Models (ORM Objects)**
- **Location**: `data/model/`
- These are your database entity representations
- Use `data class` for immutable models
- Use nullable types (`String?`) for optional fields
- These map directly to your Supabase tables

### 3. **Android Architecture Layers**

#### **Presentation Layer** (`ui/`)
- Composable functions (Jetpack Compose)
- ViewModels (if using MVVM pattern)
- UI state management

#### **Data Layer** (`data/`)
- Models: Your ORM objects
- Repository: Handles data fetching (API, local DB, etc.)
- Local: Room database (if using local storage)

#### **Domain Layer** (`domain/`) - Optional but recommended
- Business logic
- Use cases
- Domain models (if different from data models)

### 4. **Kotlin Basics for Android**

#### Data Classes
```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String? = null  // Nullable with default value
)
```
- `data class` automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`
- `val` = immutable (read-only)
- `var` = mutable (read-write)
- `?` = nullable type

#### Null Safety
- Kotlin has built-in null safety
- `String?` means the value can be null
- `String` means the value cannot be null
- Use `?.` for safe calls: `user?.name`
- Use `!!` only when you're certain it's not null (generally avoid)

### 5. **Next Steps for Supabase Integration**

When you're ready to connect Supabase:

1. **Add Supabase dependencies** to `app/build.gradle.kts`
2. **Create repositories** in `data/repository/` that:
   - Make Supabase API calls
   - Convert Supabase responses to your models
   - Handle errors
3. **Create ViewModels** (if using MVVM) that:
   - Use repositories to fetch data
   - Hold UI state
   - Expose data to Composables

## Common Patterns

### Model Relationships
- Use foreign keys as IDs (e.g., `userId: String`)
- Create relation models for joined queries (see `Relations.kt`)

### Package Organization
- Group by feature OR by layer (this guide uses layer-based)
- Be consistent across your project

### File Naming
- Use PascalCase for classes: `User`, `Restaurant`, `Review`
- Use descriptive names: `RestaurantWithReviews` not `RestaurantR`

## Resources

- [Kotlin Language Docs](https://kotlinlang.org/docs/home.html)
- [Android Developer Guide](https://developer.android.com/)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Supabase Kotlin Docs](https://supabase.com/docs/reference/kotlin/introduction)




