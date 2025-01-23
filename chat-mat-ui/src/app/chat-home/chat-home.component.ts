import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {
    ClarityModule,
    ClrAlertModule,
    ClrCommonFormsModule, ClrIconModule,
    ClrInputModule,
    ClrPasswordModule,
    ClrVerticalNavModule
} from '@clr/angular';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdsIcon} from '@cds/core/icon';
import {CdsIconModule} from '@cds/angular';

@Component({
    selector: 'app-chat-home',
    imports: [
        RouterOutlet,
        ClrVerticalNavModule,
        RouterLink,
        RouterLinkActive,
        CdsIconModule,
        ClarityModule
    ],
    templateUrl: './chat-home.component.html',
    standalone: true,
    styleUrl: './chat-home.component.scss'
})
export class ChatHomeComponent {

}
