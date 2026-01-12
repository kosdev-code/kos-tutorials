import {
  Container,
  CoolingMode,
  HeatingMode,
  Icon,
  Mode,
  Temperature,
} from './temperature-display.styles';

const modes = {
  cooling: {
    label: 'Cooling',
    icon: '/assets/icons/cooling-icon.svg',
    alt: 'Cooling Icon',
    Component: CoolingMode,
  },
  heating: {
    label: 'Heating',
    icon: '/assets/icons/heating-icon.svg',
    alt: 'Heating Icon',
    Component: HeatingMode,
  },
  off: {
    label: 'Off',
    icon: undefined,
    Component: Mode,
  },
} as const;

interface TemperatureDisplayProps {
  mode?: 'cooling' | 'heating' | 'off';
  temperature?: number;
}

export const TemperatureDisplay = ({
  mode,
  temperature,
}: TemperatureDisplayProps) => {
  const config = mode ? modes[mode] : null;

  return (
    <Container>
      {config?.icon && <Icon alt={config.alt} src={config.icon} />}

      {temperature !== undefined && <Temperature>{temperature}°F</Temperature>}

      {config && <config.Component>{config.label}</config.Component>}
    </Container>
  );
};
