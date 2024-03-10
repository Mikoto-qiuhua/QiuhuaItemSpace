plugins {
    id("java")
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.qiuhua.QiuhuaItemSpace"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
    maven("https://jitpack.io")
    maven {
        name = "spigotmc-repo"
        url = uri ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }  //SpigotMC仓库
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("mysql:mysql-connector-java:8.0.33")
    implementation("com.zaxxer:HikariCP:4.0.3")//数据库连接池
    compileOnly ("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")  //仅在编译时可用
    compileOnly(fileTree("src/libs"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveFileName.set("QiuhuaItemSpace-测试插件.jar")
    destinationDirectory.set(File ("D:\\我的世界插件"))
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    relocate("com.zaxxer.hikari", "org.qiuhua.hikari")
    mergeServiceFiles()
    manifest {
    }
}