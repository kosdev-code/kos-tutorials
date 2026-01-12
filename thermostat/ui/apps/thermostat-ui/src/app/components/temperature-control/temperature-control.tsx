import { DecreaseButton } from '../decrease-button';
import { IncreaseButton } from '../increase-button';
import { TemperatureControlDisplay } from '../temperature-control-display';
import { Container } from './temperature-control.styles';

interface TemperatureControlProps {
  mode: 'cooling' | 'heating';
  temperature: number;
}

export const TemperatureControl = ({
  mode,
  temperature = 70,
}: TemperatureControlProps) => {
  return (
    <Container>
      <IncreaseButton />
      <TemperatureControlDisplay mode={mode} temperature={temperature} />
      <DecreaseButton />
    </Container>
  );
};
