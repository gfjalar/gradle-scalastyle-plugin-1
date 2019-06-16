package org.github.alisiikh.scalastyle

import org.apache.commons.io.FileUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

abstract class ScalastyleSpec extends Specification {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Shared
    File testProjectBuildDir

    def createBuildFolder(String projectDir) {
        FileUtils.copyDirectory(new File(this.class.getResource("/$projectDir").file), testProjectDir.root)
        testProjectBuildDir = new File(testProjectDir.root, "build")
    }

    def generateBuildGradleFile(String scalastyleOverrides = null) {
        testProjectDir.newFile("build.gradle") << """
plugins {
    id 'com.github.alisiikh.scalastyle'
}

${scalastyleOverrides ?: """
scalastyle {
    scalaVersion = '2.12'
    verbose = false
}
"""}

repositories {
    mavenCentral()
}

dependencies {
    compile 'org.scala-lang:scala-library:2.12.8'
}

ScalaCompileOptions.metaClass.useAnt = false
tasks.withType(ScalaCompile) {
    scalaCompileOptions.useAnt = false
}
tasks.withType(ScalaCompile) {
    configure(scalaCompileOptions.forkOptions) {
        memoryMaximumSize = '1g'
        jvmArgs = ['-XX:MaxPermSize=512m']
    }
}
"""
    }
}
