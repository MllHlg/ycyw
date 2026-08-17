import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient, private zone: NgZone) {}

  getMessages(sessionId: string): Observable<any> {
    return new Observable(observer => {
      const eventSource = new EventSource(`${this.apiUrl}/stream/${sessionId}`);
      
      eventSource.addEventListener('message', event => {
        this.zone.run(() => {
          observer.next(JSON.parse(event.data));
        });
      });

      eventSource.onerror = error => {
        this.zone.run(() => {
          observer.error(error);
        });
      };

      return () => eventSource.close();
    });
  }

  sendMessage(messageRequest: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/send`, messageRequest, { responseType: 'text' });
  }

  getSessions(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/sessions/${userId}`);
  }
  
  createSession(userId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/sessions`, { userId });
  }

  getHistory(sessionId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history/${sessionId}`);
  }

  deleteSession(sessionId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/sessions/${sessionId}`, { responseType: 'text' });
  }
}