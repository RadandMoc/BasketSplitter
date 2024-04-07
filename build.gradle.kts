import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0" // Dodajemy plugin Shadow
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //testImplementation("org.testng:testng:7.5.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveBaseName.set("BasketSplitter")
        archiveVersion.set("1.0")
        manifest.attributes["Main-Class"] = "com.ocado.basket.BasketSplitter"
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}

tasks.register("shadowJar2", ShadowJar::class) {
    archiveBaseName.set("BasketSplitter")
    archiveVersion.set("1.0")
    manifest.attributes["Main-Class"] = "com.ocado.basket.BasketSplitter" // Ustaw główną klasę

    // Include dependencies in the shadow JAR
    //from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
    /*from{
        configurations.runtimeClasspath.collect {it.isDirectory() ? it : zipTree(it)}
    }*/

    from(sourceSets.main.get().output)
    mergeServiceFiles() // Scal pliki META-INF/services/*
}
