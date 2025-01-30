import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {catchError, map, mergeMap, Observable, tap, throwError} from 'rxjs';
import {ChatResponse} from '../../common/rest/types/responses/chat-response';
import {resolveErrorMessage} from '../../common/utils/util-functions';
import {ChatService} from '../../services/chat.service';
import {AuthService} from '../../services/auth.service';
import {ChatUserType, UserChatRightsResponse, UserResponse} from '../../common/rest/types/responses/user-response';
import {ChatTitlePipe} from '../chat-title.pipe';
import {CHAT_ROUTE_PATHS} from '../../app.routes';
import {ClrAlertModule, ClrSpinnerModule} from '@clr/angular';

@Component({
    selector: 'app-chat-home',
    imports: [
        RouterOutlet,
        ChatTitlePipe,
        ClrAlertModule,
        RouterLinkActive,
        RouterLink,
        ClrSpinnerModule
    ],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent implements OnInit {

    loading = false;
    errorMessage = "";
    alertClosed = true;
    chat: ChatResponse;
    currentUser: UserChatRightsResponse;
    currentUserId: number;
    currentChatId: number;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private chatService: ChatService,
        private authService: AuthService,
    ) {
        this.currentUserId = authService.getUserIdentity().id;
    }

    ngOnInit(): void {
        this.initChatHomeComponent();
    }

    public initChatHomeComponent(): void {
        this.loading = true;
        (this.activatedRoute.params as Observable<Params>).pipe(
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
                return this.loadCorrespondence(this.currentChatId);
            })).subscribe({
            next: () => {
            }
        });
    }

    private loadCorrespondence(chatId: number): Observable<ChatResponse> {
        return this.chatService.getChat(chatId).pipe(
            tap((chat: ChatResponse) => {
                this.chat = chat;
                this.loading = false;
            }),
            catchError((error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.loading = false;
                return throwError(error);
            }),
        )
    }

    protected readonly CHAT_ROUTE_PATHS = CHAT_ROUTE_PATHS;
    protected readonly ChatUserType = ChatUserType;
}
