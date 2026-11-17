---
title: Your First KOS App
excerpt: Build your first KOS app! Learn how to create, configure, build, run, and debug a KOS application from start to finish.
categories: [Rack]
tags: [Tutorial]
status: draft
author: sneh
_yoast_wpseo_focuskw: Application
---

## Introduction

Welcome to the tutorial on building your first KOS app! In this guide, you'll learn how to create, configure, build,
run, and debug a KOS application from start to finish.

[kondra_note type="info" title="Information"]
Before beginning, ensure you have completed all three steps in
the [Get Started section](https://kosdev.com/articles/setup-you-kos-java-workspace/).
[/kondra_note]

[kondra_note type="admin-alert" title="Target Audience"]
This tutorial assumes you have Java experience. The source code for these tutorials is available
on [GitHub](https://github.com/kosdev-code/kos-tutorials/tree/main/rack).
[/kondra_note]

## Customer Request

This tutorial imagines a bookstore chain looking to modernize their bestseller display using a digital smart rack.

[kondra_note type="info" title="Information"]
A smart rack is an advanced shelving system that uses modern technology to enhance shopping and store management. It
might include sensors to track inventory, Bluetooth and NFC to gather data on customer habits, or digital screens and
shelf fronts to display updated prices and promotions.

Their business objectives are:

1. Save time and money by transitioning from cardboard signage to over-the-air updates of digital content.
2. Enhance the customer experience through more engaging and dynamic displays.
   [/kondra_note]

Our solution is to build a smart rack application that will rotate a collection of image files promoting recent
bestselling books.

<image title="" name="bestsellers.png" caption="Open bestsellers" /> 

Let's dive into creating an innovative and engaging digital product rack using KOS for our customer!

## Step 1: Install KOS Studio

The KOS Studio application is an essential tool for KOS software developers, available for Windows, Mac and Linux. It
enables developers to define, package, and share KOS applications, test them using a built-in simulator, and deploy them
to actual hardware like Raspberry Pi. While KOS Studio itself does not include an integrated development environment (
IDE) or code editor, it allows for debugging and stepping through code in conjunction with Studio and an IDE, making it
a comprehensive development tool.

Follow instructions for your OS to install KOS Studio [here](https://kosdev.com/download-studio-2-0/).

## Step 2: Code the Application

Our digital rack project is quite straightforward. We need to loop through a collection of content and display it on the
screen. This can be easily achieved by installing Linux on a Raspberry Pi and running a small Java Swing application. In
fact, this example will demonstrate the KOS equivalent of this setup. While the example is simple, it allows us to
introduce a few initial KOS concepts, use KOS Studio to install software and run in a simulator, and set up debugger
support for remote debugging on any hardware target. We'll also get a glimpse into the simplicity of building a
dedicated-purpose device using KOS, where no install or startup scripts are required. We'll simply push the play button
and watch our device boot as a smart rack.

[kondra_note type="info" title="What is an Application in KOS?"]
KOS is an application-based platform. There are many examples of application-based platforms these days. From mobile
devices to smart televisions, it seems everything can run applications. How is KOS any different from these platforms?
The primary difference is the collaboration model for applications. On most consumer devices, applications represent
dedicated experiences that a user interacts with one at a time. These applications are inherently untrusted and are
typically isolated from each other using a form of sandboxing. In KOS, the application model is designed to be
collaborative, where applications work together to enable new functionality. This model emphasizes the ability for
applications to provide core functionality that other applications can use, similar to a library or a microservice

Find more [here](https://kosdev.com/articles/applications/)
[/kondra_note]

### The System Application

Now that we have a way to display our digital content, we need to code the core component that launches it: the System
Application.

[kondra_note type="info" title="What is a System Application?"]
As KOS is designed for dedicated-use devices, the operating system boots into a single user-provided application that
takes over the device and orchestrates all other functionality. In our example, this application simply displays content
on the screen, but in other cases, the system application may load other applications that are dynamically defined and
pushed over the air. In this tutorial, we’ll get our first look at a simple system application.
[/kondra_note]

### Displaying Content

Before we get to the system application, let's consider our options for displaying content on our rack. A common
approach to displaying content is to use a web browser. It's a rich display engine for a broad range of content,
including device user interfaces. KOS has sophisticated browser integration support, including the ability to control
multiple browsers across multiple devices, push real time data, handle asynchronous operations and even provides the
ability for different browsers to communicate directly to each other. However, for our first example we want to focus on
the basics such as learning how to use KOS Studio and debugger integration, so we're going to display content in the
simplest way possible, which is using Java Swing to rotate through images using a timer.

The class below uses basic Swing components to fill the screen with a Jframe that switches between images using a timer.
As this is standard Swing programming, we show the class below without any additional description. This class will be
used in our system application to display content.

Rather than bundling images inside the application [KAB](https://kosdev.com/articles/everything-is-a-kab/), we will use
a data KAB. Data KABs allow you to package assets like images and videos separately from your code, making them easy to
manage and update.

Creating a system application for KOS is as simple as extending the `SystemApplication` class. In the example below, we
use the `load()` method to retrieve our asset KAB and then pass it to our UI.

<snippet-viewer source="tutorials-public" snippet="rack-app@RackApp.java"></snippet-viewer>

### The User Interface

The provided Java code defines a class `RackUI` that displays the book rack content directly from the `KabFile`. It is
launched in the started method of the System Application. It uses the KOS `AdjustableCallback` to handle the timing of
the image rotation.

<snippet-viewer source="tutorials-public" snippet="rack-ui@RackUI.java"></snippet-viewer>

## Step 3: Setup Project

In this section, we'll walk you through setting up a Maven project to create and build a KOS-specific deployable file,
known as a KAB, for deployment. We'll cover the creation of a POM file for building the project and using a descriptor
file to tell the KOS environment where to start. This tutorial assumes you are already familiar with Maven and POM
files.

[kondra_note type="info" title="What are KABs?"]
For now, think of a KAB file as a collection of related files that serve as a universal container for all components of
the operating system, from the kernel to applications. In a KOS release, EVERYTHING is a KAB.
Learn more [here](https://kosdev.com/articles/everything-is-a-kab/)
[/kondra_note]

### Create the POM file

This section will guide you through the pom.xml file setup specifically for developing a KOS application for your
bestselling books rack. Because we are separating our application logic from our image assets, we use a multi-module
Maven project. This consists of a parent `pom.xml` file to manage versions, and two child `pom.xml` files: one for the
application, and another for the assets.

Here is the creation of the parent `pom.xml` file.

#### Section 1: Properties

The `<properties>` section centralizes project-wide settings and versions, ensuring consistency and ease of updates.
This includes specifying the version of KOS to target, the version of Java required by KOS, and the versions of
essential plugins.

```pom.xml
<!-- properties -->
<properties>
    <lombok.version>1.18.38</lombok.version>
    <maven-compiler-plugin.version>3.11.0</maven-compiler-plugin.version>
    <maven-assembly-plugin.version>3.6.0</maven-assembly-plugin.version>
    <java.version>17</java.version>
    <maven.compiler.release>${java.version}</maven.compiler.release>
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

Problem: KOS provides numerous SDKs for building your app, but how do we know which versions of those SDKs to use so
that they are compatible with each other and the version of KOS we are using?

Solution: We use the configuration management concept of a Bill of Materials (BOM). Each version of KOS (2.0, 2.1, 2.2,
etc.) has a BOM that manages the specific compatible versions of SDKs. That way, when we add an SDK dependency, we don't
have to know the specific SDK version. We place these special BOM dependencies as a parent
to ensure version consistency across this Maven module. If this were a multi-module app, as will be used in future
tutorials, it would ensure consistency across all modules.

```pom.xml
<parent>
    <groupId>com.kosdev.kos2.sdk.bom</groupId>
    <artifactId>kos-bom</artifactId>
    <version>2.0.0</version>
    <relativePath/>
</parent>
```

Then, in the `<dependencies>` element, we can add our core-api SDK without having to specify the version.

```pom.xml
<!-- project dependencies -->
<dependencies>
    <dependency>
        <groupId>com.kosdev.kos2.sdk.api</groupId>
        <artifactId>api-core</artifactId>
    </dependency>
</dependencies>    
```

#### Section 4: Profiles

The `<profiles>` section defines build profiles that can be activated under specific conditions. The developer profile
is activated by default and includes plugins for packaging your application into a KAB file, tailored for KOS
development. Using the kos-kab-maven-plugin, you can specify which files you need in your KAB and in which directories
they should be placed.

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

The application `pom.xml` manages the Java compilation and packages the `RackApp` code. It uses `kos.system` type in the
KAB plugin, to indicate that it is being packaged into a system application KAB.

<snippet-viewer source="tutorials-public" snippet="rack-app-pom@pom.mxl"></snippet-viewer>

### Assets POM file

This `pom.xml` is used to package images into a data KAB. The KAB type is a custom type called `tutorial.assets` for the
purpose of this tutorial. Note, this matches the `ASSETS_KAB_TYPE` we defined in our `RackApp` to fetch the assets KAB
and get the images.

<snippet-viewer source="tutorials-public" snippet="rack-assets-pom@pom.mxl"></snippet-viewer>

### Create the Descriptor File

The descriptor.json file provides a standardized method for embedding metadata into KAB files for use in KOS. We will
use it to describe the details of our applications to KOS. In later tutorials, we'll see how descriptor.json can be used
to support localization and even firmware updates. Developers can customize the descriptor.json, but must reserve the
kos.* namespace for standardized sections.

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

`appId`: Defines a unique ID for this application. Every application requires a unique ID to help allocate resources in
KOS. We're building a system application which always has an ID of `system`.

### Place the Content for the Image Rotator

#### Adding Images to Your Project

Add these [four images](https://github.com/kosdev-code/kos-tutorials/tree/main/rack/assets/pics) to the assets
directory.

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

In this section, you learned how to set up a Maven project for a KOS application. We covered creating and configuring a
POM file to manage your project, including specifying dependencies and plugins for building and deploying your app. We
used a BOM to ensure compatibility between SDK versions. We also touched on the KAB plugin to package your project into
a KAB file. By following these steps, you now have a fully configured Maven project ready for deploying a KOS
application.

## Step 4: Build the Project

Run `mvn clean install` then navigate to the `target` directory within your project. You will find several files,
including `kos-tutorials-rack-system-app-0.0.0-SNAPSHOT.kab`. This is the KOS Archive Bundle that packages your application for
  deployment.

Your target directory should contain the JAR and KAB files, along with any other directories that Maven may generate.
This structure represents the skeleton of your project after a build.

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
│       ├── kos-tutorials-rack-system-app-0.0.0-SNAPSHOT.jar
│       └── kos-tutorials-rack-system-app-0.0.0-SNAPSHOT.kab
├── assets/
│   ├── pom.xml  
│   ├── pics
│   │   ├── all.png
│   │   ├── book1.png
│   │   ├── book2.png
│   │   └── book3.png 
│   └── target/
│       ├── kos-tutorials-rack-assets-0.0.0-SNAPSHOT.jar
│       └── kos-tutorials-rack-assets-0.0.0-SNAPSHOT.kab
└── README.md
```

By following these steps, you built your KOS system application and generated the
necessary [artifacts](https://kosdev.com/articles/artifacts-instances-and-stores/). The KAB file, which packages your
application, can then be imported into KOS Studio for further configuration and simulation. This process ensures that
your application is properly packaged and ready for deployment on KOS-supported hardware.

## Step 5: Run the Application

This section guides you through using KOS Studio to create and configure a runnable KOS image for your Java application.
You'll open KOS Studio, create a new image, configure it with the necessary settings, add your source tree, and assign
it to the appropriate section. You'll run the configured image in the KOS Studio simulator to test its functionality.
Finally, you'll debug your applications to step through the code you've written. Following these steps is essential to
running your application on your developer machine.

[kondra_note type="info" title="Why can’t I run a KOS app like any other Java application?"]
A KOS application, packaged into an image, is a complete set of code and dependencies that executes only on the KOS
runtime system. KOS Studio includes a simulator to test applications in a KOS environment before deploying them to
actual hardware.
Learn more about Studio [here](https://kosdev.com/articles/kos-studio/)
[/kondra_note]

### Create the image in Studio

Now that our Java application is written and bundled into a KAB file, let's turn our attention to KOS Studio.

[kondra_note type="info" title="KOS Image"]
In a nutshell, an image, or disk image, is the blueprint for what a KOS node will run when it boots. It’s defined by a
manifest, which outlines the filesystem’s contents and how user space functions. For nodes running Java, this manifest
also specifies all the Java code to execute. Think of the image as an editable version of this manifest—your ticket to
creating a bootable and reusable environment for multiple devices without the hassle of repeated inheritance setups.
[/kondra_note]

First, launch the KOS Studio application. This will lead you to a pillars page. 

<image title="Home: Pillars" name="pillars.png" caption="" />

Go into "Images" and create an image. 

<image title="Images" name="my_images.png" caption="" />

This will launch a wizard with steps that you can follow. 

<image title="Create Image" name="create_image.png" caption="" />

### Source Trees

A source tree points at a source-control repository, and Studio surfaces the artifacts built in it. Those artifacts are available to every developer with the same source tree configured, so a team can develop, build, and test locally - and override a published artifact with a local one when needed.

Within the image creation wizard, you can create a source tree, which launches a source tree creation wizard. Since you have already written the Java application, you should have a folder that contains your code. You can use this as the directory for the source tree. 

<image title="Create Source Trees" name="source_tree.png" caption="" />

Since this is a Java tutorial and you have built the Java KAB, you will notice only the Java artifacts in the Discovered artifacts list, not the UI artifacts. 

<image title="Source Trees: Discovered Artifacts" name="source_tree_artifacts.png" caption="" />

#### Perspectives

Perspectives let you swap published artifacts for your local ones at run time. You can tag your discovered artifacts as Java. Then, run an image with that perspective and Studio uses this source tree's copy instead of the published one — local development without editing the image.

<image title="Source Trees: Perspectives" name="perspectives.png" caption="" />

Now that you have setup source trees, you can make a code change, build, and run your image. It will reflect the changes you just made immediately without any extra setup. 

### KOS Release

Next, you can select a KOS release, that is, the version of KOS to be used for this image. Here, the production release is selected. Additionally, the Graphics option is toggled on so that the UI can be displayed when the image is run. 

<image title="Select a KOS Release" name="kos_release.png" caption="" />

### System Application

A section in KOS is an artifact container where you group your KABs and give them meaning by their
names. More about [Sections](https://kosdev.com/articles/docs_topics_kabs_md/). All images require a system application. Select the `kos-tutorials-rack-system-app` for the System Application. 

<image title="System Application" name="system_application.png" caption="" />

Finally, exit out of the wizard by selecting "Create Image".

### Artifacts

In the artifacts page, we still need to add our assets KAB. Create a new section called `kos.assets` to match the name defined in the `RackApp`. From the "Source Tree" list, add the `kos-tutorials-rack-assets` artifacts to this section.

<image title="Image: Artifacts" name="image_artifacts.png" caption="" />

Notice that each of the artifacts you have selected have a green folder icon. This indicates that the artifacts are currently being served local KAB instances.

### Execute the image

Now, run your KOS application in Studio's built-in simulator by selecting the "Play Image" button.

<image title="Run Image" name="run_image.png" caption="" />

The application first downloads all necessary artifacts, showing the status. The first time
you run an application it may take a while to download the required packages. Subsequent executions open much faster. When downloading is finished, the application will begin to load. You'll see the simulator starting up, with a KOS
splash screen. When the simulator has finished, you should see your bestsellers rack application:

<image title="Application UI" name="simulator.png" caption="" />

### Connection Tools

While running the image, notice that an additional Connected Device window pops up. This contains a home screen, along with connections tools. 

The home screen displays dashboard cards, which summarize the status of the running image. 

<image title="Connected Device" name="connected_device.png" caption="" />

The other tabs on the left of this window are [connection tools](https://kosdev.com/articles/connection-tools-overview/).


Try dropping a break point in your app and give it a whirl!

Congratulations, your first KOS application is up and running! To make changes to your app, simply stop the simulator,
update your code, rebuild the KAB file, and restart the simulator. It's that easy!

This lesson has equipped you with the skills to effectively use KOS Studio to create, configure, and test a runnable KOS
image for your Java application. By following these steps—opening KOS Studio, creating a new image, configuring it with
necessary settings, adding your KAB file, assigning it to the appropriate section, running it in the KOS Studio
simulator, and debugging your code—you've laid the groundwork for successfully running your application on your
developer machine. Great job!

# Conclusion

In this tutorial, we guided you through creating your first KOS application for a smart rack. Starting with an
introduction to KOS, we installed KOS Studio, coded the core application, set up a Maven project, and built the KAB file
using the KAB plugin. We then configured and tested the application in KOS Studio, ensuring it runs smoothly before
deployment.

By the end of the tutorial, you built a functional smart rack application and gained insights into KOS's potential.
You're now equipped with the skills to embark on more ambitious projects, leveraging KOS to create innovative, reliable,
and efficient solutions for various smart device applications.