import {
  kosServiceRequest as baseKosServiceRequest,
  createClient,
  type ClientResponse,
  type HttpMethod,
  type IKosServiceRequestParams,
  type KosExecutionContext,
  type PathsByMethod,
} from "@kosdev-code/kos-ui-sdk"
import type { paths } from "./openapi";

/**
 * Type aliases for system API
 */
export type Api = paths;
export type ApiPath = keyof paths;
export type ValidPaths = PathsByMethod<paths>;

/**
 * Get client response type for system API
 */
export type ApiResponse<
  Path extends ApiPath,
  Method extends "get" | "post" | "put" | "delete" = "get"
> = ClientResponse<paths, Path, Method>;

/**
 * Get execution context type for system API
 */
export type ExecutionContext<
  Path extends ApiPath = ApiPath,
  Method extends HttpMethod = "get"
> = KosExecutionContext<paths, Path, Method>;

/**
 * Typed decorator factory for @kosServiceRequest with system API types
 *
 * Provides full IntelliSense and type safety for path, query params, and body
 * based on the system OpenAPI schema.
 *
 * @example
 * ```typescript
 * import { kosServiceRequest } from '../../utils/services/system/daily/service';
 * import { DependencyLifecycle } from '@kosdev-code/kos-ui-sdk';
 *
 * @kosServiceRequest({
 *   path: '/api/...',
 *   method: 'get',
 *   lifecycle: DependencyLifecycle.LOAD
 * })
 * private onDataLoaded(): void {
 *   // Fully typed based on system API
 * }
 * ```
 */
export function kosServiceRequest<
  Path extends ApiPath,
  Method extends HttpMethod = "get",
  Response = any,
  TransformedResponse = Response
>(
  params: IKosServiceRequestParams<
    paths,
    Path,
    Method,
    Response,
    TransformedResponse
  >
) {
  return baseKosServiceRequest<
    paths,
    Path,
    Method,
    Response,
    TransformedResponse
  >(params);
}

/**
 * Create an API client for system
 */
export const api = createClient<paths>();

export default api;
