import { KosLog, type ServiceResponse } from '@kosdev-code/kos-ui-sdk';

import API, { type ApiPath, type ApiResponse } from '../../../utils/service';

const log = KosLog.createLogger({
  name: 'thermostat-service',
  group: 'Services',
});

const SERVICE_PATH: ApiPath = '/api/system/thermostat/service/state';
export type ThermostatResponse = ApiResponse<typeof SERVICE_PATH, 'get'>;

/**
 * @category Service
 * Retrieves the initial  thermostat data.
 */
export const getThermostatState = async (): Promise<
  ServiceResponse<ThermostatResponse>
> => {
  log.debug('sending GET for  thermostat');
  return await API.get(SERVICE_PATH);
};
