import {Component, OnInit} from '@angular/core';
import {ClrAlertModule, ClrCommonFormsModule, ClrInputModule, ClrModalModule, ClrPasswordModule} from '@clr/angular';
import {
    AbstractControlOptions,
    FormBuilder,
    FormGroup,
    FormsModule,
    ReactiveFormsModule,
    Validators
} from '@angular/forms';
import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';
import {FormValidators} from '../common/utils/form-validators';
import {UsersApiService} from '../common/rest/api-services/users-api.service';
import {resolveErrorMessage} from '../common/utils/util-functions';
import {UserResponse} from '../common/rest/types/responses/userResponse';
import {UpdateProfileRequest} from '../common/rest/types/auth-types';
import {CHAT_ROUTE_PATHS} from '../app.routes';

@Component({
    selector: 'app-profile-settings',
    imports: [
        ClrAlertModule,
        ClrCommonFormsModule,
        ClrInputModule,
        ClrPasswordModule,
        FormsModule,
        ReactiveFormsModule,
        ClrModalModule
    ],
    templateUrl: './profile-settings.component.html',
    standalone: true,
    styleUrl: './profile-settings.component.scss'
})
export class ProfileSettingsComponent implements OnInit{
    updateProfileForm: FormGroup = {} as any;
    errorMessage: string | null = null;
    alertClosed = true;
    user: UserResponse = {} as any;
    showConfirm = false;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {

    }

    ngOnInit(): void {
        this.buildForm(this.authService.getUserIdentity());

    }

    //TODO Could potentially be checking if user exists while typing username
    private buildForm(user: UserResponse): void{
        this.updateProfileForm = this.fb.group({
            username: [user.username, [Validators.required, Validators.minLength(6)]],
            email: [user.email, [Validators.required, Validators.email]],
        } as AbstractControlOptions);

        this.updateProfileForm.get("email")?.disable();
    }

    updateProfile(): void{
        this.authService.updateProfile(
            {
                username: this.updateProfileForm.get("username")?.value,
                avatar: "",
            } as UpdateProfileRequest
        ).subscribe({
            next: (user: UserResponse) => {
                this.router.navigate([CHAT_ROUTE_PATHS.HOME]);
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        })
    }

    logout(): void{
        this.authService.logout().subscribe({
            next: () => {
                this.router.navigate([CHAT_ROUTE_PATHS.LOGIN]);
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        });
    }


    openDeleteProfileModal(): void{
        this.showConfirm = true;
    }

    deleteProfile(): void{
        this.authService.deleteProfile().subscribe({
            next: () => {
                this.router.navigate([CHAT_ROUTE_PATHS.LOGIN]);
            },
            error: (error) => {
                this.errorMessage = resolveErrorMessage(error);
                this.alertClosed = false;
            },
        })
    }
}
