package com.example.oopatmuiv3;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ATM {
    public void displayATMMenu() throws Exception {
        Scanner input = new Scanner(System.in);
        Bank theBank = new Bank("Bank of Kek");
        User curUser;
        // transfer/withdrawal limit testing code...please ignore
        //User currentUser = theBank.userLogin("8133389705", "6969");
        //Account currentAcc = currentUser.getAccount(0);
        //System.out.println(currentAcc.getOverseasTransferLimit());
        // currentAcc.changeTransferLimit("localTransferLimit", 999);
        // currentAcc.changeTransferLimit("overseasTransferLimit", 8888);
        // currentAcc.changeTransferLimit("localWithdrawLimit", 1);
        // currentAcc.changeTransferLimit("overseasWithdrawLimit", 6);

        try {
            while (true) {
                curUser = ATM.mainMenuPrompt(theBank, input);
                ATM.printUserMenu(curUser, input);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Exiting, closing all connections to server...\n");
            System.exit(0);
        }

        // Bank theBank = new Bank("Bank of Kek");
        // System.out.println(theBank.countUsers());
        // theBank.printInfo();
        // User currentUser = theBank.userLogin("8133389705", "6969");
        // Account newAcc = currentUser.addAccount("savings", theBank);

        // Account currentAccount = currentUser.getAccount(0);

        // currentAccount.transfer(currentUser.getAccount(1), 15);

        // currentAccount.deposit(100);
        //User firstUser = theBank.addUser("Kek", "asdasd", "1111", "Singapore");
        // Account savingsAccountForFirstUser = firstUser.addAccount("current",
        // theBank);
        // System.out.println(firstUser);
        // System.out.println(savingsAccountForFirstUser);
    }

    public static User mainMenuPrompt(Bank theBank, Scanner input) {
        String userID;
        String pin;
        User authUser;

        do {
            System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
            System.out.print("Enter User ID: ");
            userID = input.nextLine();
            System.out.print("Enter Pin: ");
            pin = input.nextLine();
            theBank.refreshUsers();
            authUser = theBank.userLogin(userID, pin);
            if (authUser == null) {
                System.out.println("Incorrect user ID/Pin combination! Please try again. ");
            }

        } while (authUser == null);
        return authUser;
    }

    public static int accSelect(User theUser, Scanner input, String msg){
        int acc;
        do {
            try {
                theUser.printAccountSummary();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.printf("Enter the number(1-%d) of the account " + msg, theUser.numOfAccounts());
            acc = input.nextInt() - 1;
            if (acc < 0 || acc >= theUser.numOfAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (acc < 0 || acc >= theUser.numOfAccounts());
        return acc;
    }

    public static void showTransactionHistory(User theUser, Scanner input) {
        int theAccount;
        theAccount = accSelect(theUser, input, "whose transaction you want to see: ");
        theUser.printTransactionHistory(theAccount);
    }

    public static void withdrawFunds(User theUser, Scanner input) throws Exception {
        int fromAcct;
        double amount;
        double acctBal;
        String memo;

        fromAcct = accSelect(theUser, input, "to withdraw from: ");
        acctBal = theUser.getAccount(fromAcct).getCurrency().convert(theUser.getAccountBalance(fromAcct));

        do{
            System.out.printf("Enter the amount to withdraw (max %s%.02f %s): %s", theUser.getAccount(fromAcct).getCurrency().getSymbolBefore(),acctBal, theUser.getAccount(fromAcct).getCurrency().getSymbolAfter() ,theUser.getAccount(fromAcct).getCurrency().getSymbolBefore());
            amount = input.nextDouble();
            try {
                if (amount < 0 || amount > acctBal || (BigDecimal.valueOf(amount).scale() > 2 || amount == -0)) {
                    throw new InvalidWithdrawAmountException(amount, acctBal);
                }
            } catch (InvalidWithdrawAmountException e) {
                e.errorMessage();
            }
            // if(amount < 0){
            //     System.out.println("Amount must be greater than zero.");
            // } else if (amount > acctBal) {
            //     System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", acctBal);
            // } else if ((BigDecimal.valueOf(amount).scale() > 2)){
            //     System.out.println("Amount must not have more than 2dp.");
            // } else if (amount == 0) {
            //     System.out.printf("Amount to withdraw can't be 0");
            // }
        }while(amount < 0 || amount > acctBal || (BigDecimal.valueOf(amount).scale() > 2));
        // takes rest of input
        input.nextLine();
        withdrawFundsHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(amount));
    }

    public static void withdrawFundsHelper(User theUser, int fromAcct, double amount) {
        try {
            theUser.getAccount(fromAcct).withdraw(amount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void depositFunds(User theUser, Scanner input) throws Exception{
        int fromAcct;
        double acctBal;
        String memo;

        fromAcct = accSelect(theUser, input, "to deposit in: ");
        acctBal = theUser.getAccountBalance(fromAcct);
        double amount;
        do{
            System.out.printf("Enter the amount to deposit: %s", theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
            amount = input.nextDouble();
            try {
                if (amount <= 0 || (BigDecimal.valueOf(amount).scale() > 2)) {
                    throw new InvalidAmountException(amount);
                }
            } catch (InvalidAmountException e) {
                e.errorMessage();
            }
            // if(amount < 0){
            //     System.out.println("Amount must be greater than zero.");
            // } else if ((BigDecimal.valueOf(amount).scale() > 2)){
            //     System.out.println("Amount must not have more than 2dp.");
            // } else if (amount == 0) {
            //     System.out.printf("Amount to deposit can't be 0");
            // }
        } while(amount < 0 || (BigDecimal.valueOf(amount).scale() > 2));

        // takes rest of input
        input.nextLine();
//        theUser.getAccount(fromAcct).deposit(amount);
        deposiFundstHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(amount));
    }

    public static void deposiFundstHelper(User theUser, int fromAcct, double amount) {
        try {
            theUser.getAccount(fromAcct).deposit(amount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void transferFunds(User theUser, Scanner input) throws Exception {
        int fromAcct;
        double acctBal;
        String memo;
        int toAcct;
        fromAcct = accSelect(theUser, input, "to transfer from: ");
        acctBal = theUser.getAccount(fromAcct).getCurrency().convert(theUser.getAccountBalance(fromAcct));
        boolean correctAcc = false;
        do {
            try {
                theUser.printAccountSummary();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.printf("%d) Other Accounts",theUser.numOfAccounts()+1);
            System.out.printf("\nEnter the number (1-%d) of the account" + " to transfer to: ", theUser.numOfAccounts()+1);
            toAcct = input.nextInt()-1;
            if (toAcct < 0 || toAcct >= theUser.numOfAccounts()+1) {
                System.out.println("Invalid account. Please try again.");
            }
            else if (toAcct==fromAcct){
                System.out.println("Invalid selection, select a different account. Please try again.");
            } else {
                correctAcc = true;
            }
        } while (toAcct < 0 || toAcct >= theUser.numOfAccounts()+1 || correctAcc == false);
        if (toAcct == theUser.numOfAccounts()){
            input.nextLine();
            System.out.printf("Enter the account number to transfer to: ");
            String toAcctOthr = (String)input.nextLine();
            double checkDestinationExist = theUser.getAccount(fromAcct).getOtherBal(toAcctOthr);
            double amounts = 0;
            if (checkDestinationExist == -1) {
                System.out.println("Account does not exist!");
                ATM.printUserMenu(theUser, input);
            } else {
                //System.out.println("Account exists");
                do {
                    System.out.printf("Enter the amount to transfer: %s", theUser.getAccount(fromAcct).getCurrency().getSymbolBefore());
                    amounts = input.nextDouble();
                    try {
                        if (amounts < 0 || (BigDecimal.valueOf(amounts).scale() > 2)) {
                            throw new InvalidTransferFundsException(amounts, acctBal);
                        }
                    } catch (InvalidTransferFundsException e) {
                        e.errorMessage();
                    }
                    // if(amounts <= 0){
                    //     System.out.println("Amount must be greater than zero.");
                    // } else if (amounts > acctBal) {
                    //     System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", acctBal);
                    // } else if ((BigDecimal.valueOf(amounts).scale() > 2)){
                    //     System.out.println("Amount must not have more than 2dp.");
                    // } 
                } while(amounts < 0 || amounts > acctBal || (BigDecimal.valueOf(amounts).scale() > 2));
            }
            // takes rest of input
            input.nextLine();
            // get a memo
            System.out.print("Enter a memo: ");
            memo = input.nextLine();
            //theUser.getAccount(fromAcct).otherTransfer(toAcctOthr, memo, amounts);
            otherTransferFundsHelper(theUser, fromAcct, toAcctOthr, theUser.getAccount(fromAcct).getCurrency().unconvert(amounts), memo);
            System.out.println("Transferred to OTHER " + toAcctOthr + " successfully!"); 
        }
        else{
            double amount;
            do{
                System.out.printf("Enter the amount to transfer: %s", theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                amount = input.nextDouble();
                try {
                    if (amount < 0 || amount > acctBal || (BigDecimal.valueOf(amount).scale() > 2)) {
                        throw new InvalidTransferFundsException(amount, acctBal);
                    }
                } catch (InvalidTransferFundsException e) {
                    e.errorMessage();
                }
                // if(amount <= 0){
                //     System.out.println("Amount must be greater than zero.");
                // } else if (amount > acctBal) {
                //     System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", acctBal);
                // } else if ((BigDecimal.valueOf(amount).scale() > 2)){
                //     System.out.println("Amount must not have more than 2dp.");
                // }
            }while(amount < 0 || amount > acctBal || (BigDecimal.valueOf(amount).scale() > 2));
            // takes rest of input
            input.nextLine();
            // get a memo
            System.out.print("Enter a memo: ");
            memo = input.nextLine();
            //theUser.getAccount(fromAcct).transfer(theUser.getAccount(toAcct), memo, amount);
            transferFundsHelper(theUser, fromAcct, toAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(amount), memo);
        }
    }

    public static void otherTransferFundsHelper(User theUser, int fromAcct, String toAcctOthr, double amounts, String memo){
        try {
            theUser.getAccount(fromAcct).otherTransfer(toAcctOthr, memo, amounts);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void transferFundsHelper(User theUser, int fromAcct, int toAcct, double amount, String memo){
        try {
            theUser.getAccount(fromAcct).transfer(theUser.getAccount(toAcct), memo, amount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void settings(User theUser, Scanner input) throws Exception {
        int choice;
        int fromAcct;
        double newLimit;
        double oldLimit;
        DecimalFormat df = new DecimalFormat("0.00");
        fromAcct = accSelect(theUser, input, "Please select an account to change settings for:");
        do {
            System.out.println("What would you like to change?");
            System.out.println("    1) Change Local Transfer Limit");
            System.out.println("    2) Change Overseas Transfer Limit");
            System.out.println("    3) Change Local Withdraw Limit");
            System.out.println("    4) Change Overseas Withdraw Limit");
            System.out.println("    5) Change currency");
            System.out.println("    6) Exit");
            System.out.print("Enter choice: ");
            choice = input.nextInt();
        } while (choice < 1 || choice > 6);
        switch (choice) {
            case 1:
                oldLimit = theUser.getAccount(fromAcct).getLocalTransferLimit();
                System.out.println("The current limit is: "+ theUser.getAccount(fromAcct).getCurrency().getSymbolBefore() + " " + df.format(theUser.getAccount(fromAcct).getCurrency().convert(oldLimit)) + " " + theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                System.out.println("Enter new local transfer limit: ");    
                newLimit = input.nextDouble();
                //theUser.getAccount(fromAcct).changeTransferLimit("localTransferLimit", input.nextDouble());
                localTransferLimitHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(newLimit));
                break;
            case 2:
                oldLimit = theUser.getAccount(fromAcct).getOverseasTransferLimit();
                System.out.println("The current limit is: " + theUser.getAccount(fromAcct).getCurrency().getSymbolBefore() + " " + df.format(theUser.getAccount(fromAcct).getCurrency().convert(oldLimit)) + " " + theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                System.out.println("Enter new overseas transfer limit: ");
                newLimit = input.nextDouble();
                //theUser.getAccount(fromAcct).changeTransferLimit("overseasTransferLimit", input.nextDouble());
                overseasTransferLimitHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(newLimit));
                break;
            case 3:
                oldLimit = theUser.getAccount(fromAcct).getLocalWithdrawLimit();
                System.out.println("The current limit is: " + theUser.getAccount(fromAcct).getCurrency().getSymbolBefore() + " " + df.format(theUser.getAccount(fromAcct).getCurrency().convert(oldLimit)) + " " + theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                System.out.println("Enter new local withdraw limit: ");
                newLimit = input.nextDouble();
                //theUser.getAccount(fromAcct).changeTransferLimit("localWithdrawLimit", input.nextDouble());
                localWithdrawLimitHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(newLimit));
                break;
            case 4:
                oldLimit = theUser.getAccount(fromAcct).getOverseasWithdrawLimit();
                System.out.println("The current limit is: " + theUser.getAccount(fromAcct).getCurrency().getSymbolBefore() + " " + df.format(theUser.getAccount(fromAcct).getCurrency().convert(oldLimit)) + " " + theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                System.out.println("Enter new overseas withdraw limit: ");
                newLimit = input.nextDouble();
                //theUser.getAccount(fromAcct).changeTransferLimit("overseasWithdrawLimit",input.nextDouble());
                overseasWithdrawLimitHelper(theUser, fromAcct, theUser.getAccount(fromAcct).getCurrency().unconvert(newLimit));
                break;
            case 5:
                Bank bank = new Bank("The Bank of kek");
                for(int i = 0; i < bank.getCurrencies().size(); i++) {
                    System.out.println(i + ") " + bank.getCurrencies().get(i).getSymbolAfter() + " - " + bank.getCurrencies().get(i).getSymbolBefore() + " " + bank.getCurrencies().get(i).getExchangeRate());
                }
                System.out.println("The current currency is: " + theUser.getAccount(fromAcct).getCurrency().getSymbolAfter());
                System.out.print("Enter the currency you want to convert to: ");
                int selectCurrency = input.nextInt();
                // bank.getCurrencies().size().get(selectCurrency);
                for (int i = 0; i < bank.getCurrencies().size(); i++) {
                    String symbol;
                    String country;
                    Double exchangeRate;
                    String countryAbbrev;
                    if (selectCurrency == i) {
                        selectCurrency = i;
                        country = bank.getCurrencies().get(selectCurrency).getCountry();
                        countryAbbrev = bank.getCurrencies().get(selectCurrency).getSymbolAfter();
                        symbol = bank.getCurrencies().get(selectCurrency).getSymbolBefore();
                        exchangeRate = bank.getCurrencies().get(selectCurrency).getExchangeRate();
                        changeCurrencyHelper(theUser, fromAcct, country, countryAbbrev, symbol, exchangeRate);
                    }
                }
                break;
            case 6:
                ATM.printUserMenu(theUser, input);
                break;
        } if (choice != 6) {
            ATM.printUserMenu(theUser, input);
        }
    }

    public static void changeCurrencyHelper(User theUser,int fromAcct, String country, String countryAbbrev, String symbol, double exchangeRate) {
        String currentCountryAbbrev = theUser.getAccount(fromAcct).getCurrency().getSymbolAfter();
        String acctHolder = theUser.getAccount(fromAcct).getHolder();
        try {
            System.out.println("Converting from " + currentCountryAbbrev + " to " + countryAbbrev + " for account " + acctHolder + "...");
            theUser.getAccount(fromAcct).setCountry(country);
            System.out.println("Updating user's country...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void localTransferLimitHelper(User theUser, int fromAcct, double newLimit) {
        try {
            theUser.getAccount(fromAcct).changeTransferLimit("localTransferLimit", newLimit);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void overseasTransferLimitHelper(User theUser, int fromAcct, double newLimit){
        try {
            theUser.getAccount(fromAcct).changeTransferLimit("overseasTransferLimit", newLimit);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void localWithdrawLimitHelper(User theUser, int fromAcct, double newLimit){
        try {
            theUser.getAccount(fromAcct).changeTransferLimit("localWithdrawLimit", newLimit);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void overseasWithdrawLimitHelper(User theUser, int fromAcct, double newLimit){
        try {
            theUser.getAccount(fromAcct).changeTransferLimit("overseasWithdrawLimit", newLimit);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void printUserMenu(User theUser, Scanner input) throws Exception {
        try {
            theUser.printAccountSummary();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int choice;
        do {
            System.out.printf("Welcome %s, what would you like to do? \n", theUser.getFirstName());
            System.out.println("    1) Show Account Transaction History");
            System.out.println("    2) Withdrawal");
            System.out.println("    3) Deposit");
            System.out.println("    4) Transfer");
            System.out.println("    5) Settings");
            System.out.println("    6) Quit");
            System.out.println();
            System.out.print("Enter choice: ");
            choice = input.nextInt();
            if (choice < 1 || choice > 6) {
                System.out.println("Invalid choice! Please choice 1-6");
            }
        } while (choice < 1 || choice > 6);
        switch (choice) {
            case 1:
                ATM.showTransactionHistory(theUser, input);
                break;
            case 2:
                ATM.withdrawFunds(theUser, input);
                break;
            case 3:
                ATM.depositFunds(theUser, input);
                break;
            case 4:
                ATM.transferFunds(theUser, input);
                break;
            case 5:
                ATM.settings(theUser, input);
                break;
            case 6:
                input.nextLine();
        }
        if (choice != 6) {
            ATM.printUserMenu(theUser, input);
        }
    }

}
