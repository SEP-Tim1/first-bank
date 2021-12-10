export class CardInfoDTO {
  constructor(
    public pan: string,
    public cardHolderName: string,
    public securityCode: string,
    public expirationDate: string
  ) {}
}
