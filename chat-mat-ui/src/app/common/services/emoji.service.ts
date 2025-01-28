import { Injectable } from '@angular/core';
import EmojiConvertor from 'emoji-js';

@Injectable({
  providedIn: 'root'
})
export class EmojiService {
  private emoji: EmojiConvertor;

  constructor() {
    this.emoji = new EmojiConvertor();
    this.emoji.replace_mode = 'unified'; // Use Unicode emoji
  }

  parseMessage(message: string): string {
    return this.emoji.replace_emoticons(
      this.emoji.replace_colons(message)
    );
  }
}
