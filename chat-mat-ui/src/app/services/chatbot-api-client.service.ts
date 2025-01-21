import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChatResponse, UserInput} from '../models/chat.models';

const baseUrl = "http://127.0.0.1:5000/api";

@Injectable({providedIn: "root"})
export class ChatbotApiClientService {


    constructor(private httpClient: HttpClient) {
    }

    ///chatbot/programs
    public getPrograms(userInput: UserInput): Observable<ChatResponse>{
        return this.httpClient.post<ChatResponse>(
            `${baseUrl}/chatbot`,
            userInput
        )
    }
}
