import { Routes } from '@angular/router';
import {ChatHomeComponent} from './chat-home/chat-home.component';
import {ChatChannelComponent} from './chat-channel/chat-channel.component';
import {ChatLoginComponent} from './auth-components/chat-login/chat-login.component';
import {ChatRegisterUserComponent} from './auth-components/chat-register-user/chat-register-user.component';

export const CHAT_ROUTE_PATHS = {
    HOME: "home",
    CHAT_CHANEL: "chat-chanel",
    LOGIN: "login",
    REGISTER: "register",
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
                component: ChatHomeComponent,
                children: [
                    {
                        path: "",
                        redirectTo: CHAT_ROUTE_PATHS.CHAT_CHANEL,
                        pathMatch: "full",
                    },
                    {
                        component: ChatChannelComponent,
                        path: CHAT_ROUTE_PATHS.CHAT_CHANEL
                    }
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
