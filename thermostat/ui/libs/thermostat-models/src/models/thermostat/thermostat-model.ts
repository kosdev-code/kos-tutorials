/* eslint-disable @typescript-eslint/no-unsafe-declaration-merging */

import type {
  IKosDataModel,
  IKosIdentifiable,
  PublicModelInterface,
  KosModelRegistrationType,
  KosConfigProperty,
} from '@kosdev-code/kos-ui-sdk';
import {
  kosModel,
  kosLoggerAware,
  KosLoggerAware,
  DependencyLifecycle,
  kosConfigProperty,
  kosTopicHandler,
} from '@kosdev-code/kos-ui-sdk';
import { SYSTEM_DAILY as KOS_APP } from '../../utils/services-index';
import type { ThermostatMode, ThermostatOptions } from './types';

export const MODEL_TYPE = 'thermostat-model';

const DEFAULT_TEMPERATURE = 70;

export type ThermostatModel = PublicModelInterface<ThermostatModelImpl>;

// Interface merging for decorator type safety
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface ThermostatModelImpl extends KosLoggerAware {}

const { kosServiceRequest } = KOS_APP;

@kosModel({ modelTypeId: MODEL_TYPE, singleton: true })
@kosLoggerAware()
export class ThermostatModelImpl implements IKosDataModel, IKosIdentifiable {
  static Registration: KosModelRegistrationType<
    ThermostatModel,
    ThermostatOptions
  >;

  id: string;
  mode: ThermostatMode;
  temperature: number;

  @kosConfigProperty({
    path: 'system:service:thermostat',
    attribute: 'maxTemp',
  })
  maxTemp!: KosConfigProperty<number>;

  get maxTempValue() {
    return this.maxTemp.value;
  }

  @kosConfigProperty({
    path: 'system:service:thermostat',
    attribute: 'minTemp',
  })
  minTemp!: KosConfigProperty<number>;

  get minTempValue() {
    return this.minTemp.value;
  }

  constructor(modelId: string) {
    this.id = modelId;
    this.mode = 'OFF';
    this.temperature = DEFAULT_TEMPERATURE;
  }

  updateModel(options: ThermostatOptions): void {
    console.log('updateModel:', options);

    this.mode = options.mode;
    this.temperature = options.temperature;
  }

  // @kosServiceRequest({
  //   path: '/api/system/thermostat/service/state',
  //   lifecycle: DependencyLifecycle.LOAD,
  // })
  // getState(data: ThermostatOptions) {
  //   console.log('getState:', data);

  //   this.updateModel(data);
  // }

  @kosTopicHandler({ topic: '/app/system/thermostat/state', websocket: true })
  stateHandler(data: ThermostatOptions) {
    console.log('stateHandler:', data);

    this.updateModel(data);
  }

  increaseMaxTemp() {
    const value = (this.maxTemp.rawValue || DEFAULT_TEMPERATURE) + 1;

    console.log('increaseMaxTemp:', value);

    this.maxTemp.updateProperty(value);
  }

  decreaseMaxTemp() {
    const value = (this.maxTemp.rawValue || DEFAULT_TEMPERATURE) - 1;

    console.log('decreaseMaxTemp:', value);

    this.maxTemp.updateProperty(value);
  }

  increaseMinTemp() {
    const value = (this.minTemp.rawValue || DEFAULT_TEMPERATURE) + 1;

    console.log('increaseMinTemp:', value);

    this.minTemp.updateProperty(value);
  }

  decreaseMinTemp() {
    const value = (this.minTemp.rawValue || DEFAULT_TEMPERATURE) - 1;

    console.log('decreaseMinTemp:', value);

    this.minTemp.updateProperty(value);
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
