import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {
    ClarityModule,
    ClrAlertModule,
    ClrCommonFormsModule, ClrIconModule,
    ClrInputModule,
    ClrPasswordModule,
    ClrVerticalNavModule
} from '@clr/angular';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdsIcon} from '@cds/core/icon';
import {CdsIconModule} from '@cds/angular';
import {UsersService} from '../services/users.service';
import {QueryRequest, QueryRequestSortType} from '../common/rest/types/query-request';
import {User} from '../common/rest/types/user';
import {debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs';
import {resolveErrorMessage} from '../common/utils/util-functions';
import {FriendsService} from '../services/friends.service';
import {CHAT_ROUTE_PATHS} from '../app.routes';

@Component({
    selector: 'app-chat-home',
    imports: [
        RouterOutlet,
        ClrVerticalNavModule,
        RouterLink,
        RouterLinkActive,
        CdsIconModule,
        ClarityModule,
        ReactiveFormsModule
    ],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent implements OnInit {
    friendSearchControl = new FormControl('');
    friends: User[] = [];
    errorMessage = "";
    alertClosed = true;
    openViewUserModal = false;
    selectedUser: User = {} as User;

    constructor(
        private friendsService: FriendsService,
    ) {
    }

    ngOnInit(): void {


        this.friendsService.getFriends({
            page: 0,
            pageSize: 32,
        }).subscribe((response) => {
            this.friends = response.content;

        });
        this.subscribeToFriendsSearch();
    }

    public subscribeToFriendsSearch(): void{
        this.friendSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => this.friendsService.getFriends({
                    page: 0,
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

    public viewUser(user: User): void{
        this.selectedUser = user;
        this.openViewUserModal = true;
    }

    public viewFriend(user: User): void{
        this.selectedUser = user;
        this.openViewUserModal = true;
    }

    protected readonly CHAT_ROUTE_PATHS = CHAT_ROUTE_PATHS;
}
