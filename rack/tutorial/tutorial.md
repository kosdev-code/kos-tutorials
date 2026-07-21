---
title: Your First KOS App
excerpt: Build your first KOS app! Learn how to create, configure, build, run, and debug a KOS application from start to finish.
categories: [Rack]
tags: [Tutorial]
status: publish
author: sneh
---

# Your First KOS App

## Introduction

Welcome to the tutorial on building your first KOS app! In this guide, you'll learn how to create, configure, build, run, and debug a KOS application from start to finish.

[kondra_note type="info" title="Information"]
Before beginning, ensure you have completed all three steps in the [Get Started section](https://kosdev.com/articles/setup-you-kos-java-workspace/).
[/kondra_note]

[kondra_note type="admin-alert" title="Target Audience"]
This tutorial assumes you have Java experience. The source code for these tutorials is available on [GitHub](https://github.com/kosdev-code/kos-tutorials).
[/kondra_note]

## Customer Request

This tutorial imagines a bookstore chain looking to modernize their bestseller display using a digital smart rack.

[kondra_note type="info" title="Information"]
A smart rack is an advanced shelving system that uses modern technology to enhance shopping and store management. It might include sensors to track inventory, Bluetooth and NFC to gather data on customer habits, or digital screens and shelf fronts to display updated prices and promotions.

Their business objectives are:
1. Save time and money by transitioning from cardboard signage to over-the-air updates of digital content.
2. Enhance the customer experience through more engaging and dynamic displays.
[/kondra_note]

Our solution is to build a smart rack application that will rotate a collection of image files promoting recent bestselling books.

<image title="" name="bestsellers.png" caption="Open bestsellers" />

Let's dive into creating an innovative and engaging digital product rack using KOS for our customer!

## Step 1: Install KOS Studio

The KOS Studio application is an essential tool for KOS software developers, available for both Windows and Mac. It enables developers to define, package, and share KOS applications, test them using a built-in simulator, and deploy them to actual hardware like Raspberry Pi. While KOS Studio itself does not include an integrated development environment (IDE) or code editor, it allows for debugging and stepping through code in conjunction with Studio and an IDE, making it a comprehensive development tool.

Follow instructions for your OS to install KOS Studio [here](https://kosdev.com/kosdev/kos_downloads/).

## Step 2: Code the Application

Our digital rack project is quite straightforward. We need to loop through a collection of content and display it on the screen. This can be easily achieved by installing Linux on a Raspberry Pi and running a small Java Swing application. In fact, this example will demonstrate the KOS equivalent of this setup. While the example is simple, it allows us to introduce a few initial KOS concepts, use KOS Studio to install software and run in a simulator, and set up debugger support for remote debugging on any hardware target. We'll also get a glimpse into the simplicity of building a dedicated-purpose device using KOS, where no install or startup scripts are required. We'll simply push the play button and watch our device boot as a smart rack.

[kondra_note type="info" title="What is an Application in KOS?"]
KOS is an application-based platform. There are many examples of application-based platforms these days. From mobile devices to smart televisions, it seems everything can run applications. How is KOS any different from these platforms? The primary difference is the collaboration model for applications. On most consumer devices, applications represent dedicated experiences that a user interacts with one at a time. These applications are inherently untrusted and are typically isolated from each other using a form of sandboxing. In KOS, the application model is designed to be collaborative, where applications work together to enable new functionality. This model emphasizes the ability for applications to provide core functionality that other applications can use, similar to a library or a microservice

Find more [here](https://kosdev.com/articles/applications/)
[/kondra_note]

### The System Application

Now that we have a way to display our digital content, we need to code the core component that launches it: the System Application.

[kondra_note type="info" title="What is a System Application?"]
As KOS is designed for dedicated-use devices, the operating system boots into a single user-provided application that takes over the device and orchestrates all other functionality. In our example, this application simply displays content on the screen, but in other cases, the system application may load other applications that are dynamically defined and pushed over the air. In this tutorial, we’ll get our first look at a simple system application.
[/kondra_note]

### Displaying Content

Before we get to the system application, let's consider our options for displaying content on our rack. A common approach to displaying content is to use a web browser. It's a rich display engine for a broad range of content, including device user interfaces. KOS has sophisticated browser integration support, including the ability to control multiple browsers across multiple devices, push real time data, handle asynchronous operations and even provides the ability for different browsers to communicate directly to each other. However, for our first example we want to focus on the basics such as learning how to use KOS Studio and debugger integration, so we're going to display content in the simplest way possible, which is using Java Swing to rotate through images using a timer.

The class below uses basic Swing components to fill the screen with a Jframe that switches between images using a timer. As this is standard Swing programming, we show the class below without any additional description. This class will be used in our system application to display content.

Rather than bundling images inside the application [KAB](https://kosdev.com/articles/everything-is-a-kab/), we will use a data KAB. Data KABs allow you to package assets like images and videos separately from your code, making them easy to manage and update.

Creating a system application for KOS is as simple as extending the `SystemApplication` class. In the example below, we use the `load()` method to retrieve our asset KAB and then pass it to our UI.

<snippet-viewer source="tutorials" snippet="rack-app@RackApp.java"></snippet-viewer>

### The User Interface

The provided Java code defines a class `RackUI` that displays the book rack content directly from the `KabFile`. It is launched in the started method of the System Application. It uses the KOS `AdjustableCallback` to handle the timing of the image rotation.

<snippet-viewer source="tutorials" snippet="rack-ui@RackUI.java"></snippet-viewer>

## Step 3: Setup Project

In this section, we'll walk you through setting up a Maven project to create and build a KOS-specific deployable file, known as a KAB, for deployment. We'll cover the creation of a POM file for building the project and using a descriptor file to tell the KOS environment where to start. This tutorial assumes you are already familiar with Maven and POM files.

[kondra_note type="info" title="What are KABs?"]
For now, think of a KAB file as a collection of related files that serve as a universal container for all components of the operating system, from the kernel to applications. In a KOS release, EVERYTHING is a KAB.
Learn more [here](https://kosdev.com/articles/everything-is-a-kab/)
[/kondra_note]

### Create the POM file

This section will guide you through the pom.xml file setup specifically for developing a KOS application for your bestselling books rack. Because we are separating our application logic from our image assets, we use a multi-module Maven project. This consists of a parent `pom.xml` file to manage versions, and two child `pom.xml` files: one for the application, and another for the assets.

Here is the creation of the parent `pom.xml` file.

#### Section 1: Properties

The `<properties>` section centralizes project-wide settings and versions, ensuring consistency and ease of updates. This includes specifying the version of KOS to target, the version of Java required by KOS, and the versions of essential plugins.

```pom.xml
<!-- properties -->
<properties>
    <kos.version>1.10.0</kos.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <kos-kab-maven-plugin.version>1.3.5</kos-kab-maven-plugin.version>
    <lombok.version>1.18.38</lombok.version>
</properties>
```

The parent `pom.xml` also define two modules: `app` and `assets`.

```pom.xml
<modules>
    <module>app</module>
    <module>assets</module>
</modules>
```

#### Section 2: Include the KOS SDK

Problem: KOS provides numerous SDKs for building your app, but how do we know which versions of those SDKs to use so that they are compatible with each other and the version of KOS we are using?

Solution: We use the configuration management concept of a Bill of Materials (BOM). Each version of KOS (1.0, 1.1, 1.2, etc.) has a BOM that manages the specific compatible versions of SDKs. That way, when we add an SDK dependency, we don't have to know the specific SDK version. We place these special BOM dependencies in the `<dependencyManagement>` element to ensure version consistency across this Maven module. If this were a multi-module app, as will be used in future tutorials, it would ensure consistency across all modules.

In this example, we've added kos-bom as a `<dependency>` using the property kos.version from our properties section.

```pom.xml
<!-- include kOS SDK BOM -->
<dependencyManagement>
    <dependencies>
        <!-- kOS SDK -->
        <dependency>
            <groupId>com.kosdev.kos.sdk.bom</groupId>
            <artifactId>kos-bom</artifactId>
            <version>${kos.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then, in the `<dependencies>` element, we can add our core-api SDK without having to specify the version.

```pom.xml
<!-- project dependencies -->
<dependencies>
    <dependency>
        <groupId>com.kosdev.kos.sdk.api</groupId>
        <artifactId>api-core</artifactId>
    </dependency>
</dependencies>    
```

#### Section 3: Plugin Management

The build and `<pluginManagement>` sections manage the versions of plugins used in the build process. This includes plugins for packaging your application JAR and data files and converting into a KAB file for deployment. Remember, building jars is just steps to build what a KOS environment really needs: KAB files.

```pom.xml
<!-- plugins to build zip and kab files -->
<build>
    <pluginManagement>
        <plugins>
            <!-- kOS plugin for kab files -->
            <plugin>
                <groupId>com.kosdev.kos.maven</groupId>
                <artifactId>kos-sdk-maven-plugin</artifactId>
                <version>${kos-maven-plugin.version}</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

#### Section 4: Profiles

The `<profiles>` section defines build profiles that can be activated under specific conditions. The developer profile is activated by default and includes plugins for packaging your application into a KAB file, tailored for KOS development. Using the kos-kab-maven-plugin, you can specify which files you need in your KAB and in which directories they should be placed.

```pom.xml
<profiles>
    <profile>
        <id>developer</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <build>
            <plugins>
                <!-- Build KAB file -->
                <plugin>
                    <groupId>com.kosdev.kos.maven</groupId>
                    <artifactId>kos-kab-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>kabtool</goal>
                            </goals>
                            <configuration>
                                <type>kos.system</type>
                                <content>
                                    <copy>
                                        <dir>lib</dir>
                                        <include>${project.build.directory}/${project.artifactId}-${project.version}.jar</include>
                                    </copy>
                                    <copy>
                                        <dir>.</dir>
                                        <include>${project.basedir}/descriptor.json</include>
                                    </copy>
                                </content>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### App POM file

The application `pom.xml` manages the Java compilation and packages the `RackApp` code. It uses `kos.system` type in the KAB plugin, to indicate that it is being packaged into a system application KAB.

```pom.xml
<?xml version="1.0" encoding="utf-8"?>
<!-- @kdoc-pomcomplete@ -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
      <groupId>com.kos.tutorials</groupId>
      <artifactId>rack</artifactId>
      <version>0.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>rack-app</artifactId>


    <!-- project dependencies -->
    <dependencies>
        <dependency>
            <groupId>com.kosdev.kos.sdk.api</groupId>
            <artifactId>api-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <!-- build kab file -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>${maven-assembly-plugin.version}</version>
                <executions>
                    <execution>
                        <id>make-jar-with-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                        <configuration>
                            <descriptorRefs>
                                <descriptorRef>jar-with-dependencies</descriptorRef>
                            </descriptorRefs>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>com.kosdev.kos.maven</groupId>
                <artifactId>kos-kab-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>kabtool</goal>
                        </goals>
                        <configuration>
                            <type>kos.system</type>
                            <content>
                                <copy>
                                    <include>descriptor.json</include>
                                </copy>
                                <copy>
                                    <include>
                                        ${project.build.directory}/${project.artifactId}-${project.version}-jar-with-dependencies.jar</include>
                                    <dir>lib</dir>
                                </copy>
                            </content>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Assets POM file

This `pom.xml` is used to package images into a data KAB. The KAB type is a custom type called `tutorial.assets` for the purpose of this tutorial. Note, this matches the `ASSETS_KAB_TYPE` we defined in our `RackApp` to fetch the assets KAB and get the images.

```pom.xml
<?xml version="1.0" encoding="utf-8"?>
<!-- @kdoc-pomcomplete@ -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
      <groupId>com.kos.tutorials</groupId>
      <artifactId>rack</artifactId>
      <version>0.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>rack-assets</artifactId>

    <!-- build kab file -->
    <build>
        <plugins>
            <plugin>
                <groupId>com.kosdev.kos.maven</groupId>
                <artifactId>kos-kab-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>kabtool</goal>
                        </goals>
                        <configuration>
                            <type>tutorial.assets</type>
                            <content>
                                <copy>
                                    <include>*.png</include>
                                </copy>
                            </content>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Create the Descriptor File

The descriptor.json file provides a standardized method for embedding metadata into KAB files for use in KOS. We will use it to describe the details of our applications to KOS. In later tutorials, we'll see how descriptor.json can be used to support localization and even firmware updates. Developers can customize the descriptor.json, but must reserve the kos.* namespace for standardized sections.

#### The descriptor.json File

```json
{
  "kos": {
    "app": {
      "appClass": "com.bookstore.rack.RackApp",
      "appId": "system"
    }
  }
}
```

`appClass`: Specifies to KOS the class that it should load to launch your application.

`appId`: Defines a unique ID for this application. Every application requires a unique ID to help allocate resources in KOS. We're building a system application which always has an ID of `system`.

### Place the Content for the Image Rotator

#### Adding Images to Your Project

Add these [four images](https://github.com/kosdev-code/kos-tutorials/tree/main/rack/assets/pics) to the assets directory.

#### Project Directory Structure

Your project directory should look like this:

```
rack/
├── pom.xml                 (Parent POM)
├── app/
│   ├── pom.xml             (App POM)
│   ├── descriptor.json
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── bookstore/
│                       └── rack/
│                           ├── RackApp.java
│                           └── RackUI.java
├── assets/
│   ├── pom.xml             (Assets POM)
│   └── pics
│       ├── all.png
│       ├── book1.png
│       ├── book2.png
│       └── book3.png 
└── README.md
```

In this section, you learned how to set up a Maven project for a KOS application. We covered creating and configuring a POM file to manage your project, including specifying dependencies and plugins for building and deploying your app. We used a BOM to ensure compatibility between SDK versions. We also touched on the KAB plugin to package your project into a KAB file. By following these steps, you now have a fully configured Maven project ready for deploying a KOS application.

## Step 4: Build the Project

Run `mvn clean install` then navigate to the `target` directory within your project. You will find several files, including:

- **KAB File**: samples-rack-0.0.0-SNAPSHOT.kab. This is the KOS Archive Bundle that packages your application for deployment.

Your target directory should contain the JAR and KAB files, along with any other directories that Maven may generate. This structure represents the skeleton of your project after a build.

### Project Directory Structure

```
rack/
├── pom.xml  
├── app/
│   ├── pom.xml  
│   ├── descriptor.json
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── bookstore/
│   │                   └── rack/
│   │                       ├── RackApp.java
│   │                       └── RackUI.java
│   └── target/
│       ├── rack-app-0.0.0-SNAPSHOT.jar
│       └── rack-app-0.0.0-SNAPSHOT.kab
├── assets/
│   ├── pom.xml  
│   ├── pics
│   │   ├── all.png
│   │   ├── book1.png
│   │   ├── book2.png
│   │   └── book3.png 
│   └── target/
│       ├── rack-assets-0.0.0-SNAPSHOT.jar
│       └── rack-assets-0.0.0-SNAPSHOT.kab
└── README.md
```

By following these steps, you built your KOS system application and generated the necessary [artifacts](https://kosdev.com/articles/artifacts-instances-and-stores/). The KAB file, which packages your application, can then be imported into KOS Studio for further configuration and simulation. This process ensures that your application is properly packaged and ready for deployment on KOS-supported hardware.

## Step 5: Run the Application

This section guides you through using KOS Studio to create and configure a runnable KOS image for your Java application. You'll open KOS Studio, create a new image, configure it with the necessary settings, add your KAB file, and assign it to the appropriate section. You'll run the configured image in the KOS Studio simulator to test its functionality. Finally, you'll debug your applications to step through the code you've written. Following these steps is essential to running your application on your developer machine.

[kondra_note type="info" title="Why can’t I run a KOS app like any other Java application?"]
A KOS application, packaged into an image, is a complete set of code and dependencies that executes only on the KOS runtime system. KOS Studio includes a simulator to test applications in a KOS environment before deploying them to actual hardware.
Learn more about Studio [here](https://kosdev.com/articles/kos-studio/)
[/kondra_note]

### Create the image in Studio

Now that our Java application is written and bundled into a KAB file, let's turn our attention to KOS Studio.

[kondra_note type="info" title="KOS Image"]
In a nutshell, an image, or disk image, is the blueprint for what a KOS node will run when it boots. It’s defined by a manifest, which outlines the filesystem’s contents and how user space functions. For nodes running Java, this manifest also specifies all the Java code to execute. Think of the image as an editable version of this manifest—your ticket to creating a bootable and reusable environment for multiple devices without the hassle of repeated inheritance setups.
[/kondra_note]

Follow these steps to create an image:

1. Open the KOS Studio application.
2. In the top-level menu bar, click on the "open image manager" button.

<image title="Open my images" name="my_images.png" caption="" />

In this Images window, click on the "create new image" button.

<image title="Create image" name="create_image.png" caption="" />

In the popup dialog, enter a "Name" for this image, leave the "Parent Image" field blank, then click "Create":

<image title="Name your Image" name="name_it.png" caption="" />

Now the Images window shows an entry for the image you just created:

<image title="Listed image" name="listed_image.png" caption="" />

Close the Images window and return to the main Studio window.

<image title="My images updates" name="my_images_updates.png" caption="" />

### Configure the image

On the main Studio screen, click the gear icon associated with the image you just created, which opens up the configuration dialog. This configuration window is perhaps the most important part of KOS Studio. It gives you and your team the ability to create any number of runnable images, each with a wide variety of options. Much time will be devoted to using it.

<image title="Config" name="config.png" caption="" />

As you can see, there is a stack of six configuration items, also known as "cards":

- **Sections**: A section in KOS is an artifact container where you group your KABs and give them meaning by their names. More about [Sections](https://kondra.atlassian.net/wiki/pages/resumedraft.action?draftId=532414497).
- **File Mappings**: Defines mappings into the device/simulator file system.
- **Layers**: A layer is a specific type of KAB file that allows you to customize OS-level changes for your image. You can choose from Market, Published, or local layers to add to your image.
- **Local Artifacts**: The Local Artifacts card in the image configuration window lets users select a locally available KAB for use in the image. This is useful for testing changes or using artifacts that are not yet published.
- **Hardware Settings**: The Hardware Settings card allows users to configure their simulation and node settings, including selecting hardware profiles, configuring port mappings, and setting developer flags.
- **KOS Version**: The KOS Version card is the first step in setting up your image. Here, you can specify which KOS version and add-ons you want to use.

The "KOS Version" item has red text, meaning that it needs attention.

#### Configure the KOS Version

Our next step is to tell Studio which release of KOS we want to use, which will satisfy the current alert.

1. Click on the "KOS Version" card.
2. Click on the "Configure new release" button.
3. On the left-hand side, navigate and then select the desired KOS release.
4. Give this selection a name and an optional note.
5. Check Graphics.

<image title="Configure KOS release" name="release.png" caption="" />

1. Click the "Submit" button.
2. Then select the newly-created release.

At this point, the "KOS Version" section is no longer red, meaning it is configured properly.

#### Configure the local artifact

1. Click on the "Local Artifacts" card.
2. Click the "Select local artifact" button.
3. Navigate to and select the KAB files created earlier by our Java code.

<image title="Configure Local Artifact" name="local_artifact.png" caption="" />

4. Click the Select button, where you should see:

<image title="Showing selected private local artifact" name="available.png" caption="" />

#### Add local artifact to section

A section in KOS is the artifact container. It is where you group your KABs and give them meaning by their names.

1. Open the "Sections" card.
2. Drag the "rack-app-0.0.0-SNAPSHOT.kab" item from "Local Artifacts" to the kos.system "Section".

<image title="Add the local artifact to the kos.system section of the image" name="sections.png" caption="" />

3. Create a new section called `kos.assets` to match the name defined in the `RackApp`. Add the `rack-assets-0.0.0-SNAPSHOT.kab` to this section.

<image title="" name="rack-assets.png" caption="" />

### Execute the image

To run your KOS application in Studio's built-in simulator:

1. On the main Studio screen, find your "Bestseller Rack" image entry.
2. Ensure that "Simulator" is selected.
3. Click the "run now" (play) icon.

The application first downloads all necessary artifacts, showing the status in the Connections window. The first time you run an application it may take a while to download the required packages. Subsequent executions open much faster.

<image title="The connections window" name="downloading.png" caption="" />

When downloading is finished, the application will begin to load. You'll see the simulator starting up, with a KOS splash screen. When the simulator has finished, you should see your bestsellers rack application:

<image title="Screenshot" name="app-ui.png" caption="" />

Try dropping a break point in your app and give it a whirl!

Congratulations, your first KOS application is up and running! To make changes to your app, simply stop the simulator, update your code, rebuild the KAB file, and restart the simulator. It's that easy!

This lesson has equipped you with the skills to effectively use KOS Studio to create, configure, and test a runnable KOS image for your Java application. By following these steps—opening KOS Studio, creating a new image, configuring it with necessary settings, adding your KAB file, assigning it to the appropriate section, running it in the KOS Studio simulator, and debugging your code—you've laid the groundwork for successfully running your application on your developer machine. Great job!

# Conclusion

In this tutorial, we guided you through creating your first KOS application for a smart rack. Starting with an introduction to KOS, we installed KOS Studio, coded the core application, set up a Maven project, and built the KAB file using the KAB plugin. We then configured and tested the application in KOS Studio, ensuring it runs smoothly before deployment.

By the end of the tutorial, you built a functional smart rack application and gained insights into KOS's potential. You're now equipped with the skills to embark on more ambitious projects, leveraging KOS to create innovative, reliable, and efficient solutions for various smart device applications.