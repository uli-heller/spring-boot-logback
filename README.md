Zusammenspiel von SpringBoot und Logback
========================================

Dies ist ein kleines Beispielprojekt, welches
das Zusammenspiel von SpringBoot und LogBack untersucht.
Keine Ahnung, warum wir Logback verwenden und nicht das
neuere Log4j2!

SpringBoot
----------

Mini-Beispielprojekt von SpringBoot anlegen:

- [start.spring.io](start.spring.io)
- Project: Gradle Project
- Language: Java
- SpringBoot: 2.5.6
- ProjectMetadata
    - Group: com.example
    - Artifact: springboot
    - Name: springboot
    - Package name: com.example.springboot
    - Packaging: Jar
    - Java: 11
- Generate -> springboot.zip
- Auspacken nach "01-springboot"
- Löschen: `rm -rf 01-springboot/{gradle,gradlew,gradlew.bat}`

Projekt bauen:

```
$ gradle build
Starting a Gradle Daemon (subsequent builds will be faster)

BUILD SUCCESSFUL in 27s
7 actionable tasks: 7 executed
```

Projekt ausführen:

```
$ java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-10-24 08:42:39.397  INFO 656856 --- ... Starting SpringbootApplication using Java 11.0.12 on ulicsl with ...
2021-10-24 08:42:39.399  INFO 656856 --- ... No active profile set, falling back to default profiles: default
2021-10-24 08:42:39.791  INFO 656856 --- ... Started SpringbootApplication in 0.688 seconds (JVM running for 0.994)
2021-10-24 08:42:39.793  INFO 656856 --- ... Uli war da
```

Eigene Logs einbauen basierend auf Lombok:

- build.gradle erweitern um Lombok
- SpringbootApplication.java erweitern um Lognachricht

Im Detail:

```diff
diff --git a/01-springboot/build.gradle b/01-springboot/build.gradle
index a410fa3..745159c 100644
--- a/01-springboot/build.gradle
+++ b/01-springboot/build.gradle
@@ -14,6 +14,8 @@ repositories {
 
 dependencies {
        implementation 'org.springframework.boot:spring-boot-starter'
+        compileOnly 'org.projectlombok:lombok:1.18.22'
+        annotationProcessor 'org.projectlombok:lombok:1.18.22'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
 }

diff --git a/01-springboot/src/main/java/com/example/springboot/SpringbootApplication.java b/01-springboot/src/main/java/com/example/springboot/SpringbootApplication.java
index de19f6f..fd0a034 100644
--- a/01-springboot/src/main/java/com/example/springboot/SpringbootApplication.java
+++ b/01-springboot/src/main/java/com/example/springboot/SpringbootApplication.java
@@ -1,13 +1,16 @@
 package com.example.springboot;
 
+import lombok.extern.slf4j.Slf4j;
 import org.springframework.boot.SpringApplication;
 import org.springframework.boot.autoconfigure.SpringBootApplication;
 
 @SpringBootApplication
+@Slf4j
 public class SpringbootApplication {
 
        public static void main(String[] args) {
                SpringApplication.run(SpringbootApplication.class, args);
+               log.info("Uli war da");
        }
 
 }

```

Nochmal bauen:

```
$ gradle clean build

BUILD SUCCESSFUL in 2s
8 actionable tasks: 8 executed
```

Und auch nochmal ausführen:

```
$ java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-10-24 08:46:21.898  INFO 661527 --- ... Starting SpringbootApplication using Java 11.0.12 on ulicsl with ...
2021-10-24 08:46:21.900  INFO 661527 --- ... No active profile set, falling back to default profiles: default
2021-10-24 08:46:22.295  INFO 661527 --- ... Started SpringbootApplication in 0.705 seconds (JVM running for 1.014)
2021-10-24 08:46:22.298  INFO 661527 --- ... Uli war da
```


Links
-----

- [SpringBoot](https://spring.io/projects/spring-boot)
- [start.spring.io](start.spring.io)
- [Logback](http://logback.qos.ch/)

Historie
--------

- 2021-10-24: Erste Version
