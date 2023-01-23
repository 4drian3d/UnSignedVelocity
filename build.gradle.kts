plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    //implementation(libs.bstats)
    compileOnly(libs.velocity.api)
    compileOnly(libs.velocity.proxy)
    annotationProcessor(libs.velocity.api)
    compileOnly(libs.protocolize)
}

blossom {
    replaceTokenIn("src/main/java/me/adrianed/unsignedvelocity/utils/Constants.java")
    replaceToken("{version}", version)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(11)
    }
    clean {
        delete("run")
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
