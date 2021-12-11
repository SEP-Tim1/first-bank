import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CardInfoDTO } from 'src/app/dtos/CardInfoDTO';
import { BankService } from 'src/app/service/bank.service';

@Component({
  selector: 'app-payment-info',
  templateUrl: './payment-info.component.html',
  styleUrls: ['./payment-info.component.css'],
})
export class PaymentInfoComponent implements OnInit {
  dto = new CardInfoDTO('', '', '', '');

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

  submit() {}

  openSnackBar(message: string) {
    this._snackBar.open(message, 'hehe', {
      duration: 10000,
    });
  }
}
