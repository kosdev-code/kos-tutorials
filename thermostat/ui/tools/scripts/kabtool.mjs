#!/usr/bin/env node
import devkit from '@nx/devkit';
import { spawn } from 'child_process';
import fs from 'fs';
import path from 'path';
const { readCachedProjectGraph } = devkit;

import chalk from 'chalk';
import dotenv from 'dotenv';
import dotenvExpand from 'dotenv-expand';

const [, , buildType, projectName] = process.argv;
const graph = readCachedProjectGraph();
const project = graph.nodes[projectName];

const kabOptions = project.data?.targets?.kab?.options;
const config = dotenv.config({
  path: path.resolve(process.cwd(), '.env.local'),
});
dotenvExpand.expand(config);

const dotKabFilePath = path.resolve(
  process.cwd(),
  project.data.root,
  '.kos.json'
);

const dotKabJson = JSON.parse(fs.readFileSync(dotKabFilePath, 'utf8'));
const version = dotKabJson.version;
const installPath = process.env.KOS_INSTALL_PATH;
const kabJarPath = process.env.KOS_KABTOOL_JAR;
const jarLocation =
  process.platform === 'darwin'
    ? path.join('Contents', 'MacOS', 'lib', 'jars', 'kabtool.jar')
    : path.join('release', 'lib', 'jars', 'kabtool.jar');
const jarPath = kabJarPath ?? path.resolve(installPath, jarLocation);
const jar = path.resolve(jarPath);
const command = process.env.JAVA_CMD;

const name = dotKabJson.name;
const id = dotKabJson.id;
const type = dotKabJson.type;
const qualifier = dotKabJson.qualifier;
const inputFile = path.resolve(kabOptions.outputPath, kabOptions.zipName);
const outputFile = path.resolve(kabOptions.outputPath, kabOptions.kabName);

const baseBuildArgs = [
  '-b',
  '-t',
  type,
  '-n',
  name,
  '-v',
  version,
  '-z',
  inputFile,
];

const qualifierArgs = qualifier ? ['-q', qualifier] : [];
const idArgs = id ? ['-i', id] : [];

const buildArgs = [...baseBuildArgs, ...qualifierArgs, ...idArgs, outputFile];
const helpArgs = ['-h'];

const listArgs = ['-l', outputFile];
const cmdMap = {
  build: buildArgs,
  help: helpArgs,
  list: listArgs,
};
const cmdArgs = cmdMap[buildType] || helpArgs;

const args = ['-Dfile.encoding=UTF-8', '-jar', jar, ...cmdArgs];

const ps = spawn(command, args);

ps.stdout.on('data', (data) => {
  console.log(chalk.blue(`studio: ${data}`));
});

ps.stderr.on('data', (data) => {
  console.error(chalk.red(`studio: ${data}`));
});

ps.on('close', (code) => {
  console.log(`child process exited with code ${code}`);
});
