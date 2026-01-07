import {
  createClient,
  type ClientResponse,
  type PathsByMethod,
  kosServiceRequest as baseKosServiceRequest,
  type IKosServiceRequestParams,
  type HttpMethod,
  type KosExecutionContext,
} from '@kosdev-code/kos-ui-sdk';
import { paths } from './openapi';

const api = createClient<paths>();

export type KosApi = paths;
export type ApiPath = keyof paths;
export type ValidPaths = PathsByMethod<paths>;

export type GetPaths = ValidPaths['get'];
export type PostPaths = ValidPaths['post'];
export type PutPaths = ValidPaths['put'];
export type DeletePaths = ValidPaths['delete'];

export type ApiResponse<
  Path extends ApiPath,
  Method extends 'get' | 'post' | 'put' | 'delete' = 'get'
> = ClientResponse<KosApi, Path, Method>;

/**
 * Type alias for strongly-typed execution context.
 * The context is automatically injected as the last parameter to methods
 * decorated with @kosServiceRequest (without lifecycle) or @kosFuture.
 *
 * @example With @kosServiceRequest
 * ```typescript
 * @kosServiceRequest({
 *   path: PATH_EXAMPLE,
 *   method: 'post'
 * })
 * async performAction(
 *   data: any,
 *   ctx: ExecutionContext<typeof PATH_EXAMPLE, 'post'>
 * ): Promise<void> {
 *   const [error, response] = await ctx.$request({
 *     body: data // ← Fully typed from OpenAPI schema!
 *   });
 *   if (error) throw new Error(error);
 *   return response?.data;
 * }
 * ```
 *
 * @example With @kosFuture (context-based tracker)
 * ```typescript
 * @kosFuture({ trackerPolicy: 'context' })
 * async loadData(filter: string, ctx: ExecutionContext): Promise<Data> {
 *   const data = await fetchData(filter);
 *   ctx.$tracker?.complete();
 *   return data;
 * }
 * ```
 */
export type ExecutionContext<
  Path extends ApiPath = ApiPath,
  Method extends HttpMethod = 'get'
> = KosExecutionContext<paths, Path, Method>;

/**
 * Typed decorator factory for @kosServiceRequest with package-specific OpenAPI types
 *
 * Provides full IntelliSense and type safety for path, query params, and body
 * based on this package's OpenAPI schema.
 *
 * @example
 * ```typescript
 * import { kosServiceRequest, PATH_EXAMPLE } from '../../utils/service';
 *
 * @kosServiceRequest({
 *   path: PATH_EXAMPLE,
 *   method: 'get',
 *   lifecycle: DependencyLifecycle.LOAD,
 *   queryParams: { ... } // ← Fully typed from OpenAPI schema!
 * })
 * private onDataLoaded(): void {
 *   const response = getServiceResponse(this, PATH_EXAMPLE, 'get');
 * }
 * ```
 */
export function kosServiceRequest<
  Path extends ApiPath,
  Method extends HttpMethod = 'get',
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

export default api;
