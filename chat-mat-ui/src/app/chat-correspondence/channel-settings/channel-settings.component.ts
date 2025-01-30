import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {
    ClrAlertModule,
    ClrDatagridModule,
    ClrDatagridStateInterface,
    ClrInputModule, ClrModalModule,
    ClrSidePanelModule
} from '@clr/angular';
import {DatePipe} from '@angular/common';
import {catchError, debounceTime, map, mergeMap, Observable, Subject, tap, throwError} from 'rxjs';
import {PaginatedResponse} from '../../common/rest/types/responses/paginated-response';
import {ChatUserType, UserChatRightsResponse, UserResponse} from '../../common/rest/types/responses/user-response';
import {QueryRequest, QueryRequestSortType} from '../../common/rest/types/requests/query-request';
import {FriendsService} from '../../services/friends.service';
import {buildRestGridFilter, resolveErrorMessage} from '../../common/utils/util-functions';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ChatService} from '../../services/chat.service';
import {CreateChatRequest} from '../../common/rest/types/requests/chat-request';
import {ChatResponse} from '../../common/rest/types/responses/chat-response';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {CHAT_ROUTE_PATHS} from '../../app.routes';
import {AuthService} from '../../services/auth.service';

@Component({
    selector: 'channel-settings',
    imports: [
        ClrAlertModule,
        ClrDatagridModule,
        DatePipe,
        ClrSidePanelModule,
        ClrInputModule,
        FormsModule,
        ReactiveFormsModule,
        ClrModalModule
    ],
    templateUrl: './channel-settings.component.html',
    standalone: true,
    styleUrl: './channel-settings.component.scss'
})
export class ChannelSettingsComponent implements OnInit {
    private onDataGridRefresh = new Subject<ClrDatagridStateInterface>();
    errorMessage = "";
    alertClosed = true;
    loading = true;
    channelForm: FormGroup;

    currentChat: ChatResponse;
    currentUser: UserChatRightsResponse;
    currentUserId: number;
    currentChatId: number;
    openConfirmChannelDelete = false;
    constructor(
        private friendsService: FriendsService,
        private chatService: ChatService,
        private fb: FormBuilder,
        private activatedRoute: ActivatedRoute,
        private authService: AuthService,
        private router: Router,
    ) {
        this.currentUserId = authService.getUserIdentity().id;
    }

    ngOnInit(): void {
        this.buildForm();
        this.initChatHomeComponent();
    }

    private initChatHomeComponent(): void {
        (this.activatedRoute.parent?.params as Observable<Params>).pipe(
            map((routeParameters) => {
                return routeParameters[CHAT_ROUTE_PATHS.CHAT_ID];
            })
        ).pipe(
            mergeMap((chatId) => {
                this.currentChatId = chatId;
                return this.chatService.getParticipantRights(chatId, this.currentUserId);
            }),
            mergeMap((currentUserWithRights: UserChatRightsResponse) => {

                this.currentUser = currentUserWithRights;
                return this.loadChat(this.currentChatId);
            })).subscribe({
            next: () => {
            }
        });
    }

    private loadChat(chatId: number): Observable<ChatResponse> {
        return this.chatService.getChat(chatId).pipe(
            tap((chat: ChatResponse) => {
                this.currentChat = chat;
                this.channelForm.get("channelName")?.setValue(chat.title);
            }),
            catchError((error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                return throwError(error);
            }),
        )
    }

    private buildForm(): void {
        this.channelForm = this.fb.group({
            channelName: [this.currentChat?.title, [Validators.required, Validators.minLength(6)]],
        });
    }


    public updateChannel(): void {
        this.chatService.updateChat(this.currentChat.id, {
            chatTitle: this.channelForm.get("channelName")?.value,
        }).subscribe({
            next: () => {
                this.router.routeReuseStrategy.shouldReuseRoute = () => false;
                this.router.navigate([`/${CHAT_ROUTE_PATHS.HOME}/${CHAT_ROUTE_PATHS.CHAT}/${this.currentChat.id}`], {
                    replaceUrl: true,
                    preserveFragment: false
                });
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        });
    }

    public deleteChannel(): void {
        this.chatService.deleteChat(this.currentChat.id).subscribe({
            next: (chat) => {
                this.router.navigate([CHAT_ROUTE_PATHS.HOME]); // Navigate to the home page after login
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        });
    }

    protected readonly ChatUserType = ChatUserType;
}
