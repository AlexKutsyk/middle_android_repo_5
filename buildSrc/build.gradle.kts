plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
}

allprojects {
    repositories {
        mavenCentral()
    }
}

gradlePlugin {
        plugins {
            create("untranslated_plugin") {
                id = "untranslated_plugin"
                implementationClass = "com.example.buildsrc.FindUntranslatedStringsPlugin"
            }
        }
}