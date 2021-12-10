import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CardInfoDTO } from '../dtos/CardInfoDTO';
import { MerchantDTO } from '../dtos/MerchantDTO';

@Injectable({
  providedIn: 'root',
})
export class BankService {
  registerUrl = environment.banka1 + '/account/register';

  constructor(private _http: HttpClient) {}

  register(cardInfo: CardInfoDTO): Observable<MerchantDTO> {
    return this._http.post<MerchantDTO>(this.registerUrl, cardInfo);
  }
}
