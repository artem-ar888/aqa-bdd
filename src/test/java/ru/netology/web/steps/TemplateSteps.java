package ru.netology.web.steps;

import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import org.junit.jupiter.api.Assertions;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.MoneyTransferPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;

public class TemplateSteps {
    private static LoginPage loginPage;
    private static VerificationPage verificationPage;
    private static DashboardPage dashboardPage;
    private static MoneyTransferPage moneyTransferPage;

    @Пусть("пользователь залогинен с именем {string} и паролем {string}")
    public void loginAndVerification(String login, String password) {
        loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Когда("пользователь переводит {string} рублей с карты с номером {string} на свою {int} карту с главной страницы")
    public void moneyTransfer(String amount, String cardFrom, int index) {
        moneyTransferPage = dashboardPage.selectCardTransferByIndex(index);
        dashboardPage = moneyTransferPage.makeTransferFromCard(amount, cardFrom);
    }

    @Тогда("баланс его {int} карты из списка на главной странице должен стать {string} рублей")
    public void checkBalance(int index, String balance) {
        int expectedCardBalance = 0;
        try {
            expectedCardBalance = Integer.parseInt(balance.replace(" ", ""));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        dashboardPage.reloadDashboardPage();
        int actualCardBalance = dashboardPage.getCardBalanceByIndex(index);
        Assertions.assertEquals(expectedCardBalance, actualCardBalance);
    }
}
