import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParkingPassService } from '../../services/parking-pass.service';
import { VehicleService } from '../../services/vehicle.service';
import { AuthService } from '../../services/auth.service';
import { MasterDataService, ParkingZone } from '../../services/master-data.service';

@Component({
    selector: 'app-passes',
    templateUrl: './passes.component.html',
    styleUrls: ['./passes.component.css'],
    standalone: false
})
export class PassesComponent implements OnInit {
    passes: any[] = [];
    vehicles: any[] = [];
    zones: ParkingZone[] = [];
    passForm: FormGroup;
    currentUser: any;
    showPurchaseForm = false;

    constructor(
        private passService: ParkingPassService,
        private vehicleService: VehicleService,
        private authService: AuthService,
        private masterDataService: MasterDataService,
        private fb: FormBuilder
    ) {
        this.passForm = this.fb.group({
            licensePlates: [[], [Validators.required, (control: any) => control.value?.length > 4 ? { maxPlates: true } : null]],
            zone: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        this.authService.currentUser.subscribe(user => {
            this.currentUser = user;
            if (this.currentUser) {
                console.log('DEBUG: User logged in, loading passes for ID:', this.currentUser.id);
                this.loadPasses();
                this.loadVehicles();
            }
        });
        this.loadMasterData();
    }

    loadMasterData() {
        this.masterDataService.getZones().subscribe(data => this.zones = data);
    }

    loadPasses() {
        const userId = this.currentUser.role === 'CITIZEN' ? this.currentUser.id : null;
        console.log('DEBUG: Calling getPasses API with userId:', userId);
        this.passService.getPasses(userId).subscribe({
            next: (data) => {
                console.log('DEBUG: Received passes from API:', data);
                this.passes = data;
            },
            error: (err) => {
                console.error('DEBUG: Failed to load passes:', err);
            }
        });
    }

    loadVehicles() {
        this.vehicleService.getVehicles(this.currentUser.id).subscribe(data => {
            this.vehicles = data;
        });
    }

    onPurchase() {
        if (this.passForm.valid) {
            const passData = {
                ...this.passForm.value,
                ownerId: this.currentUser.id
            };
            console.log('Attempting purchase:', passData);
            this.passService.purchasePass(passData).subscribe({
                next: (response) => {
                    console.log('Purchase success:', response);
                    this.loadPasses();
                    this.passForm.reset({ zone: '', licensePlates: [] });
                    this.showPurchaseForm = false;
                },
                error: (err) => {
                    console.error('Purchase failed:', err);
                    alert(err.error?.error || 'Could not purchase pass. Please check if you already have an active pass for this zone.');
                }
            });
        }
    }

    togglePlate(plate: string) {
        const plates = this.passForm.get('licensePlates')?.value as string[];
        if (plates.includes(plate)) {
            this.passForm.get('licensePlates')?.setValue(plates.filter(p => p !== plate));
        } else if (plates.length < 4) {
            this.passForm.get('licensePlates')?.setValue([...plates, plate]);
        }
    }

    isValid(expiryDate: string): boolean {
        return new Date(expiryDate) > new Date();
    }
}
