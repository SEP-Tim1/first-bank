package sep.firstbank.util;

import sep.firstbank.dtos.CardInfoDTO;
import sep.firstbank.exceptions.CreditCardInfoNotValidException;
import sep.firstbank.model.CreditCard;

import java.time.LocalDate;

public class CreditCardValidator {
    public static void validate (CreditCard card, CardInfoDTO dto) throws CreditCardInfoNotValidException {
        if(!validateExpirationDate(card.getExpirationDate(), dto.getExpirationDate())
                || !validateCardHolderName(card.getCardHolderName(), dto.getCardHolderName())
                || !validateSecurityCode(card.getSecurityCode(), dto.getSecurityCode())){
            throw new CreditCardInfoNotValidException();
        }
    }

    private static boolean validateSecurityCode(String hardCode, String dtoCode) {
        return hardCode.equals(dtoCode);
    }

    private static boolean validateCardHolderName(String cardName, String dtoName) {
        return  cardName.equals(dtoName);
    }

    private static boolean validateExpirationDate(LocalDate expDate, String dtoDate) throws CreditCardInfoNotValidException {
        LocalDate dtoExpDate = getDateFromDtoDate(dtoDate);
        if(dtoExpDate.getMonthValue() != expDate.getMonthValue() || dtoExpDate.getYear() != expDate.getYear()) {
            return false;
        }
        return isNotExpired(expDate.getYear(), expDate.getMonthValue());
    }

    private static LocalDate getDateFromDtoDate(String dtoDate) throws CreditCardInfoNotValidException {
        if(getMonth(dtoDate) < 1 || getMonth(dtoDate) > 12) throw new CreditCardInfoNotValidException();
        return LocalDate.of(getYear(dtoDate), getMonth(dtoDate), 1);
    }

    private static boolean isNotExpired(int year, int month) {
        if(year < LocalDate.now().getYear()) return false;
        return year != LocalDate.now().getYear() || month >= LocalDate.now().getMonthValue();
    }

    private static int getMonth(String dtoDate){
        return Integer.parseInt(dtoDate.split("/")[0]);
    }
    private static int getYear(String dtoDate){
        return Integer.parseInt(dtoDate.split("/")[1]);
    }

}
