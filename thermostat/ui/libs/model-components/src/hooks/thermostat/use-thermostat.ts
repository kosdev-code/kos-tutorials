import { useKosModel } from '@kosdev-code/kos-ui-sdk';
import { Thermostat, ThermostatModel } from '@thermostat/thermostat-models';

export const useThermostat = () => {
  const modelId = Thermostat.type;
  const result = useKosModel<ThermostatModel>({
    modelId,
    modelType: Thermostat.type,
    options: {},
  });

  return result;
};
