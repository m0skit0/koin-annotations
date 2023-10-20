# Koin Annotations

## This library is now obsolete and won't be maintained anymore since [Koin now already supports annotations](https://insert-koin.io/docs/reference/koin-annotations/start/).

Koin Annotations is a helper library for Koin that implements annotations to simplify the use of Koin.

## @KoinModule

Automatically adds functions that return Koin modules to listOfKoinModules() return value.

```kotlin
object Test {

    // Annotate functions that return Koin modules you want to be automatically loaded
    @KoinModule
    fun module1(): Module = module {  }

    @KoinModule
    fun module2(): Module = module {  }
}

fun main() {
    // Call startKoin or loadKoinModules passing auto-generated function listOfKoinModules() as list of modules
    startKoin {
        modules(listOfKoinModules())
    }
}
```
## Dependencies

Add the Koin Annotations JAR to your dependencies and import it, as well as Kotlin Poet.

```gradle
dependencies {
    implementation files('libs/koin-annotations-0.2.0.jar')
    kapt files('libs/koin-annotations-0.2.0.jar')
    implementation 'com.squareup:kotlinpoet:1.5.0'
}
```
