Zusammenspiel von SpringBoot und Logback und Log4j2
===================================================

Dies ist ein kleines Beispielprojekt, welches
das Zusammenspiel von SpringBoot und LogBack untersucht.
Keine Ahnung, warum wir Logback verwenden und nicht das
neuere Log4j2!

Im Rahmen von [CVE-2021-44228 vulnerability in Apache Log4j library](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-44228)
erweitere ich die Untersuchung noch ein klein wenig in Hinblick auf die Einbindung von "log4j2".

TLDR
----

- Das Zusammenspiel ist relativ problemlos
- Wenn **alle** Fehlermeldungen von SpringBoot im Log erscheinen sollen,
  wird es ein wenig kompliziert
- Problematisch scheint insbesondere das automatische Entschlüsseln
  mittels ENCRYPT_KEY zu sein
- Es dürfen dann **keine** "Spring-Erweiterungen" bei der Konfiguration
  von Logback verwendet werden
  - logging.config ... funktioniert nicht
  - logback-spring.xml und `<springProfile>` ... funktioniert nicht
- Also dies verwenden:
  - logback.configurationFile
  - logback.xml

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

### Irrweg

- Ausgangspunkt: 04-encryption
- Arbeitsverzeichnis: 05-springprofile
- Ziel: Wir wollen die Logback-Konfiguration differenzieren nach den aktiven Spring Profiles.
  Wir machen es erstmal falsch, so dass die Logback-Konfiguration bei der Intitalisierung von
  Spring teilweise nicht verwendet wird!

Aktionen:

- Projekt bereinigen: `( cd 04-encryption; gradle clean; rm -rf .gradle app-logback.log; )`
- Projekt kopieren: `cp -a 04-encryption 05-springprofile`
- In's Projektverzeichnis wechseln: `cd 05-springprofile`
- Datei [logback.xml](05-springprofile/src/main/resources/logback.xml) anpassen: springProfile aufnehmen
    ```diff
    @@ -25,8 +25,22 @@
             <appender-ref ref="FILE-ROLLING"/>
         </logger>
     
    +    <springProfile name="!local">
           <root level="error">
             <appender-ref ref="FILE-ROLLING"/>
           </root>
    +    </springProfile>
    +    <springProfile name="local">
    +      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    +        <!-- encoders are assigned the type
    +           ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    +        <encoder>
    +          <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    +        </encoder>
    +      </appender>
    +      <root level="info">
    +        <appender-ref ref="STDOUT" />
    +      </root>
    +    </springProfile>
     
     </configuration>
    ```
- Kompilieren: `ENCRYPT_KEY=uli-war-da gradle clean build`
- Ausführen: `rm -f app-logback.log; java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Wie erwartet erscheinen Teile der Logs auf der Konsole! (Klar! springProfile darf ja nicht in logback.xml verwendet werden!)

### Korrektur

- Ausgangspunkt: 05-springprofile
- Arbeitsverzeichnis: 06-springprofile
- Ziel: Wir wollen die Logback-Konfiguration differenzieren nach den aktiven Spring Profiles.
  Diesmal machen wir es richtig!

Aktionen:

- Projekt bereinigen: `( cd 05-springprofile; gradle clean; rm -rf .gradle app-logback.log; )`
- Projekt kopieren: `cp -a 05-springprofile 06-springprofile`
- In's Projektverzeichnis wechseln: `cd 06-springprofile`
- Datei logback.xml umbenennen in [logback-spring.xml](06-springprofile/src/main/resources/logback-spring.xml)
- Kompilieren: `ENCRYPT_KEY=uli-war-da gradle clean build`
- Ausführen: `rm -f app-logback.log; java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Es erscheinen immer noch Teile der Logs auf der Konsole!

```
$ rm -f app-logback.log; java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar
05:54:59.472 [main] ERROR org.springframework.boot.SpringApplication - Application run failed
java.lang.IllegalStateException: Cannot decrypt: key=encrypted.property
	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:159)
	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.lambda$decrypt$0(AbstractEnvironmentDecrypt.java:137)
	at java.base/java.util.LinkedHashMap.replaceAll(LinkedHashMap.java:694)
	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:132)
...
Caused by: java.lang.UnsupportedOperationException: No decryption for FailsafeTextEncryptor. Did you configure the keystore correctly?
	at org.springframework.cloud.bootstrap.encrypt.TextEncryptorUtils$FailsafeTextEncryptor.decrypt(TextEncryptorUtils.java:188)
	at org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:144)
	... 31 common frames omitted
```

### Mehrere Konfigurationsdateien

- Ausgangspunkt: 04-encryption
- Arbeitsverzeichnis: 07-multiconfig
- Ziel: Wir wollen die Logback-Konfiguration differenzieren nach den aktiven Spring Profiles.
  Wir verwenden dazu "-Dlogging.config".

Aktionen:

