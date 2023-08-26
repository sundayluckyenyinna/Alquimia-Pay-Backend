package com.gms.alquimiapay.test;



import java.util.*;

public class GetSuggestedBanks {

    private static final String TAG ="GetSuggestedBanks";

    private static HashMap<String, String> banksWithCodeHashMap = new HashMap<>();

    public static HashMap<String, String> getBanksWithCodeHashMap() {
        //populate();
        return banksWithCodeHashMap;
    }

    public static List<String> getOtherBanks() {
        Collections.sort(otherBanks, String::compareToIgnoreCase);
        return otherBanks;
    }

    public static void populate(final Map<String, String> bankCodeNamesMap) {

        // //Log.d("TransferFragment_", bankCodeNamesMap.keySet() +"___"+bankCodeNamesMap.values

        banksWithCodeHashMap.putAll(bankCodeNamesMap);

        // populate();
    }


    private static void populate() {
        banksWithCodeHashMap.put("ACCESS BANK", "044");
        banksWithCodeHashMap.put("ACCESSMOBILE", "323");
        banksWithCodeHashMap.put("ASO SAVINGS AND LOANS", "401");
        banksWithCodeHashMap.put("CELLULANT", "317");
        banksWithCodeHashMap.put("CENTRAL BANK OF NIGERIA", "001");
        banksWithCodeHashMap.put("CITIBANK", "023");
        banksWithCodeHashMap.put("CORONATION MERCHANT BANK", "559");
        banksWithCodeHashMap.put("CORPORETTI", "310");
        banksWithCodeHashMap.put("COVENANT MICROFINANCE BANK", "551");
        banksWithCodeHashMap.put("DIAMOND BANK", "063");
        banksWithCodeHashMap.put("EARTHOLEUM (QIK QIK)", "302");
        banksWithCodeHashMap.put("ECOBANK NIGERIA", "050");
        banksWithCodeHashMap.put("ECOMOBILE", "307");
        banksWithCodeHashMap.put("EKONDO MICROFINANCE BANK", "562");
        banksWithCodeHashMap.put("ENTERPRISE BANK", "084");
        banksWithCodeHashMap.put("EQUITORIAL TRUST BANK", "040");
        banksWithCodeHashMap.put("E-TRANZACT", "306");
        banksWithCodeHashMap.put("FBN M-MONEY", "309");
        banksWithCodeHashMap.put("FBN MORTGAGES", "413");
        banksWithCodeHashMap.put("FETS (MY WALLET)", "314");
        banksWithCodeHashMap.put("FIDELITY BANK", "070");
        banksWithCodeHashMap.put("FIDELITY MOBILE", "318");
        banksWithCodeHashMap.put("FINATRUST MICROFINANCE BANK", "608");
        banksWithCodeHashMap.put("FIRST BANK OF NIGERIA", "011");
        banksWithCodeHashMap.put("FIRST CITY MONUMENT BANK", "214");
        banksWithCodeHashMap.put("FIRST INLAND BANK", "085");
        banksWithCodeHashMap.put("FORTIS MICROFINANCE BANK", "501");
        banksWithCodeHashMap.put("FORTIS MOBILE", "308");
        banksWithCodeHashMap.put("FSDH", "601");
        banksWithCodeHashMap.put("GT MOBILE MONEY", "315");

        banksWithCodeHashMap.put("GUARANTY TRUST BANK", "058");

        banksWithCodeHashMap.put("HEDONMARK", "324");
        banksWithCodeHashMap.put("HERITAGE BANK", "030");
        banksWithCodeHashMap.put("IMPERIAL HOMES MORTGAGE BANK", "415");
        banksWithCodeHashMap.put("INTERCONTINENTAL BANK", "069");
        banksWithCodeHashMap.put("JAIZ BANK", "301");
        banksWithCodeHashMap.put("JUBILEE LIFE", "402");
        banksWithCodeHashMap.put("KEGOW", "303");
        banksWithCodeHashMap.put("KEYSTONE BANK", "082");
        banksWithCodeHashMap.put("MAINSTREET BANK", "014");
        banksWithCodeHashMap.put("MIMONEY (POWERED BY INTELLIFIN)", "330");
        banksWithCodeHashMap.put("M-KUDI", "313");
        banksWithCodeHashMap.put("MONETIZE", "312");
        banksWithCodeHashMap.put("MONEYBOX", "325");
        banksWithCodeHashMap.put("NEW PRUDENTIAL BANK", "561");
        banksWithCodeHashMap.put("NPF MFB", "552");
        banksWithCodeHashMap.put("OCEANIC BANK", "056");
        banksWithCodeHashMap.put("OMOLUABI SAVINGS AND LOANS", "606");
        banksWithCodeHashMap.put("ONE FINANCE", "565");
        banksWithCodeHashMap.put("PAGA", "327");
        banksWithCodeHashMap.put("PAGE MFBANK", "560");
        banksWithCodeHashMap.put("PARALLEX", "502");
        banksWithCodeHashMap.put("PARKWAY (READY CASH)", "311");
        banksWithCodeHashMap.put("PAYATTITUDE ONLINE", "329");
        banksWithCodeHashMap.put("PAYCOM", "304");
        banksWithCodeHashMap.put("PROVIDUS BANK", "101");
        banksWithCodeHashMap.put("SAFETRUST MORTGAGE BANK", "403");
        banksWithCodeHashMap.put("SEED CAPITAL MICROFINANCE BANK", "609");
        banksWithCodeHashMap.put("POLARIS BANK", "076");
        banksWithCodeHashMap.put("STANBIC IBTC BANK", "221");
        banksWithCodeHashMap.put("STANBIC MOBILE", "304");
        banksWithCodeHashMap.put("STANDARD CHARTERED BANK", "068");
        banksWithCodeHashMap.put("STERLING BANK", "232");
        banksWithCodeHashMap.put("STERLING MOBILE", "326");
        banksWithCodeHashMap.put("SUNTRUST", "100");
        banksWithCodeHashMap.put("TEASY MOBILE", "319");
        banksWithCodeHashMap.put("TRUSTBOND", "523");
        banksWithCodeHashMap.put("U-MO", "316");
        banksWithCodeHashMap.put("UNION BANK OF NIGERIA", "032");
        banksWithCodeHashMap.put("UNITED BANK FOR AFRICA", "033");
        banksWithCodeHashMap.put("UNITY BANK", "215");
        banksWithCodeHashMap.put("VFD MICROFINANCE BANK", "566");
        banksWithCodeHashMap.put("VISUAL ICT", "328");
        banksWithCodeHashMap.put("VTNETWORK", "320");
        banksWithCodeHashMap.put("WEMA BANK", "035");
        banksWithCodeHashMap.put("ZENITH BANK", "057");
        banksWithCodeHashMap.put("ZENITH MOBILE", "322");
    }

