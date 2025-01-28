import {AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {
    BehaviorSubject,
    catchError,
    delayWhen,
    interval,
    map,
    mergeMap, Observable,
    retry,
    Subscription,
    tap,
    throwError,
    timer
} from 'rxjs';
import {CHAT_ROUTE_PATHS} from '../app.routes';
import {resolveErrorMessage} from '../common/utils/util-functions';
import {ChatService} from '../services/chat.service';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {UserResponse} from '../common/rest/types/responses/userResponse';
import {AuthService} from '../services/auth.service';
import {ChatMessageViewModel} from '../common/view-models/chat-message-view-models';
import {ChatMessageRequest} from '../common/rest/types/requests/chat-request';
import {CdsIconModule} from '@cds/angular';
import {DatePipe} from '@angular/common';
import {PaginatedResponse} from '../common/rest/types/responses/paginated-response';
import {EmojiParserPipe} from '../common/pipes/emoji-parser.pipe';
import {ClrAlertModule} from '@clr/angular';


@Component({
    selector: 'chat-correspondence',
    imports: [FormsModule, CdsIconModule, DatePipe, EmojiParserPipe, ClrAlertModule],
    templateUrl: './chat-correspondence.component.html',
    standalone: true,
    styleUrl: './chat-correspondence.component.scss',
})
export class ChatCorrespondenceComponent implements OnInit, AfterViewChecked, OnDestroy {
    @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

    loading = true;
    errorMessage = "";
    alertClosed = true;
    chat: ChatResponse;
    currentUser: UserResponse;
    loadedChatMessages: ChatMessageViewModel[];
    initialScrollToBottom = false;

    protected userChatInput: string = '';

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private chatService: ChatService,
        private authService: AuthService,
    ) {
    }

    public ngOnInit(): void {
        this.currentUser = this.authService.getUserIdentity();
        this.initChatComponent();
    }

    ngAfterViewChecked(): void {
        if(!this.initialScrollToBottom && !!this.loadedChatMessages?.length){
            this.scrollToBottom();
        }
    }

    ngOnDestroy(): void {
        this.stopChatPolling();
    }

    public initChatComponent(): void {
        this.loading = true;
        this.activatedRoute.params.pipe(
            map((routeParameters) => {
                return routeParameters[CHAT_ROUTE_PATHS.CHAT_ID];
            })
        ).pipe(mergeMap((chatId) => {
            return this.loadCorrespondence(chatId);
        })).subscribe({
            next: () => {
                this.startChatPolling(5000);
            }
        });
    }

    public onKeydown(event: KeyboardEvent) {
        if (event.key === 'Enter') {
            event.preventDefault();
            this.sendMessage();
        }
    }

    public sendMessage(): void {
        if (!this.userChatInput || this.userChatInput.trim().length === 0) {
            return;
        }

        this.chatService.sendMessage({
                messageContent: this.userChatInput,
            } as ChatMessageRequest, this.chat.id
        ).pipe(
            mergeMap(() => {
                return this.loadCorrespondence(this.chat.id);
            })
        ).subscribe({
            next: (chat) => {
                this.userChatInput = "";
                this.scrollToBottom();
            }, error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.loading = false;

            }
        });
    }

    private loadCorrespondence(chatId: number): Observable<ChatResponse> {
        return this.chatService.getChat(chatId).pipe(
            tap((chat: ChatResponse) => {
                this.chat = chat;
                this.loadedChatMessages = chat.messagesPage.content.map((message) => {
                    return {
                        ...message,
                        isMine: message.senderId === this.currentUser.id,
                    } as ChatMessageViewModel
                });
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

    private pollingSbj: BehaviorSubject<number>;
    private pollingSubscription: Subscription;

    private startChatPolling(interval: number): void {
        this.stopChatPolling();
        this.pollingSbj = new BehaviorSubject<number>(interval);

        this.pollingSubscription = this.pollingSbj.pipe(
            delayWhen((interval) => timer(interval)),
            mergeMap(() => {
                return this.loadCorrespondence(this.chat.id);
            }),
            tap(() => {
                this.pollingSbj.next(interval);
            }),
            retry(),
        ).subscribe();
    }

    private stopChatPolling(): void {
        if (this.pollingSbj) {
            this.pollingSbj.complete();
        }

        if (this.pollingSubscription) {
            this.pollingSubscription.unsubscribe();
        }
    }

    private scrollToBottom(): void {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
        this.initialScrollToBottom = true;
    }
}
