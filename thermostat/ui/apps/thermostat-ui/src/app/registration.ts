import { KosModelRegistry } from '@kosdev-code/kos-dispense-sdk';
import { initKosProvider } from '@kosdev-code/kos-ui-sdk';
import { Thermostat } from '@thermostat/thermostat-models';

KosModelRegistry.dispense.models().model(Thermostat);

const { KosCoreContextProvider } = initKosProvider();

export { KosCoreContextProvider };