    private static int getCheckCode(String accountNumber) {

        accountNumber = accountNumber.trim();
        if (accountNumber.length() <= 3)return 0;
        String lastDigitStr = accountNumber.substring(accountNumber.length() - 1);
        int lastDigit = Integer.valueOf(lastDigitStr);
        switch (lastDigit) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            {
                return 10 - lastDigit;
            }
            default:
                return 10;
        }
    }

    private static int ComputeTheSecond(String accountNumber) throws Exception {
        if (accountNumber.length() != 10)
            throw new IllegalArgumentException("Invalid account number");

        String d = String.valueOf(accountNumber.charAt(0)),
                e = String.valueOf(accountNumber.charAt(1)),
                f = String.valueOf(accountNumber.charAt(2)),
                g = String.valueOf(accountNumber.charAt(3)),
                h = String.valueOf(accountNumber.charAt(4)),
                i = String.valueOf(accountNumber.charAt(5)),
                j = String.valueOf(accountNumber.charAt(6)),
                k = String.valueOf(accountNumber.charAt(7)),
                l = String.valueOf(accountNumber.charAt(8));

        int sum = 0;
        sum += (3 * Integer.valueOf(d));
        sum += (7 * Integer.valueOf(e));
        sum += (3 * Integer.valueOf(f));
        sum += (3 * Integer.valueOf(g));
        sum += (7 * Integer.valueOf(h));
        sum += (3 * Integer.valueOf(i));
        sum += (3 * Integer.valueOf(j));
        sum += (7 * Integer.valueOf(k));
        sum += (3 * Integer.valueOf(l));

        return sum;
    }

    private static int ComputeTheFirst(String digitCode)  {
        int sum = 0;
        String a = String.valueOf(digitCode.charAt(0)),
                b = String.valueOf(digitCode.charAt(1)),
                c = String.valueOf(digitCode.charAt(2));
        sum += (3 * Integer.valueOf(a));
        sum += (7 * Integer.valueOf(b));
        sum += (3 * Integer.valueOf(c));
        return sum;
    }

    private static List<String> otherBanks = new ArrayList<>();

    public static List<String> getSuggestedBanks(String accountNumber) throws Exception{
        List<String> suggestedBanks = new ArrayList<>();
        List<String> otherBanksLocal = new ArrayList<>();
        accountNumber = accountNumber.replaceAll("[-\\[\\]^/,'*:.!><~@#$%+=?|\"\\\\()]+", "");

        int accountCheckCode = getCheckCode(accountNumber);
        int secondPartSum = ComputeTheSecond(accountNumber);

        populate();
        for (Map.Entry<String, String> entry : banksWithCodeHashMap.entrySet()) {
            final String bankCode = entry.getValue();
            if (bankCode != null && !bankCode.trim().isBlank()) {
                int firstSum = ComputeTheFirst(bankCode);
                int genSum = firstSum + secondPartSum;
                int mod = genSum % 10;
                if ( isValidCode(bankCode, accountNumber)) {
                    suggestedBanks.add(entry.getKey());
                   /* if (bankCode.length() <= 3) {
                        suggestedBanks.add(entry.getKey());
                    }*/
                } else {
                    otherBanksLocal.add(entry.getKey());
                }
            }
        }
        Collections.sort(suggestedBanks, String::compareToIgnoreCase);
        otherBanks = new ArrayList<>(otherBanksLocal);
        return suggestedBanks;
    }



    static boolean isValidCode(String bankCode, String nubanNumber) {
        if (bankCode.length() > 3) {
            return false;
        }
        int codeValue = 0;

        List<Integer> factorList = new ArrayList<>(Arrays.asList(
                /* Start Bank Code */3, 7, 3, /* End Bank Code */
                /* Start Account Number Factor */
                3, 7, 3, 3, 7, 3, 3, 7, 3
                /* End Account Number Factor */));
        for (int i=0; i<bankCode.length(); i++) {
            int number = Integer.valueOf(String.valueOf(bankCode.charAt(i)));

            int first = factorList.remove(0);
            codeValue += (first * number);
        }


        int currentNubanIndex = 0;
        for (int i=0; i<nubanNumber.length();i++) {
            int number = Integer.valueOf(String.valueOf(nubanNumber.charAt(i)));
            if (currentNubanIndex < 9) {
                int first = factorList.remove(0);
                codeValue += (number * first);
            }
            currentNubanIndex += 1;
        }

        int modulus = codeValue % 10;
        int checkValue = 10 - modulus;
        if (checkValue == 10) {
            checkValue = 0;
        }
        if (nubanNumber.length() == 10) {
            int lastNumber = Integer.valueOf(String.valueOf(nubanNumber.charAt(9)));
            return lastNumber == checkValue;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getSuggestedBanks("0038565651"));
    }
}