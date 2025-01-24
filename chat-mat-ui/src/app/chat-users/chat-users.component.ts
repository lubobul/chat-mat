import {Component, OnInit} from '@angular/core';
import {UsersService} from '../services/users.service';
import {PaginatedResponse} from '../common/rest/types/paginated-response';
import {User} from '../common/rest/types/user';
import {ClarityModule, ClrDatagridStateInterface} from '@clr/angular';
import {DatePipe} from '@angular/common';
import {debounceTime, mergeMap, Subject} from 'rxjs';
import {QueryRequest, QueryRequestSortType} from '../common/rest/types/query-request';
import {buildRestGridFilter, resolveErrorMessage} from '../common/utils/util-functions';

@Component({
    selector: 'app-chat-users',
    imports: [ClarityModule, DatePipe,],
    templateUrl: './chat-users.component.html',
    standalone: true,
    styleUrl: './chat-users.component.scss'
})
export class ChatUsersComponent implements OnInit{
    private onDataGridRefresh = new Subject<ClrDatagridStateInterface>();
    errorMessage = "";
    alertClosed = true;
    loading = true;
    usersPage: PaginatedResponse<User> = {
        pageSize: 0,
        content: [],
        totalPages: 0,
    } as unknown as PaginatedResponse<User>;

    private restQuery: QueryRequest = {
        page: 0,
        pageSize: 32,
    };
    constructor(private usersService: UsersService,) {
    }

    ngOnInit(): void {
        this.subscribeToUsersGrid();
    }

    public subscribeToUsersGrid(): void{
        this.onDataGridRefresh.pipe(
            debounceTime(500),
            mergeMap((state) => {
                this.loading = true;
                this.restQuery = {
                    page: 0,
                    pageSize: 32,
                    sort: state.sort ? {
                        sortField: state.sort.by as string,
                        sortType: state.sort.reverse ? QueryRequestSortType.DESC : QueryRequestSortType.DESC
                    } : undefined,
                    filter: buildRestGridFilter(state.filters)
                }
                return this.usersService.getUsers(this.restQuery);
            })).subscribe( {
            next: (response) => {
                this.usersPage = response;
                this.loading = false;

            }, error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    public refreshByGrid(state: ClrDatagridStateInterface): void {
        this.onDataGridRefresh.next(state);
    }

    public refresh(): void{
        this.usersService.getUsers(this.restQuery).subscribe((response) => {
            this.usersPage = response;
        });
    }

    public addFriend(user: User): void{

    }
}
