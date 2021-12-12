import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CardInfoDTO } from '../dtos/CardInfoDTO';
import { MerchantDTO } from '../dtos/MerchantDTO';
import { RedirectUrlDTO } from '../dtos/RedirectUrlDTO';

@Injectable({
  providedIn: 'root',
})
export class BankService {
  registerUrl = environment.banka1 + '/account/register';
  payUrl = environment.banka1 + '/account/payment/';

  constructor(private _http: HttpClient) {}

  register(cardInfo: CardInfoDTO): Observable<MerchantDTO> {
    return this._http.post<MerchantDTO>(this.registerUrl, cardInfo);
  }

  pay(invoiceId: number, dto: CardInfoDTO): Observable<RedirectUrlDTO> {
    return this._http.post<RedirectUrlDTO>(this.payUrl + invoiceId, dto);
  }
}
