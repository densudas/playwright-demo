package io.github.densudas;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SauceDemoTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void successfulLogin() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("[data-test='username']").fill("standard_user");
        page.locator("[data-test='password']").fill("secret_sauce");
        page.locator("[data-test='login-button']").click();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

    @Test
    void unsuccessfulLogin() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("[data-test='username']").fill("locked_out_user");
        page.locator("[data-test='password']").fill("secret_sauce");
        page.locator("[data-test='login-button']").click();
        assertThat(page.locator("[data-test='error']")).isVisible();
    }

    @Test
    void addItemToCart() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("[data-test='username']").fill("standard_user");
        page.locator("[data-test='password']").fill("secret_sauce");
        page.locator("[data-test='login-button']").click();
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        assertThat(page.locator(".shopping_cart_badge")).hasText("1");
    }
}
