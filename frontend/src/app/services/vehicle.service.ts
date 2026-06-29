import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class VehicleService {
    private apiUrl = `${environment.apiUrl}/vehicles`;

    constructor(private http: HttpClient) { }

    getVehicles(userId: number | null): Observable<any[]> {
        const url = userId ? `${this.apiUrl}?userId=${userId}` : this.apiUrl;
        return this.http.get<any[]>(url);
    }

    getTotalCount(): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/count`);
    }

    registerVehicle(vehicle: any): Observable<any> {
        return this.http.post<any>(this.apiUrl, vehicle);
    }

    updateVehicle(id: number, vehicle: any): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/${id}`, vehicle);
    }

    deleteVehicle(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }
}
