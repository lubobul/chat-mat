import { Routes } from '@angular/router';
import {ApplicationHomeComponent} from './application-home/application-home.component';
import {ChatLoginComponent} from './auth-components/chat-login/chat-login.component';
import {ChatRegisterUserComponent} from './auth-components/chat-register-user/chat-register-user.component';
import {ChatUsersComponent} from './chat-users/chat-users.component';
import {ProfileSettingsComponent} from './profile-settings/profile-settings.component';
import {ChatWelcomeScreenComponent} from './chat-welcome-screen/chat-welcome-screen.component';
import {ChatCorrespondenceComponent} from './chat-correspondence/chat-correspondence.component';
import {ChatHomeComponent} from './application-home/chat-home/chat-home.component';

export const CHAT_ROUTE_PATHS = {
    HOME: "home",
    CHAT: "chat",
    CHAT_ID: "chat_id",
    CHANNEL: "channel",
    CHAT_CORRESPONDENCE: "correspondence",
    CHAT_USERS: "users",
    LOGIN: "login",
    REGISTER: "register",
    PROFILE_SETTINGS: "profile",
}

export const routes: Routes = [
    {
        path: "",
        children: [
            {
                path: "",
                redirectTo: CHAT_ROUTE_PATHS.HOME,
                pathMatch: "full",
            },
            {
                path: CHAT_ROUTE_PATHS.HOME,
                component: ApplicationHomeComponent,
                children: [
                    {
                        path: "",
                        redirectTo: CHAT_ROUTE_PATHS.CHAT,
                        pathMatch: "full",
                    },
                    {
                        component: ChatWelcomeScreenComponent,
                        path: CHAT_ROUTE_PATHS.CHAT
                    },
                    {
                        path: `${CHAT_ROUTE_PATHS.CHAT}/:${CHAT_ROUTE_PATHS.CHAT_ID}`,
                        children: [
                            {
                                path: "",
                                redirectTo: CHAT_ROUTE_PATHS.CHANNEL,
                                pathMatch: "full",
                            },
                            {
                                path: CHAT_ROUTE_PATHS.CHANNEL,
                                component: ChatHomeComponent,
                                children: [
                                    {
                                        path: "",
                                        redirectTo: CHAT_ROUTE_PATHS.CHAT_CORRESPONDENCE,
                                        pathMatch: "full",
                                    },
                                    {
                                        path: CHAT_ROUTE_PATHS.CHAT_CORRESPONDENCE,
                                        component: ChatCorrespondenceComponent,
                                    },
                                ]
                            },
                        ]
                    },
                    {
                        component: ChatUsersComponent,
                        path: CHAT_ROUTE_PATHS.CHAT_USERS
                    },
                    {
                        component: ProfileSettingsComponent,
                        path: CHAT_ROUTE_PATHS.PROFILE_SETTINGS
                    },
                ]
            },
            {
                component: ChatLoginComponent,
                path: CHAT_ROUTE_PATHS.LOGIN
            },
            {
                component: ChatRegisterUserComponent,
                path: CHAT_ROUTE_PATHS.REGISTER
            },
        ]
    }
];
