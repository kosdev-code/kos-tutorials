import {
  ErrorBoundaryWithFallback,
  KosTranslationProvider,
  LoadingMessage,
  getLogLevel,
  initKosProvider,
} from '@kosdev-code/kos-ui-sdk';
import {
  FloatingPluginExplorer,
  KosPluginProvider,
} from '@kosdev-code/kos-ui-plugin';
import log from 'loglevel';
import { Suspense } from 'react';
import { KosCoreContextProvider } from './registration';
import './app.css';
import { HashRouter as Router, Routes, Route } from 'react-router-dom';
import { MainView } from './components/main-view';

const level = getLogLevel();

log.setLevel(level);

// Configure plugin loading based on environment and URL parameters
// This supports both internal development (via `kosui dev`) and third-party development (via query params)
const getPluginConfig = () => {
  // Check environment variable (set by `kosui dev` command)
  const useLocalPlugins = process.env.USE_LOCAL_PLUGINS === 'true';

  // Check URL parameter for third-party dev: ?pluginDevServers=http://localhost:4201,...
  const params = new URLSearchParams(window.location.search);
  const devServers = params.get('pluginDevServers');

  if (useLocalPlugins || devServers) {
    return {
      // Explicitly set pluginBaseUrl to prevent ?host= query param from overriding
      pluginBaseUrl: window.location.origin,
      pluginApiPath: '/api/kos/ui/plugins/contexts',
      pluginContext: { group: 'thermostat' }, // Plugin context from .kos.json
    };
  }

  return undefined;
};

const App = () => {
  const pluginConfig = getPluginConfig();

  return (
    <ErrorBoundaryWithFallback>
      <Suspense fallback={<LoadingMessage theme={'dark'}></LoadingMessage>}>
        <KosCoreContextProvider>
          <KosPluginProvider pluginConfig={pluginConfig}>
            <FloatingPluginExplorer />
            <KosTranslationProvider>
              <div className="App">
                <Router window={window}>
                  <Routes>
                    <Route index element={<MainView />} />
                    <Route path="/main" element={<MainView />} />
                  </Routes>
                </Router>
              </div>
            </KosTranslationProvider>
          </KosPluginProvider>
        </KosCoreContextProvider>
      </Suspense>
    </ErrorBoundaryWithFallback>
  );
};

export default App;
