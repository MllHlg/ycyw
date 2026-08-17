import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ChatService } from '../../core/services/chat';
import { Subscription } from 'rxjs';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  imports: [CommonModule, DatePipe, FormsModule],
  templateUrl: './chat.html',
  styleUrl: './chat.scss'
})
export class ChatComponent implements OnInit, OnDestroy {
  messages: any[] = [];
  newMessage: string = '';
  sessions: any[] = [];
  activeSessionId: string | null = null;
  private messageSubscription!: Subscription;

  userId = '123e4567-e89b-12d3-a456-426614174001';

  constructor(private chatService: ChatService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadSessions();
  }

  ngOnDestroy(): void {
    this.closeCurrentStream();
  }

  loadSessions(): void {
    this.chatService.getSessions(this.userId).subscribe({
      next: (data) => {
        this.sessions = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur lors du chargement des sessions:', err)
    });
  }

  createNewSession(): void {
    this.chatService.createSession(this.userId).subscribe(newSession => {
      this.sessions.unshift(newSession);
      this.selectSession(newSession.id);
    });
  }

  selectSession(sessionId: string): void {
    if (this.activeSessionId && this.activeSessionId !== sessionId && this.messages.length === 0) {
      const idToDelete = this.activeSessionId;
      
      this.chatService.deleteSession(idToDelete).subscribe({
        next: () => {
          this.sessions = this.sessions.filter(s => s.id !== idToDelete);
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erreur lors de la suppression de la session', err)
      });
    }

    this.activeSessionId = sessionId;
    this.messages = [];
    this.closeCurrentStream();

    this.chatService.getHistory(sessionId).subscribe(history => {
      this.messages = history;
      this.cdr.detectChanges();
    });

    this.messageSubscription = this.chatService.getMessages(sessionId).subscribe({
      next: (message) => {
        this.messages.push(message),
        this.cdr.detectChanges();
      },
      error: (err) => console.error('SSE Error', err)
    });
  }

  closeCurrentStream(): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
  }

  send(): void {
    if (!this.newMessage.trim() || !this.activeSessionId) return;

    const request = {
      sessionId: this.activeSessionId,
      senderId: this.userId,
      content: this.newMessage
    };

    this.newMessage = '';

    this.chatService.sendMessage(request).subscribe({
      error: (err) => console.error('Erreur envoi:', err)
    });
  }
}