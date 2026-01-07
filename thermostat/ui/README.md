# thermostat

The project has been scaffolded using the `kosui` CLI and is a fully configured NX Monorepo that can support multiple UI applications, libraries and KOS model projects in the single codebase.

## Getting Started

### Install the kosui CLI

If not done already, install the `kosui` CLI tooling:

```sh
npm install -g @kosdev-code/kos-ui-cli
```

### Install dependencies

After cloning the repo install dependencies:

```sh
npm install
```

### Set up environment variables

You will need to set up some environment variables before running the `kab` target so that the tooling knows where to find Java and a Studio installation on the local system.

The `kosui` has the ability to auto-detect and populate these variables:

```sh
kosui env
--- Discover Studio Environment Variables ---
The following will discover Studio Environment Variables.
findEnv Studio Home: /Users/dev/Applications/kosStudio.app
Java Home: /usr/bin/java
```

The variables are stored in the file .env.local at the root of the environment:

```
#  Path to the Java binary
JAVA_CMD='/usr/bin/java'
#  Path to the kos installation
KOS_INSTALL_PATH='/Users/dev/Applications/kosStudio.app'
```

## Building an application

Build a project using the NX command line or the VS Code plugin if installed:

You can run a build for a single package in the monorepo. This command will run the build target for the `thermostat-ui` project and all of it dependent projects:

```sh
npx nx run thermostat-ui:build
```

Alternately, you can run the `build` target for several projects at once:

```sh
npx nx run-many -t build
```

For more information on running NX tasks refer to the the documentation on https://nx.dev/features/run-tasks.

## Dev Server

The workspace provides a dev server that can be used during active development to ensure that code updates are working as expected with real time feedback and live updates.

Use the serve command in the VS Code plugin or alternately use the command line to start the dev server for the training-zer4-ui application package.

```sh
npx nx run thermostat-ui:serve
```

When up and running, navigate in your browser to http://localhost:4200/?host=http://localhost:8081# and you should be presented with a KOS Welcome screen.

Notice the URL has an additional query parameter host=http://localhost:8081. This is telling the UI SDK where to find the KOS service host which is the location where KOS is running which is useful when running in development against an instance running on a Raspberry Pi or some other remote host. This parameter enables development against a “real” java backend rather relying on mocks or dummy data.

## Building a Kab

The environment comes pre-configured to be able to bundle applications into Kab files for easy publishing to Studio or for local testing as needed.

Create a kab using the kab task in the VS Code plugin or executing the corresponding command line tool:

```sh
npx nx run thermostat-ui:kab
```

The build process will create a test kab that can be consumed by Studio automatically without needing to leave the development environment.
