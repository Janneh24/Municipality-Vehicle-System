import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface MasterData {
    id?: number;
    name: string;
    description?: string;
}

export interface ParkingZone {
    id?: number;
    code: string;
    districtId: number;
    districtName?: string;
}

@Injectable({
    providedIn: 'root'
})
export class MasterDataService {
    private apiUrl = `${environment.apiUrl}/masterdata`;

    constructor(private http: HttpClient) { }

    // Vehicle Types
    getVehicleTypes(): Observable<MasterData[]> {
        return this.http.get<MasterData[]>(`${this.apiUrl}/vehicle-types`);
    }
    createVehicleType(data: MasterData): Observable<MasterData> {
        return this.http.post<MasterData>(`${this.apiUrl}/vehicle-types`, data);
    }
    deleteVehicleType(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/vehicle-types/${id}`);
    }

    // Engine Types
    getEngineTypes(): Observable<MasterData[]> {
        return this.http.get<MasterData[]>(`${this.apiUrl}/engine-types`);
    }
    createEngineType(data: MasterData): Observable<MasterData> {
        return this.http.post<MasterData>(`${this.apiUrl}/engine-types`, data);
    }
    deleteEngineType(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/engine-types/${id}`);
    }

    // Districts
    getDistricts(): Observable<MasterData[]> {
        return this.http.get<MasterData[]>(`${this.apiUrl}/districts`);
    }
    createDistrict(data: MasterData): Observable<MasterData> {
        return this.http.post<MasterData>(`${this.apiUrl}/districts`, data);
    }
    deleteDistrict(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/districts/${id}`);
    }

    // Parking Zones
    getZones(): Observable<ParkingZone[]> {
        return this.http.get<ParkingZone[]>(`${this.apiUrl}/zones`);
    }
    createZone(data: ParkingZone): Observable<ParkingZone> {
        return this.http.post<ParkingZone>(`${this.apiUrl}/zones`, data);
    }
    deleteZone(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/zones/${id}`);
    }
}
