plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    implementation(libs.bstats)
    implementation(libs.velocityhexlogger)
    compileOnly(libs.velocity.api)
    compileOnly(libs.velocity.proxy)
    annotationProcessor(libs.velocity.api)
    compileOnly(libs.netty)
    compileOnly(libs.vpacketevents)
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
        relocate("io.github._4drian3d.velocityhexlogger", "io.github._4drian3d.unsignedvelocity.libs.velocityhexlogger")
        relocate("net.kyori.adventure.text.logger", "io.github._4drian3d.unsignedvelocity.libs.logger")
        minimize()
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
