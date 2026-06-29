import { Component, OnInit } from '@angular/core';
import { MasterDataService, MasterData, ParkingZone } from '../../services/master-data.service';

@Component({
    selector: 'app-back-office',
    templateUrl: './back-office.component.html',
    styleUrls: ['./back-office.component.css'],
    standalone: false
})
export class BackOfficeComponent implements OnInit {
    vehicleTypes: MasterData[] = [];
    engineTypes: MasterData[] = [];
    districts: MasterData[] = [];
    zones: ParkingZone[] = [];

    newVehicleType = '';
    newEngineType = '';
    newDistrict = '';
    newDistrictDescription = '';
    newZoneCode = '';
    newZoneDistrictId: number | null = null;

    constructor(private masterDataService: MasterDataService) { }

    ngOnInit(): void {
        this.loadAll();
    }

    loadAll() {
        this.masterDataService.getVehicleTypes().subscribe(data => this.vehicleTypes = data);
        this.masterDataService.getEngineTypes().subscribe(data => this.engineTypes = data);
        this.masterDataService.getDistricts().subscribe(data => this.districts = data);
        this.masterDataService.getZones().subscribe(data => this.zones = data);
    }

    addVehicleType() {
        if (!this.newVehicleType) return;
        this.masterDataService.createVehicleType({ name: this.newVehicleType }).subscribe(() => {
            this.newVehicleType = '';
            this.loadAll();
        });
    }

    deleteVehicleType(id: number) {
        this.masterDataService.deleteVehicleType(id).subscribe(() => this.loadAll());
    }

    addEngineType() {
        if (!this.newEngineType) return;
        this.masterDataService.createEngineType({ name: this.newEngineType }).subscribe(() => {
            this.newEngineType = '';
            this.loadAll();
        });
    }

    deleteEngineType(id: number) {
        this.masterDataService.deleteEngineType(id).subscribe(() => this.loadAll());
    }

    addDistrict() {
        if (!this.newDistrict) return;
        this.masterDataService.createDistrict({
            name: this.newDistrict,
            description: this.newDistrictDescription
        }).subscribe(() => {
            this.newDistrict = '';
            this.newDistrictDescription = '';
            this.loadAll();
        });
    }

    deleteDistrict(id: number) {
        this.masterDataService.deleteDistrict(id).subscribe(() => this.loadAll());
    }

    addZone() {
        if (!this.newZoneCode || !this.newZoneDistrictId) return;
        this.masterDataService.createZone({ code: this.newZoneCode, districtId: this.newZoneDistrictId }).subscribe(() => {
            this.newZoneCode = '';
            this.newZoneDistrictId = null;
            this.loadAll();
        });
    }

    deleteZone(id: number) {
        this.masterDataService.deleteZone(id).subscribe(() => this.loadAll());
    }
}
