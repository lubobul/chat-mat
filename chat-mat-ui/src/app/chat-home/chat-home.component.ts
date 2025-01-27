import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {
    ClarityModule,
    ClrVerticalNavModule
} from '@clr/angular';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {CdsIconModule} from '@cds/angular';
import {User} from '../common/rest/types/responses/user';
import {debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs';
import {resolveErrorMessage} from '../common/utils/util-functions';
import {FriendsService} from '../services/friends.service';
import {CHAT_ROUTE_PATHS} from '../app.routes';
import {DatePipe} from '@angular/common';
import {ChatService} from '../services/chat.service';
import {CreateChatRequest} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {AuthService} from '../services/auth.service';

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
        DatePipe
    ],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent implements OnInit {
    friendSearchControl = new FormControl('');
    chatSearchControl = new FormControl('');
    channelSearchControl = new FormControl('');
    friends: User[] = [];
    directChats: ChatResponse[] = [];
    channels: ChatResponse[] = [];
    errorMessage = "";
    alertClosed = true;
    openViewUserModal = false;
    selectedFriend: User = {} as User;
    friendActionLoading = false;
    loggedInUser: User = {} as User;

    constructor(
        private friendsService: FriendsService,
        private chatService: ChatService,
        private authService: AuthService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
    ) {
    }

    ngOnInit(): void {
        this.loggedInUser = this.authService.getUserIdentity();
        this.refresh();
        this.subscribeToFriendsSearch();
        this.subscribeToDirectChatsSearch();
    }

    public subscribeToFriendsSearch(): void{
        this.friendSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => this.friendsService.getFriends({
                    page: 1,
                    pageSize: 32,
                    filter: query.trim() ? `username==${query.trim()}` : undefined
                }))
            )
            .subscribe({
                next: (response) => {
                    this.friends = response.content; // Update the filtered users
                },
                error: (error) => {
                    this.errorMessage = resolveErrorMessage(error);
                    this.alertClosed = false;
                }
            });
    }

    public subscribeToDirectChatsSearch(): void{
        this.chatSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => this.chatService.getDirectChats({
                    page: 1,
                    pageSize: 32,
                    filter: query.trim() ? `title==${query.trim()}` : undefined
                }))
            )
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

    public viewUser(user: User): void{
        this.selectedFriend = user;
        this.openViewUserModal = true;
    }

    public unfriend(): void{
        this.friendActionLoading = true;
        this.friendsService.removeFriend(this.selectedFriend).subscribe({
            next: () => {
                this.friendActionLoading = false;
                this.openViewUserModal = false;
                this.refresh();
            },
            error: (error) => {
                this.friendActionLoading = false;
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.openViewUserModal = false;
            }
        });
    }

    public messageFriend(): void{
        this.friendActionLoading = true;
        this.chatService.createChat({
            title: this.selectedFriend.username,
            isChannel: false,
            participantIds: [this.selectedFriend.id]
        } as CreateChatRequest).subscribe({
            next: (chat) => {
                this.friendActionLoading = false;
                this.openViewUserModal = false;
                this.refresh();
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

    private refresh(): void{
        this.friendsService.getFriends({
            page: 1,
            pageSize: 32,
        }).subscribe((response) => {
            this.friends = response.content;
        });

        this.chatService.getDirectChats({
            page: 1,
            pageSize: 32,
        }).subscribe((response) => {
            this.directChats = response.content;
        });

        this.chatService.getChannelChats({
            page: 1,
            pageSize: 32,
        }).subscribe((response) => {
            this.channels = response.content;
        });
    }

    protected readonly CHAT_ROUTE_PATHS = CHAT_ROUTE_PATHS;
}
