import { Component } from '@angular/core';
import {User} from '../common/rest/types/responses/user';
import {AuthService} from '../services/auth.service';

@Component({
    selector: 'app-chat-welcome-screen',
    imports: [],
    templateUrl: './chat-welcome-screen.component.html',
    standalone: true,
    styleUrl: './chat-welcome-screen.component.scss'
})
export class ChatWelcomeScreenComponent {

    constructor(private authService: AuthService) {
    }

    public get userIdentity(): User{
        return this.authService.getUserIdentity();
    }
}
