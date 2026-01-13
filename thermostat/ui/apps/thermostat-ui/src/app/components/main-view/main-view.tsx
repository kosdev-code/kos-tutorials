import styled from '@emotion/styled';
import { kosComponent, KosLog } from '@kosdev-code/kos-ui-sdk';
import { useThermostat } from '@thermostat/model-components';
import { useCallback } from 'react';
import { TemperatureControl } from '../temperature-control';
import { TemperatureDisplay } from '../temperature-display';

const log = KosLog.createLogger({ name: 'main-view' });
log.debug('main-view component loaded');

const Container = styled.div`
  align-items: center;
  display: flex;
  gap: 3px;
  height: 450px;
  justify-content: center;
  width: 900px;
`;

export const MainView: React.FunctionComponent = kosComponent(() => {
  const { model: thermostat } = useThermostat();

  const increaseMaxTemp = useCallback(
    () => thermostat?.increaseMaxTemp(),
    [thermostat]
  );

  const decreaseMaxTemp = useCallback(
    () => thermostat?.decreaseMaxTemp(),
    [thermostat]
  );

  const increaseMinTemp = useCallback(
    () => thermostat?.increaseMinTemp(),
    [thermostat]
  );

  const decreaseMinTemp = useCallback(
    () => thermostat?.decreaseMinTemp(),
    [thermostat]
  );

  return (
    <Container>
      <TemperatureControl
        mode="heating"
        onDecrease={decreaseMinTemp}
        onIncrease={increaseMinTemp}
        temperature={thermostat?.minTempValue}
      />
      <TemperatureControl
        mode="cooling"
        onDecrease={decreaseMaxTemp}
        onIncrease={increaseMaxTemp}
        temperature={thermostat?.maxTempValue}
      />
      <TemperatureDisplay
        mode={thermostat?.mode}
        temperature={thermostat?.temperature}
      />
    </Container>
  );
});
