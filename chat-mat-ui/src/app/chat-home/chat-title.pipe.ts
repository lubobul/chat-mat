import {Pipe, PipeTransform} from '@angular/core';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {UserResponse} from '../common/rest/types/responses/userResponse';

@Pipe({standalone: true, name: "chatTitle"})
export class ChatTitlePipe implements PipeTransform {
    public transform(chat: ChatResponse, currentUser: UserResponse): string {

        return <string>chat.participantsPage.content.find((user) => user.id != currentUser.id)?.username;
    }
}
