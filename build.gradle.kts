plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    implementation(libs.bstats)
    compileOnly(libs.velocity.api)
    compileOnly(libs.velocity.proxy)
    annotationProcessor(libs.velocity.api)
    compileOnly(libs.netty)
}

blossom {
    replaceTokenIn("src/main/java/io/github/_4drian3d/unsignedvelocity/utils/Constants.java")
    replaceToken("{version}", version)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(11)
    }
    build {
        dependsOn(shadowJar)
    }
    clean {
        delete("run")
    }
    shadowJar {
        relocate("org.bstats", "io.github._4drian3d.unsignedvelocity.libs.bstats")
        minimize()
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
