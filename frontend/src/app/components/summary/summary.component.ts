import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { VehicleService } from '../../services/vehicle.service';
import { ParkingPassService } from '../../services/parking-pass.service';
import { ReportService } from '../../services/report.service';

@Component({
    selector: 'app-summary',
    templateUrl: './summary.component.html',
    styleUrls: ['./summary.component.css'],
    standalone: false
})
export class SummaryComponent implements OnInit {
    currentUser: any;
    vehicleCount = 0;
    activePassCount = 0;
    violationReport: { [key: string]: number } | null = null;
    myViolations: any[] = [];
    myVehicles: any[] = [];
    myPasses: any[] = [];

    // Police search
    searchPlate = '';
    searchResult: { status: string, expiryDate?: string } | null = null;

    selectedPeriod = 'all';
    selectedGroupBy = 'district';

    constructor(
        private authService: AuthService,
        private vehicleService: VehicleService,
        private passService: ParkingPassService,
        private reportService: ReportService
    ) { }

    ngOnInit(): void {
        this.authService.currentUser.subscribe(user => {
            this.currentUser = user;
            if (this.currentUser) {
                this.loadStats();
                this.loadReport();
                if (this.currentUser.role === 'CITIZEN') {
                    this.loadMyViolations();
                }
            }
        });
    }

    loadStats() {
        if (this.currentUser.role === 'CITIZEN') {
            this.vehicleService.getVehicles(this.currentUser.id).subscribe(data => {
                this.myVehicles = data;
                this.vehicleCount = data.length;
            });
            this.passService.getPasses(this.currentUser.id).subscribe(data => {
                this.myPasses = data;
                this.activePassCount = data.filter((p: any) => new Date(p.expiryDate) > new Date()).length;
            });
        } else {
            // Load global stats for Admin/Law Enforcement
            this.vehicleService.getTotalCount().subscribe(count => this.vehicleCount = count);
            this.passService.getTotalCount().subscribe(count => this.activePassCount = count);
        }
    }

    loadReport() {
        if (this.currentUser.role !== 'CITIZEN') {
            this.reportService.getViolationReports(this.selectedPeriod, this.selectedGroupBy).subscribe(data => {
                this.violationReport = data;
            });
        }
    }

    loadMyViolations() {
        this.reportService.getViolationsByUser(this.currentUser.id).subscribe(data => {
            this.myViolations = data;
        });
    }

    checkPassValidity() {
        if (!this.searchPlate) return;
        this.passService.checkValidity(this.searchPlate).subscribe({
            next: (res) => this.searchResult = res,
            error: () => this.searchResult = { status: 'INVALID' }
        });
    }

    onReportParamsChange() {
        this.loadReport();
    }

    exportPdf() {
        this.reportService.downloadViolationReportPdf().subscribe(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'violation-report.pdf';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        });
    }

    simulateViolation() {
        const mockViolation = {
            licensePlate: 'ABC-' + Math.floor(Math.random() * 1000),
            district: 'D' + (Math.floor(Math.random() * 5) + 1),
            vehicleType: 'CAR',
            engineType: 'ICE (Combustion)',
            timestamp: new Date().toISOString()
        };

        this.reportService.createMockViolation(mockViolation).subscribe(() => {
            alert('A mock violation has been recorded!');
            this.loadReport();
        });
    }

    isPassValid(p: any): boolean {
        return new Date(p.expiryDate) > new Date();
    }
}
