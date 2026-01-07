import {
  DispenseExtensionManager,
  KosModelRegistry,
} from '@kosdev-code/kos-dispense-sdk';

import { initKosProvider } from '@kosdev-code/kos-ui-sdk';

KosModelRegistry.dispense.models();

const dispenseExtensionManager = new DispenseExtensionManager();

const { KosCoreContextProvider } = initKosProvider();

export { KosCoreContextProvider };
