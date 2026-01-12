import { composePlugins, withNx } from '@nx/webpack';
import { withReact } from '@nx/react';
import * as webpack from 'webpack';
import { withModuleFederation } from '@nx/react/module-federation';
import { merge } from 'webpack-merge';

import baseConfig from './module-federation.config';
import { withPluginDevAggregator } from '@kosdev-code/kos-ui-plugin/webpack';

import * as dotenv from 'dotenv';

dotenv.config({
  path: '.env',
});

const config = {
  ...baseConfig,
};

function getKosEnv() {
  const kosKeys = Object.keys(process.env).filter((key) =>
    key.startsWith('KOS_')
  );
  return kosKeys;
}

// Configure plugin dev servers based on environment variables
// These are set automatically by `kosui dev` command
const useLocalPlugins = process.env.USE_LOCAL_PLUGINS === 'true';
const mergeWithBackend = process.env.MERGE_WITH_BACKEND === 'true';

// Plugin dev servers are configured via environment variables
// Format: <PLUGIN_ALIAS>_DEV=true where alias is from .kos.json
// Example: BEVERAGE_POUR_DEV=true
const pluginServers = Object.keys(process.env)
  .filter((key) => key.endsWith('_DEV') && process.env[key] === 'true')
  .map((key) => {
    // Extract plugin alias from env var (e.g., BEVERAGE_POUR_DEV -> beverage-pour)
    const alias = key.replace(/_DEV$/, '').toLowerCase().replace(/_/g, '-');
    // Port is set via <PLUGIN_ALIAS>_PORT env var
    const portKey = key.replace(/_DEV$/, '_PORT');
    const port = process.env[portKey] || '4201';
    return `http://localhost:${port}`;
  });

// Nx plugins for webpack to build config object from Nx options and context.
export default composePlugins(
  withNx({}),
  withReact(),
  withModuleFederation(config),
  // Add plugin dev aggregator for multi-plugin development
  useLocalPlugins
    ? withPluginDevAggregator({
        pluginServers,
        mergeWithBackend,
        fallbackToBackend: pluginServers.length === 0,
        backendUrl: 'http://localhost:8081',
        verbose: process.env.VERBOSE_PLUGIN_DEV === 'true',
      })
    : (config) => config,
  (config, { options, context }) =>
    merge(config, {
      resolve: {
        fallback: {
          path: false,
          os: false,
          crypto: false,
        },
      },
      module: {
        rules: [
          {
            test: /\.svg$/i,
            issuer: /\.[jt]sx?$/,
            use: [{ loader: '@svgr/webpack' }],
          },
        ],
      },
      mode: (process.env.NODE_ENV || config.mode) as any,
      plugins: [
        new webpack.EnvironmentPlugin({
          NODE_ENV: 'production',
          USE_LOCAL_PLUGINS: 'false', // Default to false, set to 'true' in dev mode
          ...Object.fromEntries(
            getKosEnv().map((key) => [key, process.env[key]])
          ),
        }),
      ],
    })
);
