import {AfterViewChecked, Component, OnInit } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, FormsModule],
    templateUrl: './app.component.html',
    standalone: true,
    styleUrl: './app.component.scss',
})
export class AppComponent {

}
