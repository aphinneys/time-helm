# Agent Development Guidelines

## Architecture

This project follows the **Functional Core, Imperative Shell** pattern:
- **Logic (Functional Core)**: Pure business logic with no side effects
- **UI (Imperative Shell)**: Composable functions that orchestrate the UI

## Package Structure

```
app/src/main/java/com/timehelm/timehelm/
├── data/           # Models and repositories
├── logic/         # Pure business logic functions
├── ui/
│   ├── components/  # Reusable UI pieces
│   ├── screens/     # High-level screen layouts
│   └── theme/       # Material3 theme configuration
└── MainActivity.kt
```

## Code Style

- **Indentation**: 4 spaces
- **Functions**: Prefer top-level functions over class members
- **Data Classes**: Use immutable data classes for models
- **Error Handling**: Use `Result` or nullable returns instead of exceptions
- **State**: Use Compose state APIs (`mutableStateOf`, `remember`, etc.)

## Testing

Every logic function must have unit tests. Run tests with:
```bash
export JAVA_HOME="/usr/lib/jvm/java-17-temurin-jdk" && ./gradlew test
```

## Formatting

This project uses Spotless with ktlint for code formatting. To apply formatting:
```bash
./gradlew spotlessApply
```

To check formatting:
```bash
./gradlew spotlessCheck
```

## Prohibited Actions

- Do NOT mix UI code with business logic
- Do NOT use `object` singletons for stateful logic
- Do NOT commit secrets, API keys, or credentials
- Do NOT add debugging code (e.g., `println`, `Log.d`) - use proper logging

## Build Commands

```bash
# Build the project
export JAVA_HOME="/usr/lib/jvm/java-17-temurin-jdk" && ./gradlew build

# Run tests
export JAVA_HOME="/usr/lib/jvm/java-17-temurin-jdk" && ./gradlew test

# Apply formatting
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck
```
