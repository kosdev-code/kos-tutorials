import { kosComponent, KosLog } from '@kosdev-code/kos-ui-sdk';

const log = KosLog.createLogger({ name: 'main-view' });
log.debug('main-view component loaded');

export const MainView: React.FunctionComponent = kosComponent(() => {
  return <div>Thermostat</div>;
});
