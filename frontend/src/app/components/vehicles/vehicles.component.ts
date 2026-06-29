import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VehicleService } from '../../services/vehicle.service';
import { AuthService } from '../../services/auth.service';
import { MasterDataService, MasterData } from '../../services/master-data.service';
import { ReportService } from '../../services/report.service';

@Component({
    selector: 'app-vehicles',
    templateUrl: './vehicles.component.html',
    styleUrls: ['./vehicles.component.css'],
    standalone: false
})
export class VehiclesComponent implements OnInit {
    vehicles: any[] = [];
    vehicleForm: FormGroup;
    currentUser: any;
    showAddForm = false;
    editingVehicleId: number | null = null;

    vehicleTypes: MasterData[] = [];
    engineTypes: MasterData[] = [];

    constructor(
        private vehicleService: VehicleService,
        private authService: AuthService,
        private masterDataService: MasterDataService,
        private reportService: ReportService,
        private fb: FormBuilder
    ) {
        this.vehicleForm = this.fb.group({
            licensePlate: ['', Validators.required],
            model: ['', Validators.required],
            color: ['', Validators.required],
            vehicleType: ['', Validators.required],
            engineType: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        this.authService.currentUser.subscribe(user => {
            this.currentUser = user;
            if (this.currentUser) {
                this.loadVehicles();
            }
        });
        this.loadMasterData();
    }

    loadMasterData() {
        this.masterDataService.getVehicleTypes().subscribe(data => this.vehicleTypes = data);
        this.masterDataService.getEngineTypes().subscribe(data => this.engineTypes = data);
    }

    loadVehicles() {
        const userId = this.currentUser.role === 'CITIZEN' ? this.currentUser.id : null;
        this.vehicleService.getVehicles(userId).subscribe(data => {
            this.vehicles = data;
        });
    }

    editVehicle(v: any) {
        this.editingVehicleId = v.id;
        this.vehicleForm.patchValue({
            licensePlate: v.licensePlate,
            model: v.model,
            color: v.color,
            vehicleType: v.vehicleType,
            engineType: v.engineType
        });
        this.showAddForm = true;
    }

    onSubmit() {
        if (this.vehicleForm.valid) {
            const vehicleData = {
                ...this.vehicleForm.value,
                ownerId: this.currentUser.id
            };

            if (this.editingVehicleId) {
                this.vehicleService.updateVehicle(this.editingVehicleId, vehicleData).subscribe(() => {
                    this.resetForm();
                });
            } else {
                this.vehicleService.registerVehicle(vehicleData).subscribe(() => {
                    this.resetForm();
                });
            }
        }
    }

    private resetForm() {
        this.loadVehicles();
        this.vehicleForm.reset();
        this.showAddForm = false;
        this.editingVehicleId = null;
    }

    deleteVehicle(id: number) {
        if (confirm('Are you sure you want to remove this vehicle?')) {
            this.vehicleService.deleteVehicle(id).subscribe(() => {
                this.loadVehicles();
            });
        }
    }

    simulateViolation(v: any) {
        const mockViolation = {
            licensePlate: v.licensePlate,
            district: this.currentUser.district,
            vehicleType: v.vehicleType,
            engineType: v.engineType
        };

        console.log('DEBUG: Attempting to simulate violation:', mockViolation);

        this.reportService.createMockViolation(mockViolation).subscribe({
            next: (res) => {
                console.log('DEBUG: Violation simulation success:', res);
                alert('A demo violation for ' + v.licensePlate + ' has been recorded! Check the Summary (Admin/Law Enforcement) to see it.');
            },
            error: (err) => {
                console.error('DEBUG: Violation simulation error:', err);
                alert('Error simulating violation: ' + (err.error?.error || err.message));
            }
        });
    }
}
