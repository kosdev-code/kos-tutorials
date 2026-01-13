import { DecreaseButton } from '../decrease-button';
import { IncreaseButton } from '../increase-button';
import { TemperatureControlDisplay } from '../temperature-control-display';
import { Container } from './temperature-control.styles';

interface TemperatureControlProps {
  mode: 'cooling' | 'heating';
  onDecrease?: () => void;
  onIncrease?: () => void;
  temperature?: number;
}

export const TemperatureControl = ({
  mode,
  onDecrease,
  onIncrease,
  temperature = 70,
}: TemperatureControlProps) => {
  return (
    <Container>
      <IncreaseButton onPointerDown={onIncrease} />
      <TemperatureControlDisplay mode={mode} temperature={temperature} />
      <DecreaseButton onPointerDown={onDecrease} />
    </Container>
  );
};
