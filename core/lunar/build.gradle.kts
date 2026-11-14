plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    // Bát tự (tứ trụ, thập thần, tàng can, đại vận): https://github.com/6tail/lunar-java – giấy phép MIT
    api("cn.6tail:lunar:1.7.7")
    testImplementation(libs.junit)
}
