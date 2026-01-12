/* eslint-disable @typescript-eslint/no-unsafe-declaration-merging */

import type {
  IKosDataModel,
  IKosIdentifiable,
  PublicModelInterface,
  KosModelRegistrationType,
} from '@kosdev-code/kos-ui-sdk';
import {
  kosModel,
  kosLoggerAware,
  KosLoggerAware,
  DependencyLifecycle,
} from '@kosdev-code/kos-ui-sdk';
import { SYSTEM_DAILY as KOS_APP } from '../../utils/services-index';
import type { ThermostatOptions } from './types';

export const MODEL_TYPE = 'thermostat-model';

export type ThermostatModel = PublicModelInterface<ThermostatModelImpl>;

// Interface merging for decorator type safety
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface ThermostatModelImpl extends KosLoggerAware {}

const { kosServiceRequest: appServiceRequest } = KOS_APP;

@kosModel({ modelTypeId: MODEL_TYPE, singleton: true })
@kosLoggerAware()
export class ThermostatModelImpl implements IKosDataModel, IKosIdentifiable {
  static Registration: KosModelRegistrationType<
    ThermostatModel,
    ThermostatOptions
  >;

  id: string;
  mode: 'cooling' | 'heating' | 'off';
  temperature: number;

  constructor(modelId: string) {
    this.id = modelId;
    this.mode = 'off';
    this.temperature = 70;
  }

  @appServiceRequest({
    path: '/api/system/thermostat/service/state',
    lifecycle: DependencyLifecycle.LOAD,
  })
  getState(data: any) {
    this.mode = data.mode.toLowerCase();
    this.temperature = data.temperature;

    console.log('getState - data:', data);
  }

  // -------------------LIFECYCLE----------------------------

  async init(): Promise<void> {
    this.logger.debug(`initializing thermostat ${this.id}`);
  }

  async load(): Promise<void> {
    this.logger.debug(`loading thermostat ${this.id}`);
  }
}

export const Thermostat = ThermostatModelImpl.Registration;