- Projekt bereinigen: `( cd 04-encryption; gradle clean; rm -rf .gradle app-logback.log; )`
- Projekt kopieren: `cp -a 04-encryption 07-multiconfig`
- In's Projektverzeichnis wechseln: `cd 07-multiconfig`
- Datei logback.xml kopieren nach [logback-local.xml](07-springprofile/src/main/resources/logback-cloud.xml) und anpassen:
    ```diff
    @@ -1,9 +1,9 @@
     <?xml version="1.0" encoding="UTF-8"?>
     <configuration>
     
    -    <property name="LOGFILE" value="app-logback.log"/>
    +    <property name="LOGFILE" value="app-cloud-logback.log"/>
     
         <appender name="FILE-ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
             <file>${LOGFILE}</file>
     
             <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    @@ -15,11 +15,11 @@
                 <!-- 60 days to keep -->
                 <maxHistory>60</maxHistory>
             </rollingPolicy>
     
             <encoder>
    -            <pattern>%d %p %c{1.} [%t] %m%n</pattern>
    +            <pattern>CLOUD - %d %p %c{1.} [%t] %m%n</pattern>
             </encoder>
         </appender>
     
         <logger name="com.example" level="debug" additivity="false">
             <appender-ref ref="FILE-ROLLING"/>
             </appender>
    ```
- Kompilieren: `ENCRYPT_KEY=uli-war-da gradle clean build`
- Ausführen: `rm -f app*logback.log; java -Dlogback.configurationFile=logback-cloud.xml -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Alle Logs erscheinen in app-cloud-logback.log
- Ausführen: `rm -f app*logback.log; java -jar build/libs/springboot-0.0.1-SNAPSHOT.jar`
- Alle Logs erscheinen in app-logback.log

Ich habe auch zahlreiche Experimente mit "-Dlogging.config" durchgeführt. Die sind allesamt gescheitert.
Offenbar wird das Property erst relativ spät während der Initialisierung von Spring ausgewertet, so dass
frühe Initialisierungsprobleme dann mit der falschen Log-Konfiguration protokolliert werden!

Log4j
-----

Ausgangspunkt: 01-springboot.

Zwischenziel: Wir untersuchen, welche log4j-Klassen
angezogen werden.

- Abhängigkeitsbaum anzeigen: `( cd 01-springboot; gradle dependencies; )`
- Projekt bereinigen: `( cd 01-springboot; gradle clean; rm -rf .gradle; )`
- Projekt kopieren: `cp -a 01-springboot 08-springboot-261`
- In's Projektverzeichnis wechseln: `cd 08-springboot-261`
- Datei "build.gradle" anpassen:
    ```diff
    ----------------------- 08-springboot-2.6.1/build.gradle -----------------------
    index 745159c..31776fa 100644
    @@ -1,7 +1,7 @@
     plugins {
    -	id 'org.springframework.boot' version '2.5.6'
    +	id 'org.springframework.boot' version '2.6.1'
     	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
     	id 'java'
     }
     
     group = 'com.example'
    ```
- Abhängigkeitsbaum anzeigen: `gradle dependencies`
- Noch eine Anpassung an "build.gradle":
    ```diff
    index 31776fa..c0a8581 100644
    --- a/08-springboot-2.6.1/build.gradle
    +++ b/08-springboot-2.6.1/build.gradle
    @@ -8,6 +8,8 @@ group = 'com.example'
     version = '0.0.1-SNAPSHOT'
     sourceCompatibility = '11'
     
    +ext['log4j2.version'] = '2.16.0'
    +
     repositories {
            mavenCentral()
     }
    ```
- Abhängigkeitsbaum anzeigen: `gradle dependencies`

Die Abhängigkeitsbäume stehen unten.

### Abhängigkeiten für 2.5.6

```
...
productionRuntimeClasspath
\--- org.springframework.boot:spring-boot-starter -> 2.5.6
     +--- org.springframework.boot:spring-boot:2.5.6
     |    +--- org.springframework:spring-core:5.3.12
     |    |    \--- org.springframework:spring-jcl:5.3.12
     |    \--- org.springframework:spring-context:5.3.12
     |         +--- org.springframework:spring-aop:5.3.12
     |         |    +--- org.springframework:spring-beans:5.3.12
     |         |    |    \--- org.springframework:spring-core:5.3.12 (*)
     |         |    \--- org.springframework:spring-core:5.3.12 (*)
     |         +--- org.springframework:spring-beans:5.3.12 (*)
     |         +--- org.springframework:spring-core:5.3.12 (*)
     |         \--- org.springframework:spring-expression:5.3.12
     |              \--- org.springframework:spring-core:5.3.12 (*)
     +--- org.springframework.boot:spring-boot-autoconfigure:2.5.6
     |    \--- org.springframework.boot:spring-boot:2.5.6 (*)
     +--- org.springframework.boot:spring-boot-starter-logging:2.5.6
     |    +--- ch.qos.logback:logback-classic:1.2.6
     |    |    +--- ch.qos.logback:logback-core:1.2.6
     |    |    \--- org.slf4j:slf4j-api:1.7.32
     |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.14.1
     |    |    +--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
     |    |    \--- org.apache.logging.log4j:log4j-api:2.14.1
     |    \--- org.slf4j:jul-to-slf4j:1.7.32
     |         \--- org.slf4j:slf4j-api:1.7.32
     +--- jakarta.annotation:jakarta.annotation-api:1.3.5
     +--- org.springframework:spring-core:5.3.12 (*)
     \--- org.yaml:snakeyaml:1.28
