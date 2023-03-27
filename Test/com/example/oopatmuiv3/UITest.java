package com.example.oopatmuiv3;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.ListViewMatchers;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

// This checks if the UI elements work the way they should in javafx using testfx
public class UITest extends ApplicationTest {

    public String c;
    ComboBox<String> comboBox;
    //@Rule
    //public final ExpectedException loginException = ExpectedException.none();
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginPage.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Kek Bank");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/kek.png"))));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        ApplicationTest.launch(Login.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testLoginInput1() {
        clickOn("#userID");
        write("5044891741");
        clickOn("#loginPIN");
        write("1111");
        clickOn("#loginButton");
        closeCurrentWindow();
    }

    @Test
    public void testLoginInput2(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
    }

    @Test
    public void failLoginInput(){
        clickOn("#userID");
        write("asdfg");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        FxAssert.verifyThat("#loginLabel", (Label l)-> l.getText().contains("Invalid Account ID or PIN"));
        closeCurrentWindow();
    }

    @Test
    public void checkHomePane(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        clickOn("#homeButton");
        FxAssert.verifyThat("#name", (Label l) -> l.getText().contains("Fish F"));
        FxAssert.verifyThat("#name", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#usrID", (Label l) -> l.getText().contains("8133389705"));
        FxAssert.verifyThat("#usrID", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#countryLabel", NodeMatchers.isVisible());
        FxAssert.verifyThat("#countryLabel", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#accNumber", NodeMatchers.isVisible());
        FxAssert.verifyThat("#accNumber", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#balance", NodeMatchers.isVisible());
        FxAssert.verifyThat("#balance", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#accLsOverview", ListViewMatchers.hasItems(2));
        FxAssert.verifyThat("#accLsOverview", NodeMatchers.isNotNull());
        clickOn("#accLsOverview");
        FxAssert.verifyThat("#transactionLs", NodeMatchers.isVisible());
    }

    @Test
    public void checkDepositPane(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        clickOn("#depositButton");
        FxAssert.verifyThat("#accNumberDeposit", NodeMatchers.isVisible());
        FxAssert.verifyThat("#accNumberDeposit", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#accLsDeposit", ListViewMatchers.hasItems(2));
        FxAssert.verifyThat("#accLsDeposit", NodeMatchers.isNotNull());
        clickOn("#depositAmountTextField");
        write("1000");
        clickOn("#confirmDepositButton");
        FxAssert.verifyThat("#depositConfirmationText", NodeMatchers.isVisible());
    }

    @Test
    public void checkWithdrawPane(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        clickOn("#withdrawButton");
        FxAssert.verifyThat("#accNumberWithdraw", NodeMatchers.isVisible());
        FxAssert.verifyThat("#accNumberWithdraw", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#accLsWithdraw", ListViewMatchers.hasItems(2));
        FxAssert.verifyThat("#accLsWithdraw", NodeMatchers.isNotNull());
        clickOn("#withdrawAmountTextField");
        write("1000");
        clickOn("#confirmWithdrawButton");
        FxAssert.verifyThat("#withdrawConfirmationText", NodeMatchers.isVisible());

    }
    @Test
    public void checkTransferPane(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#transferButton");
        FxAssert.verifyThat("#accLsTransfer", ListViewMatchers.hasItems(2));
        FxAssert.verifyThat("#accLsTransfer", NodeMatchers.isVisible());
        FxAssert.verifyThat("#accLsTransfer", NodeMatchers.isNotNull());
        clickOn("#transferAmountTextField");
        write("100");
        clickOn("#recieverTextField");
        write("5044891741");
        clickOn("#transferMemoField");
        write("Transfer Test");
        FxAssert.verifyThat("#transferConfirmationText", NodeMatchers.isVisible());
    }
    @Test
    public void checkSettingsPane(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        clickOn("#settingsButton");
        FxAssert.verifyThat("#accLsSetting", ListViewMatchers.hasItems(2));
        FxAssert.verifyThat("#accLsSetting", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#accLsSetting", NodeMatchers.isVisible());
        FxAssert.verifyThat("#accNumberSetting",NodeMatchers.isVisible());
        FxAssert.verifyThat("#accNumberSetting", NodeMatchers.isNotNull());
        clickOn("#settingsCombo");
    }

    @Test
    public void checkLogout(){
        clickOn("#userID");
        write("8133389705");
        clickOn("#loginPIN");
        write("6969");
        clickOn("#loginButton");
        clickOn("#logout");
    }
}