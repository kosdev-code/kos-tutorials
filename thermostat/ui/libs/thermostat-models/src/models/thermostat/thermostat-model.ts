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
} from '@kosdev-code/kos-ui-sdk';

import type { ThermostatOptions } from './types';

export const MODEL_TYPE = 'thermostat-model';

export type ThermostatModel = PublicModelInterface<ThermostatModelImpl>;

// Interface merging for decorator type safety
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface ThermostatModelImpl extends KosLoggerAware {}

@kosModel({ modelTypeId: MODEL_TYPE, singleton: true })
@kosLoggerAware()
export class ThermostatModelImpl implements IKosDataModel, IKosIdentifiable {
  // Registration property for type safety - actual value injected by @kosModel decorator
  static Registration: KosModelRegistrationType<
    ThermostatModel,
    ThermostatOptions
  >;

  id: string;
  // logger property is automatically provided by @kosLoggerAware decorator

  constructor(modelId: string, options: ThermostatOptions) {
    this.id = modelId;
    // logger is automatically injected by @kosLoggerAware decorator

    if (options) {
      // Assign options properties here.
    }
  }

  updateModel(options: ThermostatOptions): void {
    // Update model properties here.
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
