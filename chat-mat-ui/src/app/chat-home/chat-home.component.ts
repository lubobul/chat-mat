import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {
    ClarityModule,
    ClrVerticalNavModule
} from '@clr/angular';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {CdsIconModule} from '@cds/angular';
import {UserResponse} from '../common/rest/types/responses/userResponse';
import {
    BehaviorSubject,
    debounceTime, delayWhen,
    distinctUntilChanged,
    filter,
    forkJoin,
    map, mergeMap,
    Observable, retry, Subscription,
    switchMap,
    tap, timer
} from 'rxjs';
import {resolveErrorMessage} from '../common/utils/util-functions';
import {FriendsService} from '../services/friends.service';
import {CHAT_ROUTE_PATHS} from '../app.routes';
import {DatePipe} from '@angular/common';
import {ChatService} from '../services/chat.service';
import {CreateChatRequest} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {AuthService} from '../services/auth.service';
import {ChatTitlePipe} from './chat-title.pipe';
import {NewChatComponent} from '../new-chat/new-chat.component';

@Component({
    selector: 'app-chat-home',
    imports: [
        RouterOutlet,
        ClrVerticalNavModule,
        RouterLink,
        RouterLinkActive,
        CdsIconModule,
        ClarityModule,
        ReactiveFormsModule,
        DatePipe,
        ChatTitlePipe,
        NewChatComponent,
    ],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent implements OnInit, OnDestroy {
    friendSearchControl = new FormControl('');
    chatSearchControl = new FormControl('');
    channelSearchControl = new FormControl('');
    protected readonly CHAT_ROUTE_PATHS = CHAT_ROUTE_PATHS;
    @ViewChild("newChat") private newChatComponent: NewChatComponent;
    friends: UserResponse[] = [];
    directChats: ChatResponse[] = [];
    channels: ChatResponse[] = [];
    errorMessage = "";
    alertClosed = true;
    openViewUserModal = false;
    selectedFriend: UserResponse = {} as UserResponse;
    friendActionLoading = false;
    currentUser: UserResponse = {} as UserResponse;

    chatSearch = "";
    channelSearch = "";
    friendsSearch = "";

    constructor(
        private friendsService: FriendsService,
        private chatService: ChatService,
        private authService: AuthService,
        private router: Router,
    ) {
    }

    ngOnInit(): void {
        this.currentUser = this.authService.getUserIdentity();
        this.refresh().subscribe(() => {
            this.startDetailsPolling(5000);
        });

        this.subscribeToFriendsSearch();
        this.subscribeToDirectChatsSearch();
        this.subscribeToChannelsSearch();
    }

    ngOnDestroy(): void {
        this.stopDetailsPolling();
    }

    public subscribeToFriendsSearch(): void {
        this.friendSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => {
                    this.friendsSearch = query;
                    return this.friendsService.getFriends({
                        page: 1,
                        pageSize: 32,
                        filter: this.friendsSearch .trim() ? `username==${this.friendsSearch .trim()}` : undefined
                    })
                })
            ).subscribe({
            next: (response) => {
                this.friends = response.content; // Update the filtered users
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            }
        });
    }

    public subscribeToDirectChatsSearch(): void {
        this.chatSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => {
                    this.chatSearch = query;
                    return this.chatService.getDirectChats({
                        page: 1,
                        pageSize: 32,
                        filter: this.chatSearch .trim() ? `title==${this.chatSearch .trim()}` : undefined
                    })
                }))
            .subscribe({
                next: (response) => {
                    this.directChats = response.content; // Update the filtered users
                },
                error: (error) => {
                    this.errorMessage = resolveErrorMessage(error);
                    this.alertClosed = false;
                }
            });
    }

    public subscribeToChannelsSearch(): void {
        this.channelSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => {
                    this.channelSearch = query;
                    return this.chatService.getChannelChats({
                        page: 1,
                        pageSize: 32,
                        filter: this.channelSearch.trim() ? `title==${this.channelSearch.trim()}` : undefined
                    })
                }))
            .subscribe({
                next: (response) => {
                    this.channels = response.content; // Update the filtered users
                },
                error: (error) => {
                    this.errorMessage = resolveErrorMessage(error);
                    this.alertClosed = false;
                }
            });
    }

    public viewUser(user: UserResponse): void {
        this.selectedFriend = user;
        this.openViewUserModal = true;
    }

    public unfriend(): void {
        this.friendActionLoading = true;
        this.friendsService.removeFriend(this.selectedFriend).subscribe({
            next: () => {
                this.friendActionLoading = false;
                this.openViewUserModal = false;
                this.router.navigate([`${CHAT_ROUTE_PATHS.HOME}`]);
                ;
            },
            error: (error) => {
                this.friendActionLoading = false;
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.openViewUserModal = false;
            }
        });
    }

    public createOrOpenDirectChatWithFriend(): void {
        this.friendActionLoading = true;
        this.chatService.createChat({
            title: `${this.selectedFriend.username}-${this.currentUser.username}`,
            isChannel: false,
            participantIds: [this.selectedFriend.id]
        } as CreateChatRequest).subscribe({
            next: (chat) => {
                this.friendActionLoading = false;
                this.openViewUserModal = false;
                this.refresh().subscribe();
                this.router.navigate([`${CHAT_ROUTE_PATHS.HOME}/${CHAT_ROUTE_PATHS.CHAT}/${chat.id}`]);
            },
            error: (error) => {
                this.friendActionLoading = false;
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.openViewUserModal = false;
            }
        });
    }

    public refresh(): Observable<void> {

        return forkJoin([
            this.friendsService.getFriends({
                page: 1,
                pageSize: 32,
                filter: this.friendsSearch .trim() ? `username==${this.friendsSearch .trim()}` : undefined
            }),
            this.chatService.getDirectChats({
                page: 1,
                pageSize: 32,
                filter: this.chatSearch.trim() ? `title==${this.chatSearch.trim()}` : undefined
            }),
            this.chatService.getChannelChats({
                page: 1,
                pageSize: 32,
                filter: this.channelSearch.trim() ? `title==${this.channelSearch.trim()}` : undefined
            })
        ]).pipe(
            map((data) => {
                const [friends, directChats, channels] = data;
                this.friends = friends.content;
                this.directChats = directChats.content;
                this.channels = channels.content;

                return;
            })
        );
    }

    private pollingSbj: BehaviorSubject<number>;
    private pollingSubscription: Subscription;

    private startDetailsPolling(interval: number): void {
        this.stopDetailsPolling();
        this.pollingSbj = new BehaviorSubject<number>(interval);

        this.pollingSubscription = this.pollingSbj.pipe(
            delayWhen((interval) => timer(interval)),
            mergeMap(() => {
                return this.refresh();
            }),
            tap(() => {
                this.pollingSbj.next(interval);
            }),
            retry(),
        ).subscribe();
    }

    private stopDetailsPolling(): void {
        if (this.pollingSbj) {
            this.pollingSbj.complete();
        }

        if (this.pollingSubscription) {
            this.pollingSubscription.unsubscribe();
        }
    }

    public openNewChatSideBar(): void {
        this.newChatComponent.open();
    }

    channelCreated(channel: ChatResponse): void {
        this.refresh().subscribe();
        this.router.navigate([`${CHAT_ROUTE_PATHS.HOME}/${CHAT_ROUTE_PATHS.CHAT}/${channel.id}`]);
    }
}
