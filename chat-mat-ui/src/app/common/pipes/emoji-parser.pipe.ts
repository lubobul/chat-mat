import {Pipe, PipeTransform} from '@angular/core';
import {EmojiService} from '../services/emoji.service';

@Pipe({standalone: true, name: "emojiParse"})
export class EmojiParserPipe implements PipeTransform {

    constructor(private emojiService: EmojiService) {
    }
    public transform(chatMessage: string): string {

        return this.emojiService.parseMessage(chatMessage);
    }
}
