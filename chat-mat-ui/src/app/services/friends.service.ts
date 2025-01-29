import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {QueryRequest} from '../common/rest/types/requests/query-request';
import {PaginatedResponse} from '../common/rest/types/responses/paginated-response';
import {UserResponse} from '../common/rest/types/responses/user-response';
import {FriendsApiService} from '../common/rest/api-services/friends-api.service';

@Injectable({
    providedIn: 'root',
})
export class FriendsService {
    constructor(private friendsApiService: FriendsApiService) {}

    getFriends(queryRequest: QueryRequest): Observable<PaginatedResponse<UserResponse>> {
        return this.friendsApiService.getFriends(queryRequest);
    }

    addFriend(user: UserResponse): Observable<void>{
        return this.friendsApiService.addFriend(user);
    }

    removeFriend(user: UserResponse): Observable<void>{
        return this.friendsApiService.removeFriend(user);
    }
}
