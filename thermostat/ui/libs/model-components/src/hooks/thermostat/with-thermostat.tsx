import { ThermostatModel } from '@thermostat/thermostat-models';
import { useThermostat } from './use-thermostat';

interface ThermostatProps {
  thermostat: ThermostatModel;
}

type HoCThermostatProps = ThermostatProps;
// react HOC to provide a Thermostat to a component
export function withThermostat<
  T extends HoCThermostatProps = HoCThermostatProps
>(WrappedComponent: React.ComponentType<T>) {
  return (props: Omit<T, keyof ThermostatProps>) => {
    const { model, status, KosModelLoader } = useThermostat();

    return (
      <KosModelLoader {...status}>
        <WrappedComponent {...(props as T)} thermostat={model} />
      </KosModelLoader>
    );
  };
}
