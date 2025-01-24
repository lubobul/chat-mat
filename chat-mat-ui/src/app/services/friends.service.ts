import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {QueryRequest} from '../common/rest/types/query-request';
import {PaginatedResponse} from '../common/rest/types/paginated-response';
import {User} from '../common/rest/types/user';
import {buildQueryParams} from '../common/utils/util-functions';
import {FriendsApiService} from '../common/rest/api-services/friends-api.service';

@Injectable({
    providedIn: 'root',
})
export class FriendsService {
    constructor(private friendsApiService: FriendsApiService) {}

    getFriends(queryRequest: QueryRequest): Observable<PaginatedResponse<User>> {
        return this.friendsApiService.getFriends(queryRequest);
    }
}
