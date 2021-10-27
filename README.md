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

Logback
-------

Ausgangspunkt: 01-springboot.

Zwischenziel: Die Logzeilen sollen via Logback geschrieben werden.
Laut [Baeldung - SpringBoot-Logging](https://www.baeldung.com/spring-boot-logging)
müssen wir für die Verwendung von Logback quasi keine Änderungen vornehmen,
weil wir SpringBoot-Starter verwenden und diese loggen per Standard via Logback.

- Projekt bereinigen: `( cd 01-springboot; gradle clean; rm -rf .gradle; )`
- Projekt kopieren: `cp -a 01-springboot 02-logback`
- In's Projektverzeichnis wechseln: `cd 02-logback`
- Datei "src/main/resources/logback.xml" anlegen - Details siehe [logback.xml](02-logback/src/main/resources/logback.xml)
- Datei ".gitignore" erweitern: Logdatei ignorieren
- Kompilieren: `gradle build`

Ausführen sieht nun so aus:

```
$ java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

$ ls -l
insgesamt 24
-rw-rw-r-- 1 uli uli 1546 Okt 24 09:06 app-logback.log
drwxrwxr-x 9 uli uli 4096 Okt 24 09:06 build
...

$ cat app-logback.log
2021-10-24 09:06:46,947 INFO ... [main] Starting SpringbootApplication using Java 11.0.12 on ulicsl...
2021-10-24 09:06:46,949 DEBUG ... [main] Running with Spring Boot v2.5.6, Spring v5.3.12
2021-10-24 09:06:46,949 INFO ... [main] No active profile set, falling back to default profiles: default
2021-10-24 09:06:47,319 INFO ... [main] Started SpringbootApplication in 0.618 seconds (JVM running for 1.108)
2021-10-24 09:06:47,321 INFO ... [main] Uli war da
```

Die Lognachrichten landen in der Tat in der via Logback hinterlegten Logdatei!

Properties
----------

- Ausgangspunkt: 02-logback
- Arbeitsverzeichnis: 03-properties
- Ziel: Wir wollen ein SpringProperty aus application.properties verwenden

Aktionen:

- Projekt bereinigen: `( cd 02-logback; gradle clean; rm -rf .gradle; )`
- Projekt kopieren: `cp -a 02-logback 03-properties`
- In's Projektverzeichnis wechseln: `cd 03-properties`
- Hilfsklasse anlegen: [UliWarDa.java](03-properties/src/main/java/com/example/springboot/UliWarDa.java)
- Datei [application.properties](03-properties/src/main/resources/application.properties) erweitern: `uli=heller`
- Datei [SpringbootApplication.java](03-properties/src/main/java/com/example/springboot/SpringbootApplication.java) erweitern:
    - Configuration und PropertySource
    - Hilfsklasse instantiieren
- Kompilieren: `gradle build`
- Ausführen: `java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`

Die Log-Datei sieht nun so aus:

```
2021-10-24 10:50:34,039 INFO com.example.springboot.SpringbootApplication [main] Starting Spr...
2021-10-24 10:50:34,041 DEBUG com.example.springboot.SpringbootApplication [main] Running wit...
2021-10-24 10:50:34,041 INFO com.example.springboot.SpringbootApplication [main] No active pr...
2021-10-24 10:50:34,393 INFO com.example.springboot.UliWarDa [main] Constructor('heller')
2021-10-24 10:50:34,447 INFO com.example.springboot.SpringbootApplication [main] Started Spri...
2021-10-24 10:50:34,449 INFO com.example.springboot.SpringbootApplication [main] Uli war da
```

Das Property wird korrekt aufgelöst und geloggt!

Verschlüsselte Properties
-------------------------

- Ausgangspunkt: 03-properties
- Arbeitsverzeichnis: 04-encryption
- Ziel: Wir wollen ein verschlüsseltes SpringProperty aus application.properties verwenden

Aktionen:

- Projekt bereinigen: `( cd 03-properties; gradle clean; rm -rf .gradle app-logback.log; )`
- Projekt kopieren: `cp -a 03-properties 04-encryption`
- In's Projektverzeichnis wechseln: `cd 04-encryption`
- Festlegen des Kennwortes für die Verschlüsselung: "uli-war-da" (ohne Anführungszeichen)
- Verschlüsselung durchführen: `java -jar spring-boot-cli*.jar encrypt "ich bin verschluesselt" --key uli-war-da`
- Verschlüsselten Wert zwischenspeichern: `fe435f6555c445cfb153c962d34a4c206d4d3c82200b595b89fdb372e099443d08fb23e78498f1184c74dfa0f8f40049`
- Datei [build.gradle](04-encryption/build.gradle) erweitern: spring-cloud-config-starter muß eingebunden werden
- Datei [application.properties](04-encryption/src/main/resources/application.properties) erweitern:
    ```diff
    diff --git a/04-encryption/src/main/resources/application.properties b/04-encryption/src/main/resources/application.properties
    index d908c4c..3e893bd 100644
    --- a/04-encryption/src/main/resources/application.properties
    +++ b/04-encryption/src/main/resources/application.properties
    @@ -1 +1,3 @@
     uli=heller
    +encrypted.property = {cipher}fe435f6555c445cfb153c962d34a4c206d4d3c82200b595b89fdb372e099443d08fb23e78498f1184c74dfa0f8f40049
    +cleartext.property = ich bin nicht verschluesselt
    +spring.config.import=optional:configserver:
    ```
- Hilfsklasse erweitern: [UliWarDa.java](04-encryption/src/main/java/com/example/springboot/UliWarDa.java):
    - cleartext
    - encrypted
    - Konstruktor + Logging
- Datei [SpringbootApplication.java](04-encryption/src/main/java/com/example/springboot/SpringbootApplication.java) erweitern:
    - Instantiierung von UliWarDa erweitern
- Kompilieren: `gradle clean build` -> scheitert!
- Sichten von app-logback.log:
    ```
    ...
    Caused by: java.lang.IllegalStateException: Cannot decrypt: key=encrypted.property
    	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:159)
    	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.lambda$decrypt$0(AbstractEnvironmentDecrypt.java:137)
    	at java.base/java.util.LinkedHashMap.replaceAll(LinkedHashMap.java:694)
    	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:132)
    	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:70)
    ...
    ```
- Nochmal mit ENCRYPT_KEY: `ENCRYPT_KEY=uli-war-da gradle clean build` -> klappt!
- Ausführen: `rm -f app-logback.log; java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Sichten: `tail app-logback.log`
    ```
    Caused by: java.lang.UnsupportedOperationException: No decryption for FailsafeTextEncryptor. Did you configure the keystore correctly?
	at org.springframework.cloud.bootstrap.encrypt.TextEncryptorUtils$FailsafeTextEncryptor.decrypt(TextEncryptorUtils.java:188)
	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:144)
	... 31 common frames omitted
    ```
- Nochmal ausführen mit ENCRYPT_KEY: `rm -f app-logback.log; ENCRYPT_KEY=uli-war-da java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Sichten: `tail app-logback.log`
    ```
    2021-10-24 11:40:13,140 INFO com.example.springboot.SpringbootApplication [main] Starting SpringbootApplication using Java 11.0.12 on ulicsl with PID...
    2021-10-24 11:40:13,142 DEBUG com.example.springboot.SpringbootApplication [main] Running with Spring Boot v2.5.6, Spring v5.3.12
    2021-10-24 11:40:13,142 INFO com.example.springboot.SpringbootApplication [main] No active profile set, falling back to default profiles: default
    2021-10-24 11:40:13,611 INFO com.example.springboot.UliWarDa [main] Constructor('heller','ich bin nicht verschluesselt','ich bin verschluesselt')
    2021-10-24 11:40:13,787 INFO com.example.springboot.SpringbootApplication [main] Started SpringbootApplication in 1.349 seconds (JVM running for 1.851)
    2021-10-24 11:40:13,790 INFO com.example.springboot.SpringbootApplication [main] Uli war da
    ```

Spring Profile
--------------

- Ausgangspunkt: 04-encryption
- Arbeitsverzeichnis: 05-springprofile
- Ziel: Wir wollen die Logback-Konfiguration differenzieren nach den aktiven Spring Profiles

Aktionen:

- Projekt bereinigen: `( cd 04-encryption; gradle clean; rm -rf .gradle app-logback.log; )`
- Projekt kopieren: `cp -a 04-encryption 05-springprofile`
- In's Projektverzeichnis wechseln: `cd 05-springprofile`

Links
-----

- [SpringBoot](https://spring.io/projects/spring-boot)
- [start.spring.io](start.spring.io)
- [Logback](http://logback.qos.ch/)
- [SpringBoot - Logging](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#features.logging.custom-log-configuration)
- [Baeldung - SpringBoot-Logging](https://www.baeldung.com/spring-boot-logging)
- [Mkyong - logback.xml Example](https://mkyong.com/logging/logback-xml-example/)

Historie
--------

- 2021-10-24: Erste Version
