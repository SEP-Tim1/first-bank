import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CardInfoDTO } from 'src/app/dtos/CardInfoDTO';
import { MerchantDTO } from 'src/app/dtos/MerchantDTO';
import { BankService } from 'src/app/service/bank.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit {
  dto = new CardInfoDTO('', '', '', '');
  merchant = new MerchantDTO(0, '');
  viewForm = true;

  panControl = new FormControl('', [
    Validators.required,
    Validators.maxLength(16),
    Validators.minLength(16),
    Validators.pattern('\\d{16}'),
  ]);

  cardHolderNameControl = new FormControl('', [Validators.required]);

  securityCodeControl = new FormControl('', [
    Validators.required,
    Validators.maxLength(3),
    Validators.minLength(3),
  ]);

  expirationDateControl = new FormControl('', [
    Validators.required,
    Validators.minLength(7),
    Validators.maxLength(7),
    Validators.pattern('\\d{2}/\\d{4}'),
  ]);

  constructor(
    private _snackBar: MatSnackBar,
    private _service: BankService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  register() {
    this._service.register(this.dto).subscribe(
      (response) => {
        console.log(response);
        this.merchant = response;
        this.viewForm = false;
        this.openSnackBar('BRAVOOOOOOOOOOO', 'ahahhahah');
      },
      (error) => {
        this.openSnackBar(error.error, 'Ok');
      }
    );
  }

  home() {
    this.router.navigate(['']);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 10000,
    });
  }
}
