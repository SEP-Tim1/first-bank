import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { CardInfoDTO } from 'src/app/dtos/CardInfoDTO';
import { BankService } from 'src/app/service/bank.service';

@Component({
  selector: 'app-payment-info',
  templateUrl: './payment-info.component.html',
  styleUrls: ['./payment-info.component.css'],
})
export class PaymentInfoComponent implements OnInit {
  dto = new CardInfoDTO('', '', '', '');
  purchaseId = -1;
  invoice: any;

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
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.purchaseId = Number(this.route.snapshot.paramMap.get('id'));
    this._service.getInvoice(this.purchaseId).subscribe(
      response => this.invoice = response
    )
  }

  submit() {
    this._service.pay(this.purchaseId, this.dto).subscribe(
      (response) => {
        console.log(response);
        if (response.redirectUrl === '') {
          window.location.href =
            'http://localhost:4200/error/' + this.purchaseId;
        } else window.location.href = response.redirectUrl;
      },
      (error) => {
        this.openSnackBar(error.error);
      }
    );
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Ok', {
      duration: 10000,
    });
  }
}
