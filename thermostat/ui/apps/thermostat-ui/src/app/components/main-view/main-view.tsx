import styled from '@emotion/styled';
import { kosComponent, KosLog } from '@kosdev-code/kos-ui-sdk';
import { useThermostat } from '@thermostat/model-components';
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

  return (
    <Container>
      <TemperatureControl mode="heating" temperature={68} />
      <TemperatureControl mode="cooling" temperature={74} />
      <TemperatureDisplay
        mode={thermostat?.mode}
        temperature={thermostat?.temperature}
      />
    </Container>
  );
});
