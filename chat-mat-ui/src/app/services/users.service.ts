import {Injectable} from '@angular/core';
import {UsersApiService} from '../common/rest/api-services/users-api.service';
import {QueryRequest} from '../common/rest/types/query-request';
import {Observable} from 'rxjs';
import {PaginatedResponse} from '../common/rest/types/paginated-response';
import {User} from '../common/rest/types/user';

@Injectable({
    providedIn: 'root',
})
export class UsersService {

    constructor(
        private usersApiService: UsersApiService,
    ) {
    }


    getUsers(queryRequest: QueryRequest): Observable<PaginatedResponse<User>> {
        return this.usersApiService.getUsers(queryRequest);
    }
}
