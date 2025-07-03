package io.github.densudas;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Geolocation;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaywrightFeaturesTest {

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
    void browserContexts() {
        // Create a new incognito browser context
        try (BrowserContext incognitoContext = browser.newContext()) {
            // Create a new page in the incognito context
            Page incognitoPage = incognitoContext.newPage();
            incognitoPage.navigate("https://www.saucedemo.com/");
            assertThat(incognitoPage).hasTitle("Swag Labs");
        }
    }

    @Test
    void locators() {
        page.navigate("https://www.saucedemo.com/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        // Using CSS selectors
        page.locator("#user-name").fill("standard_user");
        // Using data-test attributes
        page.locator("[data-test='password']").fill("secret_sauce");
        // Using text content
        page.locator("text=LOGIN").click();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

    @Test
    void networkInterception() {
        page.route("**/*.{png,jpg,jpeg}", Route::abort);
        page.navigate("https://www.saucedemo.com/");
        page.locator("[data-test='username']").fill("standard_user");
        page.locator("[data-test='password']").fill("secret_sauce");
        page.locator("[data-test='login-button']").click();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

    @Test
    void screenshotsAndVideos() {
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page.navigate("https://www.saucedemo.com/");
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot.png")));
        page.locator("[data-test='username']").fill("standard_user");
        page.locator("[data-test='password']").fill("secret_sauce");
        page.locator("[data-test='login-button']").click();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");

        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));
    }

    @Test
    void deviceEmulation() {
        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .setViewportSize(375, 667)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true))) {
            Page page = context.newPage();
            page.navigate("https://www.saucedemo.com/");
            assertThat(page).hasTitle("Swag Labs");
        }
    }

    @Test
    void geolocation() {
        context.grantPermissions(Collections.singletonList("geolocation"));
        context.setGeolocation(new Geolocation(35.6895f, 139.6917f));
        page.navigate("https://www.google.com/maps/@35.6895,139.6917,15z");
        assertThat(page).hasURL(Pattern.compile(".*@35.6895,139.6917.*", Pattern.CASE_INSENSITIVE));
    }

    @Test
    void fileDownloads() {
        page.navigate("https://the-internet.herokuapp.com/download");
        Download download = page.waitForDownload(() -> {
            page.locator("text=some-file.txt").click();
        });
        download.saveAs(Paths.get("some-file.txt"));
    }

    @Test
    void dialogs() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");
        page.onceDialog(dialog -> {
            assertEquals("I am a JS Alert", dialog.message());
            dialog.accept();
        });
        page.locator("button[onclick='jsAlert()']").click();
    }
}

