package com.example.oopatmuiv3;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;

public class ATMTest {

    private User testUser;
    private User testUser2;

    private int i;
    private Currency currency;

    //Test Objects
    @BeforeEach
    public void setup() {
        try {
            Bank testBank = new Bank("TestBank");
            testUser = testBank.userLogin("5044891741", "1111");
            testUser2 = testBank.userLogin("8133389705", "6969");
            testUser.setCountry("Singapore");
            testUser2.setCountry("Singapore");
        } catch (InvalidLoginException e) {
            //Test data invalid
            System.out.println("Test Data Invalid!");
        }
    }


    //DEPOSIT CASES
    @Test
    @DisplayName("Should not throw an exception $100")
    void testDeposit() throws InvalidAmountException, InvalidNoteDepositException {
        //double startBal = testUser.getAccount(0).getBalance();
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            testUser.getAccount(i).deposit(100);
            testUser.getCountry();
            assertDoesNotThrow(() -> InvalidAmountException.class);
            assertDoesNotThrow(() -> InvalidNoteDepositException.class);
        }
    }


    @Test
    @DisplayName("Invalid due to 0 value")
    public void testDepositValue0() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidAmountException.class, () -> testUser.getAccount(i).deposit(0));
        }
    }

    @Test
    @DisplayName("Invalid amount deposited")
    public void testInvalidAmountException() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidAmountException.class, () -> testUser.getAccount(i).deposit(-1));
        }
    }

    @Test
    @DisplayName("Invalid note due to decimal place error")
    public void testInvalidNoteDepositException() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidAmountException.class, () -> testUser.getAccount(i).deposit(1.696969696969));
        }
    }


    @Test
    @DisplayName("Should not throw an exception $7")
    public void testPassException() throws InvalidAmountException, InvalidNoteDepositException {
        //for (int i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            testUser.getAccount(0).deposit(7);
            assertDoesNotThrow(() -> InvalidAmountException.class);
            assertDoesNotThrow(() -> InvalidNoteDepositException.class);
    //     }
    }


    // ------------------------------------------------------------------------------------------

    //WITHDRAWAL CASES
    @Test
    @DisplayName("Invalid Amount as cannot withdraw past max amount")
    public void testmaxAmountWithdraw() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser.getAccount(i).withdraw(testUser.getAccount(0).getBalance() + 1));
        }
    }

    @Test
    @DisplayName("Invalid amount as cannot withdraw more than 2 dp")
    public void testDecimalWithdraw() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser.getAccount(i).withdraw(1.69696969));
        }
    }

    @Test
    @DisplayName("Invalid amount as cannot withdraw 0")
    public void testWithdrawZero() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser.getAccount(i).withdraw(0));
        }
    }

    @Test
    @DisplayName("Invalid amount as cannot withdraw negative values")
    public void testInValidWithdrawAndTransferAmountException4() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            int finalI = i;
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser.getAccount(i).withdraw(-20));
        }
    }

    //Demo Acc needed
    @Test
    @DisplayName("Invalid amount as cannot withdraw past max")
    public void testWithdrawLimitException() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(WithdrawLimitException.class, () -> testUser.getAccount(i).withdraw(1000));
        }
    }

    //Demo Acc needed
    @Test
    @DisplayName("Invalid amount as not accepted notes")
    public void testInvalidWithdrawalException() {
        for (i = 0; i < testUser.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidNoteWithdrawalException.class, () -> testUser.getAccount(i).withdraw(60));
        }
    }


    // ------------------------------------------------------------------------------------------

    //TRANSFER CASES
    // Need demo acc
    @Test
    @DisplayName("Should not throw an exception transfer")
    void testTransfer() throws InvalidWithdrawAndTransferAmountException, TransferLimitException {
        for(i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            //double startBal = testUser.getAccount(0).getBalance();
            testUser2.getAccount(i).transfer("8133389705", "transferTest", 66.66);
            testUser2.getCountry();
            assertDoesNotThrow(() -> InvalidWithdrawAndTransferAmountException.class);
            assertDoesNotThrow(() -> TransferLimitException.class);
        }
    }

    @Test
    @DisplayName("Invalid amount due to transfer amount = 0")
    public void testTransferZero() {
        for (i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser2.getAccount(i).transfer("8133389705", "transferTest", 0));
        }
    }
    @Test
    @DisplayName("Invalid amount due to negative amount")
    public void testNegativeAmount() {
        for (i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser2.getAccount(i).transfer("8133389705", "transferTest", -20));
        }
    }
    @Test
    @DisplayName("Invalid amount due to value more than balance")
    public void testExceedMaxBalance() {
        for (i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser2.getAccount(i).transfer("8133389705", "transferTest", testUser2.getAccount(0).getBalance() + 1));
        }
    }
    @Test
    @DisplayName("Invalid amount due to more than 2 dp")
    public void testDecimalTransfer() {
        for (i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            assertThrows(InvalidWithdrawAndTransferAmountException.class, () -> testUser2.getAccount(i).transfer("8133389705", "transferTest", 1.696969));
        }
    }
    // TO CHANGE

    @Test
    @DisplayName("Invalid transfer due to transfer limit")
    public void testTransferLimit(){
        for(i = 0; i < testUser2.getAllAccountsUUID().size(); i++) {
            assertThrows(TransferLimitException.class, () ->
                    testUser2.getAccount(i).transfer("8133389705", "transferTest", 100000));
        }
    }
}

