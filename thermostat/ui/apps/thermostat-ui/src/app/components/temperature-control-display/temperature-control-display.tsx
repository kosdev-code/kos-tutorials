import {
  CoolingContainer,
  HeatingContainer,
} from './temperature-control-display.styles';

interface TemperatureControlDisplayProps {
  mode: 'cooling' | 'heating';
  temperature: number;
}

export const TemperatureControlDisplay = ({
  mode,
  temperature = 70,
}: TemperatureControlDisplayProps) => {
  const Container = mode === 'cooling' ? CoolingContainer : HeatingContainer;

  return <Container>{temperature}</Container>;
};
