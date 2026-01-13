import { ThermostatMode } from '@thermostat/thermostat-models';
import {
  Container,
  CoolingMode,
  HeatingMode,
  Icon,
  IconContainer,
  Mode,
  Temperature,
} from './temperature-display.styles';

const modes = {
  COOLING: {
    label: 'Cooling',
    icon: '/assets/icons/cooling-icon.svg',
    alt: 'Cooling Icon',
    Component: CoolingMode,
  },
  HEATING: {
    label: 'Heating',
    icon: '/assets/icons/heating-icon.svg',
    alt: 'Heating Icon',
    Component: HeatingMode,
  },
  OFF: {
    label: 'Off',
    icon: undefined,
    Component: Mode,
  },
} as const;

interface TemperatureDisplayProps {
  mode?: ThermostatMode;
  temperature?: number;
}

export const TemperatureDisplay = ({
  mode,
  temperature,
}: TemperatureDisplayProps) => {
  const config = mode ? modes[mode] : null;

  return (
    <Container>
      <IconContainer>
        {config?.icon && <Icon alt={config.alt} src={config.icon} />}
      </IconContainer>

      {temperature !== undefined && <Temperature>{temperature}°F</Temperature>}

      {config && <config.Component>{config.label}</config.Component>}
    </Container>
  );
};
