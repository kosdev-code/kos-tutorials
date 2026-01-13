export type ThermostatMode = 'COOLING' | 'HEATING' | 'OFF';

export interface ThermostatOptions {
  mode: ThermostatMode;
  temperature: number;
}
