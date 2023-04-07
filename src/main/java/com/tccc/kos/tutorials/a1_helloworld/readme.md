# Hello World app #1

## Preliminaries

- Decide which version of kOS you're going to use

## Create a Maven Project

- Create a new Maven project
- Make the kOS BOM (bill-of-material) the parent POM. This ensures that the libraries you're using match what's in the kOS release.

``` xml
    <parent>
        <groupId>com.tccc.kos</groupId>
        <artifactId>bom</artifactId>
        <version>1.0.0-SNAPSHOT</version>           <!-- ENTER YOUR DESIRED kOS VERSION -->
        <relativePath/>
    </parent>
```

- Add a property to indicate the kOS version you're using:

``` xml
    <properties>
        <kos.version>1.0.0-SNAPSHOT</kos.version>   <!-- USE THE SAME VERSION AS THE "PARENT" -->
    </properties>
```

- Add a dependency to `api-core`:

``` xml
    <dependency>
        <groupId>com.tccc.kos.sdk</groupId>
        <artifactId>api-core</artifactId>
        <version>${kos.version}</version>
    </dependency>
```

- Add dependencies to logback and lombok:

``` xml
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
```

## Write Your Java Code

- Create `MyApp` class that extends `SystemApplication`
- You must override the `start()` and `setConfig()` methods
- You may override the `load()`, `stop()`, and `unload()` methods
- Add the `@Slf4j` annotation to the class
- Add a log statement to each method, something like `log.info("MyApp.start()");`

It should look something like this:

``` java
package com.tccc.kos.tutorials.a1_helloworld;

import com.tccc.kos.commons.core.service.config.ConfigBean;
import com.tccc.kos.core.service.app.SystemApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyApp extends SystemApplication {

    @Override
    public void load() {
        log.info("MyApp.load()");
    }

    @Override
    public void start() {
        log.info("MyApp.start()");
    }

    @Override
    public void stop() {
        log.info("MyApp.stop()");
    }

    @Override
    public void unload() {
        log.info("MyApp.unload()");
    }

    @Override
    public void setConfig(ConfigBean configBean) {
        log.info("MyApp.setConfig()");
    }
}
```

## Save Output to a ZIP file

Back to your Maven `pom.xml` file, add the following to <build> -> <plugins>:

``` xml
    <!-- Zip up the build contents -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-assembly-plugin</artifactId>
        <executions>
            <execution>
                <id>make-assembly</id>
                <phase>package</phase>
                <goals>
                    <goal>single</goal>
                </goals>
                <configuration>
                    <descriptors>
                        <descriptor>assembly.xml</descriptor>
                    </descriptors>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

And add the following `assembly.xml` file to your project's root directory:

``` xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0 http://maven.apache.org/xsd/assembly-1.1.0.xsd">
   <id>myapp</id>
   <baseDirectory>/</baseDirectory>
   <formats>
      <format>zip</format>
   </formats>
   <files>
      <file>
         <source>descriptor.json</source>
         <outputDirectory>/</outputDirectory>
      </file>
      <file>
         <source>${project.build.directory}/${project.artifactId}-${project.version}.jar</source>
         <outputDirectory>lib</outputDirectory>
      </file>
   </files>
</assembly> 
```

This creates a ZIP output file in the `target` directory.