...
```

### Abhängigkeiten für 2.6.1

```
...
productionRuntimeClasspath
\--- org.springframework.boot:spring-boot-starter -> 2.6.1
     +--- org.springframework.boot:spring-boot:2.6.1
     |    +--- org.springframework:spring-core:5.3.13
     |    |    \--- org.springframework:spring-jcl:5.3.13
     |    \--- org.springframework:spring-context:5.3.13
     |         +--- org.springframework:spring-aop:5.3.13
     |         |    +--- org.springframework:spring-beans:5.3.13
     |         |    |    \--- org.springframework:spring-core:5.3.13 (*)
     |         |    \--- org.springframework:spring-core:5.3.13 (*)
     |         +--- org.springframework:spring-beans:5.3.13 (*)
     |         +--- org.springframework:spring-core:5.3.13 (*)
     |         \--- org.springframework:spring-expression:5.3.13
     |              \--- org.springframework:spring-core:5.3.13 (*)
     +--- org.springframework.boot:spring-boot-autoconfigure:2.6.1
     |    \--- org.springframework.boot:spring-boot:2.6.1 (*)
     +--- org.springframework.boot:spring-boot-starter-logging:2.6.1
     |    +--- ch.qos.logback:logback-classic:1.2.7
     |    |    +--- ch.qos.logback:logback-core:1.2.7
     |    |    \--- org.slf4j:slf4j-api:1.7.32
     |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.14.1
     |    |    +--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
     |    |    \--- org.apache.logging.log4j:log4j-api:2.14.1
     |    \--- org.slf4j:jul-to-slf4j:1.7.32
     |         \--- org.slf4j:slf4j-api:1.7.32
     +--- jakarta.annotation:jakarta.annotation-api:1.3.5
     +--- org.springframework:spring-core:5.3.13 (*)
     \--- org.yaml:snakeyaml:1.29
...
```

### Abhängigkeiten für 2.6.1 und log4j2.version

```
...
productionRuntimeClasspath
\--- org.springframework.boot:spring-boot-starter -> 2.6.1
     +--- org.springframework.boot:spring-boot:2.6.1
     |    +--- org.springframework:spring-core:5.3.13
     |    |    \--- org.springframework:spring-jcl:5.3.13
     |    \--- org.springframework:spring-context:5.3.13
     |         +--- org.springframework:spring-aop:5.3.13
     |         |    +--- org.springframework:spring-beans:5.3.13
     |         |    |    \--- org.springframework:spring-core:5.3.13 (*)
     |         |    \--- org.springframework:spring-core:5.3.13 (*)
     |         +--- org.springframework:spring-beans:5.3.13 (*)
     |         +--- org.springframework:spring-core:5.3.13 (*)
     |         \--- org.springframework:spring-expression:5.3.13
     |              \--- org.springframework:spring-core:5.3.13 (*)
     +--- org.springframework.boot:spring-boot-autoconfigure:2.6.1
     |    \--- org.springframework.boot:spring-boot:2.6.1 (*)
     +--- org.springframework.boot:spring-boot-starter-logging:2.6.1
     |    +--- ch.qos.logback:logback-classic:1.2.7
     |    |    +--- ch.qos.logback:logback-core:1.2.7
     |    |    \--- org.slf4j:slf4j-api:1.7.32
     |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.14.1 -> 2.16.0
     |    |    +--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
     |    |    \--- org.apache.logging.log4j:log4j-api:2.16.0
     |    \--- org.slf4j:jul-to-slf4j:1.7.32
     |         \--- org.slf4j:slf4j-api:1.7.32
     +--- jakarta.annotation:jakarta.annotation-api:1.3.5
     +--- org.springframework:spring-core:5.3.13 (*)
     \--- org.yaml:snakeyaml:1.29
...
```

Links
-----

- [SpringBoot](https://spring.io/projects/spring-boot)
- [start.spring.io](start.spring.io)
- [Logback](http://logback.qos.ch/)
- [Logback - Konfiguration](http://logback.qos.ch/manual/configuration.html)
- [SpringBoot - Logging](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#features.logging.custom-log-configuration)
- [Baeldung - SpringBoot-Logging](https://www.baeldung.com/spring-boot-logging)
- [Mkyong - logback.xml Example](https://mkyong.com/logging/logback-xml-example/)
- [CVE-2021-44228](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-44228)
- [Log4J2 Vulnerability and Spring Boot](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot)

Historie
--------

- 2021-12-17: Weitere Untersuchungen in Zusammenhang mit CVE-2021-44228
- 2021-10-27: Experimente mit SpringProfiles
- 2021-10-24: Erste Version
