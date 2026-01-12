import devkit from '@nx/devkit';
import { mkdirSync, writeFileSync } from 'fs';
import openapiTS, { astToString } from 'openapi-typescript';
import { join } from 'path';
const { readCachedProjectGraph } = devkit;
const [, , name] = process.argv;

const graph = readCachedProjectGraph();
const project = graph.nodes[name];
const host = project.data.targets.api?.options?.host || 'http://127.0.0.1:8081';
const src = project.data.sourceRoot || project.data.root;

const outFile = join(src, 'utils', 'openapi.d.ts');

mkdirSync(join(src, 'utils'), { recursive: true });

console.log(`Generating API types for ${name} at ${outFile} from ${host}`);

const ast = await openapiTS(
  new URL(`${host}/api/kos/openapi/api`, import.meta.url)
);
const contents = astToString(ast);

// (optional) write to file
writeFileSync(outFile, contents);
