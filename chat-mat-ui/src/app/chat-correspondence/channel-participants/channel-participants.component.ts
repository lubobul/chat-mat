import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {
    ClrAlertModule,
    ClrDatagridModule,
    ClrDatagridStateInterface,
    ClrInputModule,
    ClrSidePanelModule
} from '@clr/angular';
import {DatePipe} from '@angular/common';
import {debounceTime, mergeMap, Subject} from 'rxjs';
import {PaginatedResponse} from '../../common/rest/types/responses/paginated-response';
import {UserResponse} from '../../common/rest/types/responses/userResponse';
import {QueryRequest, QueryRequestSortType} from '../../common/rest/types/requests/query-request';
import {FriendsService} from '../../services/friends.service';
import {buildRestGridFilter, resolveErrorMessage} from '../../common/utils/util-functions';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ChatService} from '../../services/chat.service';
import {CreateChatRequest} from '../../common/rest/types/requests/chat-request';
import {ChatResponse} from '../../common/rest/types/responses/chat-response';

@Component({
    selector: 'channel-participants',
    imports: [
        ClrAlertModule,
        ClrDatagridModule,
        ClrSidePanelModule,
        ClrInputModule,
        FormsModule,
        ReactiveFormsModule,
        DatePipe
    ],
    templateUrl: './channel-participants.component.html',
    standalone: true,
    styleUrl: './channel-participants.component.scss'
})
export class ChannelParticipantsComponent implements OnInit{
    private onDataGridRefresh = new Subject<ClrDatagridStateInterface>();
    errorMessage = "";
    alertClosed = true;
    loading = true;
    usersPage: PaginatedResponse<UserResponse> = {
        pageSize: 0,
        content: [],
        totalPages: 0,
    } as unknown as PaginatedResponse<UserResponse>;
    selectedFriends: UserResponse[] = [];
    private restQuery: QueryRequest = {
        page: 1,
        pageSize: 5,
    };
    currentChat: ChatResponse;

    protected opened = false;

    constructor(
        private chatService: ChatService,
        private friendsService: FriendsService,
    ) {
    }
    public trackByFnc = function (item: UserResponse): number | undefined {
        return item?.id;
    };

    ngOnInit(): void {
    }
    public subscribeToUsersGrid(): void{
        this.onDataGridRefresh.pipe(
            debounceTime(500),
            mergeMap((state) => {
                this.loading = true;
                this.restQuery = {
                    pageSize: state?.page?.size || 5,
                    page: state.page?.current || 1,
                    sort: state.sort ? {
                        sortField: `user.${state.sort.by as string}`,
                        sortType: state.sort.reverse ? QueryRequestSortType.DESC : QueryRequestSortType.ASC
                    } : undefined,
                    filter: buildRestGridFilter(state.filters)
                }
                return this.chatService.getParticipants(this.currentChat.id, this.restQuery);
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

    public open(chat: ChatResponse): void {
        this.currentChat = chat;
        this.opened = true;
        this.subscribeToUsersGrid();
        this.refreshByGrid({
            page: {
                size: 5,
                current: 1,
            }
        });
    }

    public close(): void {
        this.opened = false;
        this.selectedFriends = [];
    }

    public addFriend(user: UserResponse): void{
        this.loading = true;
        this.friendsService.addFriend(user).subscribe({
            next: () => {
                this.refresh();
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    public unfriend(user: UserResponse): void{
        this.loading = true;
        this.friendsService.removeFriend(user).subscribe({
            next: () => {
                this.refresh();
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    private refresh(): void{
        this.chatService.getParticipants(this.currentChat.id, this.restQuery).subscribe(
            {
                next: (response) => {
                    this.usersPage = response;
                    this.loading = false;
                },
                error: (error) => {
                    this.errorMessage = resolveErrorMessage(error);
                    this.alertClosed = false;
                }
            }
        )
    }
}
