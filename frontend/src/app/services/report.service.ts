import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private apiUrl = `${environment.apiUrl}/reports`;

    constructor(private http: HttpClient) { }

    getViolationReports(period: string, groupBy: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/violations?period=${period}&groupBy=${groupBy}`);
    }

    getViolationsByUser(userId: number): Observable<any> {
        return this.http.get(`${this.apiUrl}/violations/user/${userId}`);
    }

    createMockViolation(violation: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/violations/test`, violation);
    }

    downloadViolationReportPdf(): Observable<Blob> {
        return this.http.get(`${this.apiUrl}/violations/pdf`, { responseType: 'blob' });
    }
}
