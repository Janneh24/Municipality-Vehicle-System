import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MasterDataService, MasterData } from '../../services/master-data.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css'],
    standalone: false
})
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    error: string = '';
    districts: MasterData[] = [];

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private masterDataService: MasterDataService,
        private router: Router
    ) {
        this.registerForm = this.fb.group({
            fullName: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            username: ['', Validators.required],
            password: ['', Validators.required],
            district: ['', Validators.required],
            role: ['CITIZEN', Validators.required]
        });
    }

    ngOnInit(): void {
        this.masterDataService.getDistricts().subscribe(data => this.districts = data);
    }

    onSubmit() {
        if (this.registerForm.valid) {
            this.authService.register(this.registerForm.value).subscribe({
                next: () => {
                    this.router.navigate(['/login']);
                },
                error: (err: any) => {
                    this.error = err.error?.error || 'Registration failed';
                }
            });
        }
    }
}
