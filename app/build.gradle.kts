// Подключение базовых плагинов для Android-приложения и поддержки Kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Основная конфигурация Android-проекта
android {
    namespace = "com.example.passmanager" // Идентификатор пакета для ресурсов и R-класса
    compileSdk = 35 // Версия SDK, используемая для компиляции приложения

    // Параметры приложения по умолчанию
    defaultConfig {
        applicationId = "com.example.passmanager" // Уникальный идентификатор приложения в Google Play
        minSdk = 24 // Минимальная поддерживаемая версия ОС (Android 7.0)
        targetSdk = 35 // Целевая версия SDK, под которую оптимизировано приложение
        versionCode = 1 // Внутренний номер версии для обновлений
        versionName = "1.0" // Публичная версия приложения

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Конфигурация типов сборки (отладка/релиз)
    buildTypes {
        release {
            isMinifyEnabled = false // Отключение обфускации кода для упрощения отладки текущей версии
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Включение специфических функций Android Gradle Plugin
    buildFeatures {
        viewBinding = true // Активация типобезопасного доступа к View-элементам
    }

    // Настройка совместимости Java-компилятора
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Настройка параметров компилятора Kotlin
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Управление внешними зависимостями и библиотеками
dependencies {
    implementation("androidx.core:core-ktx:1.15.0") // Расширения Kotlin для базовых функций Android
    implementation("androidx.appcompat:appcompat:1.7.0") // Поддержка обратной совместимости UI компонентов
    implementation("com.google.android.material:material:1.12.0") // Библиотека компонентов Material Design 3
    implementation("androidx.constraintlayout:constraintlayout:2.2.0") // Расширенный менеджер компоновки слоев
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Компонент для отображения динамических списков
    
    // Библиотека для реализации криптографических функций и аппаратной защиты
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Зависимости для модульного и инструментального тестирования
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
