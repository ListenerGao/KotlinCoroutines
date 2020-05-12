# KotlinCoroutines
kotlin coroutines demo

## 准备工作
* 为避免 kotlin 项目编译失败，在 app/build.gradle 中添加以下设置：

    ``` 
    compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
    }
    // For Kotlin projects
    kotlinOptions {
        jvmTarget = "1.8"
    }
    ```
* 添加 Kotlin 协程相关，以及 lifecycle 相关依赖


    ```
    // kotlin 协程依赖
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5'
    // lifecycle 相关依赖
    implementation 'androidx.activity:activity-ktx:1.1.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.2.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'
    ```
