import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { VehiclesComponent } from './components/vehicles/vehicles.component';
import { PassesComponent } from './components/passes/passes.component';
import { SummaryComponent } from './components/summary/summary.component';
import { RegisterComponent } from './components/register/register.component';
import { BackOfficeComponent } from './components/back-office/back-office.component';
import { AuthService } from './services/auth.service';
import { VehicleService } from './services/vehicle.service';
import { ParkingPassService } from './services/parking-pass.service';
import { MasterDataService } from './services/master-data.service';

const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    {
        path: 'dashboard',
        component: DashboardComponent,
        children: [
            { path: 'summary', component: SummaryComponent },
            { path: 'vehicles', component: VehiclesComponent },
            { path: 'passes', component: PassesComponent },
            { path: 'back-office', component: BackOfficeComponent },
            { path: '', redirectTo: 'summary', pathMatch: 'full' }
        ]
    },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        VehiclesComponent,
        PassesComponent,
        SummaryComponent,
        RegisterComponent,
        BackOfficeComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        RouterModule.forRoot(routes)
    ],
    providers: [
        AuthService,
        VehicleService,
        ParkingPassService,
        MasterDataService
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
