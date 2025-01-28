import {Pipe, PipeTransform} from '@angular/core';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {UserResponse} from '../common/rest/types/responses/userResponse';

@Pipe({standalone: true, name: "chatTitle"})
export class ChatTitlePipe implements PipeTransform {
    public transform(chat: ChatResponse, currentUser: UserResponse): string {

        if (chat?.owner?.id == currentUser.id){
            return chat.participantsPage.content[0].username;
        }else{
            return chat.owner.username;
        }
    }
}
