import {QueryParams, QueryRequest} from '../rest/types/query-request';

export function resolveErrorMessage(error: any): string{
    return error?.error?.error || error?.message || error?.error?.message || error;
}

export function buildQueryParams(queryRequest: QueryRequest): QueryParams{
    const params: QueryParams = {
        page: queryRequest.page,
        size: queryRequest.pageSize,
    };

    if (queryRequest.filter) {
        params.filter = queryRequest.filter;
    }

    if (queryRequest.sort) {
        params.sort = `${queryRequest.sort.sortField},${queryRequest.sort.sortType.toLowerCase()}`;
    }

    return params;
}
