# example-KOS-sdk-app


## Setup Instructions

-Rename gitignore file to .gitignore
-Rename GitHub directory to .github to allow build automation via github actions
In the .github directory ensure that the development.json file is configured correctly
with the artifactId used in studio and the file name is correct for your project.

Rename your ExampleApp.java to what you'd like, after which you must update the descriptor.json.
Update appClass to  the new class name and package in the descriptor.json file.
Update the appId to be namespaced by your organization name.
Update sdkClassPrefixes with the class prefixes from the sdk code.






