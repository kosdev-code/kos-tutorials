import { KosModelRegistry } from '@kosdev-code/kos-dispense-sdk';
import { initKosProvider } from '@kosdev-code/kos-ui-sdk';
import { Thermostat } from '@thermostat/thermostat-models';

// Registers core dispense models and also registers the custom Thermostat
// model. If we don't register the Thermostat model, the application won't
// function as it's a required dependency for the UI to function.
KosModelRegistry.dispense.models().model(Thermostat);

const { KosCoreContextProvider } = initKosProvider();

export { KosCoreContextProvider };
