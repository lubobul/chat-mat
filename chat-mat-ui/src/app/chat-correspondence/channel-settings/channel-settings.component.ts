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
import {UserResponse} from '../../common/rest/types/responses/user-response';
import {QueryRequest, QueryRequestSortType} from '../../common/rest/types/requests/query-request';
import {FriendsService} from '../../services/friends.service';
import {buildRestGridFilter, resolveErrorMessage} from '../../common/utils/util-functions';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ChatService} from '../../services/chat.service';
import {CreateChatRequest} from '../../common/rest/types/requests/chat-request';
import {ChatResponse} from '../../common/rest/types/responses/chat-response';

@Component({
    selector: 'channel-settings',
    imports: [
        ClrAlertModule,
        ClrDatagridModule,
        DatePipe,
        ClrSidePanelModule,
        ClrInputModule,
        FormsModule,
        ReactiveFormsModule
    ],
    templateUrl: './channel-settings.component.html',
    standalone: true,
    styleUrl: './channel-settings.component.scss'
})
export class ChannelSettingsComponent implements OnInit{
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

    @Output()
    channelCreated = new EventEmitter<ChatResponse>();

    protected opened = false;

    channelForm: FormGroup;
    constructor(
        private friendsService: FriendsService,
        private chatService: ChatService,
        private fb: FormBuilder,
    ) {
    }

    public trackByFnc = function (item: UserResponse): number | undefined {
        return item?.id;
    };

    ngOnInit(): void {
        this.buildForm();
        this.subscribeToUsersGrid();
    }

    private buildForm(): void{
        this.channelForm = this.fb.group({
            channelName: ['', [Validators.required, Validators.minLength(6)]],
            selectedUsers: [this.selectedFriends, [Validators.required, Validators.minLength(1)]],
        });
    }

    private subscribeToUsersGrid(): void{
        this.onDataGridRefresh.pipe(
            debounceTime(500),
            mergeMap((state) => {
                this.loading = true;
                this.restQuery = {
                    pageSize: state?.page?.size || 5,
                    page: state.page?.current || 1,
                    sort: state.sort ? {
                        sortField: state.sort.by as string,
                        sortType: state.sort.reverse ? QueryRequestSortType.DESC : QueryRequestSortType.ASC
                    } : undefined,
                    filter: buildRestGridFilter(state.filters)
                }
                return this.friendsService.getFriends(this.restQuery);
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
        this.opened = true;
        this.buildForm();
        this.subscribeToUsersGrid();
    }

    public close(): void {
        this.opened = false;
        this.selectedFriends = [];
        this.channelForm.get("channelName")?.setValue("");
    }

    public setSelectedFriendsToForm(): void{
        this.channelForm.get("selectedUsers")?.setValue(this.selectedFriends);

        console.log(this.channelForm);
    }

    public createChatChannel(): void{
        this.chatService.createChat({
            title: this.channelForm.get("channelName")?.value,
            isChannel: true,
            participantIds: (this.channelForm.get("selectedUsers")?.value as UserResponse[]).map((user) => user.id),
        } as CreateChatRequest).subscribe({
            next: (chat) => {
                this.channelCreated.next(chat);
                this.close();
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        });
    }
}
