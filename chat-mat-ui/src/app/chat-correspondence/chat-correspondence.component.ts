import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {map, mergeMap} from 'rxjs';
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


@Component({
    selector: 'chat-correspondence',
    imports: [FormsModule, CdsIconModule, DatePipe],
    templateUrl: './chat-correspondence.component.html',
    standalone: true,
    styleUrl: './chat-correspondence.component.scss',
})
export class ChatCorrespondenceComponent implements OnInit, AfterViewChecked {
    @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

    loading = true;
    errorMessage = "";
    alertClosed = true;
    chat: ChatResponse;
    currentUser: UserResponse;
    loadedChatMessages: ChatMessageViewModel[];

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
        this.loadCorrespondence();
    }

    public loadCorrespondence(): void {
        this.loading = true;
        this.activatedRoute.params.pipe(
            map((routeParameters) => {
                return routeParameters[CHAT_ROUTE_PATHS.CHAT_ID];
            })
        ).pipe(mergeMap((chatId) => {
            return this.chatService.getChat(chatId);
        })).subscribe({
            next: (chat) => {
                this.chat = chat;
                this.loadedChatMessages = chat.messagesPage.content.map((message) => {
                    return {
                        ...message,
                        isMine: message.senderId === this.currentUser.id,
                    } as ChatMessageViewModel
                });
                this.loading = false;
            }, error: (error) => {

                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.loading = false;
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
                return this.chatService.getChat(this.chat.id);
            })
        ).subscribe({
            next: (chat) => {
                this.chat = chat;
                this.loadedChatMessages = chat.messagesPage.content.map((message) => {
                    return {
                        ...message,
                        isMine: message.senderId === this.currentUser.id,
                    } as ChatMessageViewModel
                });
                this.loading = false;
                this.userChatInput = "";
            }, error: (error) => {

                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
                this.loading = false;

            }
        });
    }

    private scrollToBottom(): void {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    }

    ngAfterViewChecked(): void {
        this.scrollToBottom();
    }
}
