import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {
    ClrAlertModule,
    ClrDatagridModule,
    ClrDatagridStateInterface,
    ClrInputModule,
    ClrSidePanelModule
} from '@clr/angular';
import {DatePipe} from '@angular/common';
import {catchError, debounceTime, map, mergeMap, Observable, Subject, tap, throwError} from 'rxjs';
import {PaginatedResponse} from '../../common/rest/types/responses/paginated-response';
import {UserResponse} from '../../common/rest/types/responses/userResponse';
import {QueryRequest, QueryRequestSortType} from '../../common/rest/types/requests/query-request';
import {FriendsService} from '../../services/friends.service';
import {buildRestGridFilter, resolveErrorMessage} from '../../common/utils/util-functions';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ChatService} from '../../services/chat.service';
import {CreateChatRequest} from '../../common/rest/types/requests/chat-request';
import {ChatResponse} from '../../common/rest/types/responses/chat-response';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {CHAT_ROUTE_PATHS} from '../../app.routes';

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
    private onParticipantsGridRefresh = new Subject<ClrDatagridStateInterface>();
    private onFriendsGridRefresh = new Subject<ClrDatagridStateInterface>();

    errorMessage = "";
    alertClosed = true;
    participantsLoading = true;
    friendsLoading = true;

    participantsPage: PaginatedResponse<UserResponse> = {
        pageSize: 0,
        content: [],
        totalPages: 0,
    } as unknown as PaginatedResponse<UserResponse>;

    friendsPage: PaginatedResponse<UserResponse> = {
        pageSize: 0,
        content: [],
        totalPages: 0,
    } as unknown as PaginatedResponse<UserResponse>;

    selectedFriends: UserResponse[] = [];

    private participantsRestQuery: QueryRequest = {
        page: 1,
        pageSize: 5,
    };

    private friendsRestQuery: QueryRequest = {
        page: 1,
        pageSize: 5,
    };

    currentChat: ChatResponse;

    currentUser: UserResponse;

    constructor(
        private friendsService: FriendsService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private chatService: ChatService,
        private authService: AuthService,
    ) {
        this.currentUser = authService.getUserIdentity();
    }

    public trackByFnc = function (item: UserResponse): number | undefined {
        return item?.id;
    };

    ngOnInit(): void {
        this.initChatHomeComponent();

    }

    public initChatHomeComponent(): void {
        (this.activatedRoute.parent?.params as Observable<Params>).pipe(
            map((routeParameters) => {
                return routeParameters[CHAT_ROUTE_PATHS.CHAT_ID];
            })
        ).pipe(mergeMap((chatId) => {
            return this.loadCorrespondence(chatId, false);
        })).subscribe({
            next: () => {
            }
        });
    }

    private loadCorrespondence(chatId: number, fromPolling: boolean): Observable<ChatResponse> {
        return this.chatService.getChat(chatId).pipe(
            tap((chat: ChatResponse) => {
                this.currentChat = chat;
                this.initGrids();
            }),
            catchError((error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                return throwError(error);
            }),
        )
    }

    private initGrids(): void{
        this.subscribeToParticipantsGrid();
        this.refreshByParticipantsGrid({
            page: {
                size: 5,
                current: 1,
            }
        });

        this.subscribeToFriendsGrid();
        this.refreshByFriendsGrid({
            page: {
                size: 5,
                current: 1,
            }
        });
    }

    public subscribeToParticipantsGrid(): void{
        this.onParticipantsGridRefresh.pipe(
            debounceTime(500),
            mergeMap((state) => {
                this.participantsLoading = true;
                this.participantsRestQuery = {
                    pageSize: state?.page?.size || 5,
                    page: state.page?.current || 1,
                    sort: state.sort ? {
                        sortField: `user.${state.sort.by as string}`,
                        sortType: state.sort.reverse ? QueryRequestSortType.DESC : QueryRequestSortType.ASC
                    } : undefined,
                    filter: buildRestGridFilter(state.filters)
                }
                return this.chatService.getParticipants(this.currentChat.id, this.participantsRestQuery);
            })).subscribe( {
            next: (response) => {
                this.participantsPage = response;
                this.participantsLoading = false;

            }, error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    public subscribeToFriendsGrid(): void{
        this.onFriendsGridRefresh.pipe(
            debounceTime(500),
            mergeMap((state) => {
                this.friendsLoading = true;
                this.friendsRestQuery = {
                    pageSize: state?.page?.size || 5,
                    page: state.page?.current || 1,
                    sort: state.sort ? {
                        sortField: `friend.${state.sort.by as string}`,
                        sortType: state.sort.reverse ? QueryRequestSortType.DESC : QueryRequestSortType.ASC
                    } : undefined,
                    filter: buildRestGridFilter(state.filters)
                }
                return this.chatService.getFriendsNotPartOfChat(this.currentChat.id, this.friendsRestQuery);
            })).subscribe( {
            next: (response) => {
                this.friendsPage = response;
                this.friendsLoading = false;

            }, error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    public refreshByParticipantsGrid(state: ClrDatagridStateInterface): void {
        this.onParticipantsGridRefresh.next(state);
    }

    public refreshByFriendsGrid(state: ClrDatagridStateInterface): void {
        this.onFriendsGridRefresh.next(state);
    }

    public addFriend(user: UserResponse): void{
        this.participantsLoading = true;
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
        this.participantsLoading = true;
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
        this.chatService.getParticipants(this.currentChat.id, this.participantsRestQuery).subscribe(
            {
                next: (response) => {
                    this.participantsPage = response;
                    this.participantsLoading = false;
                },
                error: (error) => {
                    this.errorMessage = resolveErrorMessage(error);
                    this.alertClosed = false;
                }
            }
        )
    }
}
