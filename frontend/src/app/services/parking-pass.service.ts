import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ParkingPassService {
    private apiUrl = `${environment.apiUrl}/passes`;

    constructor(private http: HttpClient) { }

    getPasses(userId: number | null): Observable<any[]> {
        const url = userId ? `${this.apiUrl}?userId=${userId}` : this.apiUrl;
        return this.http.get<any[]>(url);
    }

    getTotalCount(): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/count`);
    }

    purchasePass(pass: any): Observable<any> {
        return this.http.post<any>(this.apiUrl, pass);
    }

    checkValidity(licensePlate: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/check/${licensePlate}`);
    }
}
