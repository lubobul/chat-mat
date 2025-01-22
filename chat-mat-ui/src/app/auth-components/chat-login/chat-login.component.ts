import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {ClarityModule, ClrInputModule, ClrPasswordModule} from '@clr/angular';
import {resolveErrorMessage} from '../../common/utils/util-functions';

@Component({
    selector: 'app-chat-login',
    imports: [
        ClarityModule,
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './chat-login.component.html',
    standalone: true,
    styleUrl: './chat-login.component.scss'
})
export class ChatLoginComponent {
    loginForm: FormGroup;
    errorMessage: string | null = null;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
        });
    }

    onSubmit(): void {
        if (this.loginForm.invalid) {
            return;
        }

        const { email, password } = this.loginForm.value;

        this.authService.login({ email, password }).subscribe({
            next: () => {
                this.router.navigate(['/home']); // Navigate to the home page after login
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                console.error(error);
            },
        });
    }
}
