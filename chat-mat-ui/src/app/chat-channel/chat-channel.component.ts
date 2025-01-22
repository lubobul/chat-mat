import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {ChatHistoryMessage, ChatHistoryMessageType} from '../models/chat.models';


@Component({
    selector: 'char-main-page',
    imports: [RouterOutlet, FormsModule],
    templateUrl: './chat-channel.component.html',
    standalone: true,
    styleUrl: './chat-channel.component.scss',
})
export class ChatChannelComponent implements OnInit, AfterViewChecked {
    @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

    private chatHistoryId = 0;

    protected chatHistoryStack: ChatHistoryMessage[] = [];

    protected userChatInput: string = '';

    constructor() {
    }

    public ngOnInit(): void {
        this.appendMessageToHistory({
            messageType: ChatHistoryMessageType.BotMessage,
            message: "Здравейте, аз съм чат-бота на ФМИ. За списък от поддържани въпроси въведете /помощ",
            id: this.chatHistoryId++,
            isWarning: false,
        } as ChatHistoryMessage);
    }

    public onKeydown(event: KeyboardEvent) {
        if (event.key === 'Enter') {
            event.preventDefault(); // Prevents the default action of moving to the next line // Handle the "Enter" key press here console.log('Enter key was pressed.'); } }
            this.sendMessage();
        }
    }

    public sendMessage(): void {
        if (!this.userChatInput || this.userChatInput.trim().length === 0) {
            return; // Exit the method if the input is empty, undefined, or just spaces
        }

        this.appendMessageToHistory({
            messageType: ChatHistoryMessageType.UserMessage,
            message: this.userChatInput,
            id: this.chatHistoryId++,
            isWarning: false,
        } as ChatHistoryMessage);

        this.userChatInput = '';
    }

    private appendMessageToHistory(historyMessage: ChatHistoryMessage): void {
        this.chatHistoryStack.push(historyMessage);
    }

    private scrollToBottom(): void {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    }

    protected readonly ChatHistoryMessageType = ChatHistoryMessageType;

    ngAfterViewChecked(): void {
        this.scrollToBottom();
    }
}
