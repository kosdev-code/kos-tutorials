import chalk from 'chalk';
import { execSync } from 'child_process';
import { readFileSync, existsSync } from 'fs';
const [, , sdk, ddk] = process.argv;
const kos = [
  '@kosdev-code/kos-dispense-sdk',
  '@kosdev-code/kos-ui-plugin',
  '@kosdev-code/kos-ui-sdk',
]
  .map((dep) => `${dep}@${sdk}`)
  .join(' ');

const freestyle = ['@kosdev-code/kos-freestyle-sdk']
  .map((dep) => `${dep}@${sdk}`)
  .join(' ');

const cms = ['@kosdev-code/kos-cms-sdk']
  .map((dep) => `${dep}@${sdk}`)
  .join(' ');

const ddkPackages = [
  '@kosdev-code/kos-ddk',
  '@kosdev-code/kos-ddk-components',
  '@kosdev-code/kos-ddk-models',
  '@kosdev-code/kos-ddk-model-components',
  '@kosdev-code/kos-ddk-styles',
  '@kosdev-code/kos-ui-plugin-dev',
];

const ddkDeps = ddkPackages.map((dep) => `${dep}@${ddk}`).join(' ');

// Check if package.json exists and contains any DDK dependencies
let hasDdkDeps = false;
if (existsSync('package.json')) {
  const packageJson = JSON.parse(readFileSync('package.json', 'utf8'));
  const allDeps = {
    ...packageJson.dependencies,
    ...packageJson.devDependencies,
    ...packageJson.peerDependencies,
  };

  hasDdkDeps = ddkPackages.some((dep) => allDeps[dep]);
}

const flags = [
  '--loglevel error',
  '--no-progress',
  '--no-audit',
  '--no-fund',
  "--save-prefix='~'",
].join(' ');
console.log(chalk.green('Updating dependencies...'));
console.log(chalk.green('Updating kos dependencies...'));
execSync(`npm i ${kos}  --save ${flags}`, {
  stdio: 'inherit',
});

console.log(chalk.green('Updating freestyle dependencies...'));
execSync(`npm i ${freestyle}  --save ${flags}`, {
  stdio: 'inherit',
});

console.log(chalk.green('Updating cms dependencies...'));
execSync(`npm i ${cms} --save ${flags}`, {
  stdio: 'inherit',
});

if (hasDdkDeps) {
  console.log(chalk.green('Updating ddk dependencies...'));
  execSync(`npm i ${ddkDeps} --save ${flags}`, {
    stdio: 'inherit',
  });
} else {
  console.log(
    chalk.yellow('Skipping DDK dependencies - none found in package.json')
  );
}

console.log(chalk.green('Updating kos-ui-cli...'));
execSync(`npm i -g @kosdev-code/kos-ui-cli@${sdk} ${flags}`, {
  stdio: 'inherit',
});
