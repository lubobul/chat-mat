import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
    selector: 'app-chat-home',
    imports: [RouterOutlet],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent {

}
