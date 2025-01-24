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
    userSearchControl = new FormControl('');
    users: User[] = [];
    errorMessage = "";
    alertClosed = true;
    openViewUserModal = false;
    selectedUser: User = {} as User;

    constructor(private usersService: UsersService) {
    }

    ngOnInit(): void {
        this.usersService.getUsers({
            page: 0,
            pageSize: 32,
        }).subscribe((response) => {
            this.users = response.content;

        });

        this.subscribeToUsersSearch();
    }

    public subscribeToUsersSearch(): void{
        this.userSearchControl.valueChanges
            .pipe(
                filter((query): query is string => query !== null),
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query: string) => this.usersService.getUsers({
                    page: 0,
                    pageSize: 32,
                    filter: query.trim() ? `username==${query.trim()}` : undefined
                }))
            )
            .subscribe({
                next: (response) => {
                    this.users = response.content; // Update the filtered users
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
}
