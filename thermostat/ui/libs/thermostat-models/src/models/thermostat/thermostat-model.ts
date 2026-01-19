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
  // DependencyLifecycle,
  kosConfigProperty,
  kosTopicHandler,
} from '@kosdev-code/kos-ui-sdk';

// import { SYSTEM_DAILY as KOS_APP } from '../../utils/services-index';

import type { ThermostatMode, ThermostatOptions } from './types';

/**
 * Unique identifier used by the KOS framework to register and resolve
 * this model.
 */
export const MODEL_TYPE = 'thermostat-model';

/**
 * Default temperature used when no backend or configuration value
 * is available.
 */
const DEFAULT_TEMPERATURE = 70;

/**
 * Public-facing thermostat model type exposed to the UI.
 *
 * This type hides implementation details and only exposes the
 * public interface defined by `PublicModelInterface`.
 */
export type ThermostatModel = PublicModelInterface<ThermostatModelImpl>;

/**
 * Interface merging to ensure decorator-provided members (e.g. `logger`)
 * are visible to TypeScript.
 *
 * @remarks
 * This is required because `@kosLoggerAware()` injects properties at runtime.
 */
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface ThermostatModelImpl extends KosLoggerAware {}

// const { kosServiceRequest } = KOS_APP;

/**
 * Thermostat data model responsible for:
 *
 * - Tracking current thermostat mode and temperature
 * - Synchronizing min/max temperature configuration with the backend
 * - Responding to websocket topic updates
 *
 * @remarks
 * - Registered as a **singleton**
 * - Lifecycle managed by the KOS framework
 */
@kosModel({ modelTypeId: MODEL_TYPE, singleton: true })
@kosLoggerAware()
export class ThermostatModelImpl implements IKosDataModel, IKosIdentifiable {
  /**
   * Static registration metadata used by the KOS framework.
   */
  static Registration: KosModelRegistrationType<
    ThermostatModel,
    ThermostatOptions
  >;

  /**
   * Unique model identifier assigned by the framework.
   */
  id: string;

  /**
   * Current operating mode of the thermostat.
   */
  mode: ThermostatMode;

  /**
   * Current temperature reading.
   */
  temperature: number;

  /**
   * Configuration-backed maximum temperature value.
   *
   * @remarks
   * This property mirrors the backend configuration at:
   * `system:service:thermostat.maxTemp`
   */
  @kosConfigProperty({
    path: 'system:service:thermostat',
    attribute: 'maxTemp',
  })
  maxTemp!: KosConfigProperty<number>;

  /**
   * Convenience accessor for the resolved maximum temperature.
   */
  get maxTempValue() {
    return this.maxTemp.value;
  }

  /**
   * Configuration-backed minimum temperature value.
   *
   * @remarks
   * This property mirrors the backend configuration at:
   * `system:service:thermostat.minTemp`
   */
  @kosConfigProperty({
    path: 'system:service:thermostat',
    attribute: 'minTemp',
  })
  minTemp!: KosConfigProperty<number>;

  /**
   * Convenience accessor for the resolved minimum temperature.
   */
  get minTempValue() {
    return this.minTemp.value;
  }

  /**
   * Creates a new thermostat model instance.
   *
   * @param modelId Unique identifier assigned by the framework
   */
  constructor(modelId: string) {
    this.id = modelId;
    this.mode = 'OFF';
    this.temperature = DEFAULT_TEMPERATURE;
  }

  /**
   * Updates thermostat state from incoming data.
   *
   * @remarks
   * Centralized update logic to prevent duplication across
   * service handlers and topic handlers.
   *
   * @param options Latest thermostat state
   */
  updateModel(options: ThermostatOptions): void {
    this.logger.debug('updateModel:', options);

    this.mode = options.mode;
    this.temperature = options.temperature;
  }

  /**
   * Fire off a service request when the model loads to retrieve the initial
   * thermostat mode and temperature.
   *
   * @remarks
   * Note: This and its dependencies are currently commented out for production
   * purposes - the service is used during development.
   *
   * @param data ThermostatOptions
   */
  // @kosServiceRequest({
  //   path: '/api/system/thermostat/service/state',
  //   lifecycle: DependencyLifecycle.LOAD,
  // })
  // getState(data: ThermostatOptions) {
  //   this.logger.debug('getState:', data);
  //   this.updateModel(data);
  // }

  /**
   * Handles thermostat state updates published over websocket.
   *
   * @remarks
   * Subscribes to `/app/system/thermostat/state` and updates
   * local model state accordingly.
   *
   * @param data Incoming thermostat state
   */
  @kosTopicHandler({
    topic: '/app/system/thermostat/state',
    websocket: true,
  })
  stateHandler(data: ThermostatOptions): void {
    this.logger.debug('stateHandler:', data);
    this.updateModel(data);
  }

  /**
   * Increments the maximum allowed temperature by one degree.
   */
  increaseMaxTemp(): void {
    const value = (this.maxTemp.rawValue ?? DEFAULT_TEMPERATURE) + 1;

    this.logger.debug('increaseMaxTemp:', value);
    this.maxTemp.updateProperty(value);
  }

  /**
   * Decrements the maximum allowed temperature by one degree.
   */
  decreaseMaxTemp(): void {
    const value = (this.maxTemp.rawValue ?? DEFAULT_TEMPERATURE) - 1;

    this.logger.debug('decreaseMaxTemp:', value);
    this.maxTemp.updateProperty(value);
  }

  /**
   * Increments the minimum allowed temperature by one degree.
   */
  increaseMinTemp(): void {
    const value = (this.minTemp.rawValue ?? DEFAULT_TEMPERATURE) + 1;

    this.logger.debug('increaseMinTemp:', value);
    this.minTemp.updateProperty(value);
  }

  /**
   * Decrements the minimum allowed temperature by one degree.
   */
  decreaseMinTemp(): void {
    const value = (this.minTemp.rawValue ?? DEFAULT_TEMPERATURE) - 1;

    this.logger.debug('decreaseMinTemp:', value);
    this.minTemp.updateProperty(value);
  }

  // ------------------- LIFECYCLE ----------------------------

  /**
   * Called once when the model is initialized.
   */
  async init(): Promise<void> {
    this.logger.debug(`initializing thermostat ${this.id}`);
  }

  /**
   * Called when the model is fully loaded and ready.
   */
  async load(): Promise<void> {
    this.logger.debug(`loading thermostat ${this.id}`);
  }
}

/**
 * Public model export used by consumers to access the thermostat model.
 */
export const Thermostat = ThermostatModelImpl.Registration;
