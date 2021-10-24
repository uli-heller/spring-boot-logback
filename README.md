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

2021-10-24 07:23:43.261  INFO 594947 --- [           main] c.e.springbo...
2021-10-24 07:23:43.262  INFO 594947 --- [           main] c.e.springbo...
2021-10-24 07:23:43.663  INFO 594947 --- [           main] c.e.springbo...
```

Links
-----

- [SpringBoot](https://spring.io/projects/spring-boot)
- [start.spring.io](start.spring.io)
- [Logback](http://logback.qos.ch/)

Historie
--------

- 2021-10-24: Erste Version
