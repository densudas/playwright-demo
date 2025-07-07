package io.github.densudas;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.Geolocation;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class PlaywrightFeaturesTest extends BaseTest {

    @Test
    void browserContexts() {
        try (BrowserContext incognitoContext = BrowserFactory.getBrowser().newContext()) {
            Page incognitoPage = incognitoContext.newPage();
            incognitoPage.navigate("https://www.saucedemo.com/");
            assertThat(incognitoPage).hasTitle("Swag Labs");
        }
    }

    @Test
    void locators() {
        page.navigate("https://www.saucedemo.com/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("#user-name").fill("standard_user");
        page.locator("[data-test='password']").fill("secret_sauce");
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
        try (BrowserContext context = BrowserFactory.getBrowser().newContext(new Browser.NewContextOptions()
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
        assertTrue(download.path().toFile().exists());
        assertTrue(download.path().toFile().length() > 0);
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

    @Test
    void keyboardInput() {
        page.navigate("https://the-internet.herokuapp.com/key_presses");
        Keyboard keyboard = page.keyboard();

        keyboard.press("A");
        assertThat(page.locator("#result")).hasText("You entered: A");

        keyboard.press("Tab");
        assertThat(page.locator("#result")).hasText("You entered: TAB");

        page.locator("#target").click();
        keyboard.type("Hello Playwright");
        assertEquals("Hello Playwright", page.evaluate("() => document.activeElement.value"));
    }

    @Test
    void mouseInteractions() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        ElementHandle columnA = page.querySelector("#column-a");
        ElementHandle columnB = page.querySelector("#column-b");

        Mouse mouse = page.mouse();

        BoundingBox boxA = columnA.boundingBox();
        BoundingBox boxB = columnB.boundingBox();

        mouse.move(boxA.x + boxA.width / 2, boxA.y + boxA.height / 2);
        mouse.down();
        mouse.move(boxB.x + boxB.width / 2, boxB.y + boxB.height / 2);
        mouse.up();

        assertThat(page.locator("#column-a")).hasText("B");
        assertThat(page.locator("#column-b")).hasText("A");
    }

    @Test
    void evaluateJavaScript() {
        page.navigate("https://www.saucedemo.com/");

        Object title = page.evaluate("() => document.title");
        assertEquals("Swag Labs", title);

        Object result = page.evaluate("([a, b]) => a + b", Arrays.asList(2, 3));
        assertEquals(5, result);

        page.evaluate("() => document.body.style.backgroundColor = 'red'");

        int height = (int) page.evaluate("() => document.body.clientHeight");
        assertTrue(height > 0);
    }

    @Test
    void fileUploads() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        Path filePath = Paths.get("some-file.txt");

        FileChooser fileChooser = page.waitForFileChooser(() -> {
            page.locator("#file-upload").click();
        });

        fileChooser.setFiles(filePath);

        page.locator("#file-submit").click();

        assertThat(page.locator("#uploaded-files")).containsText("some-file.txt");
    }

    @Test
    void framesAndIframes() {
        page.navigate("https://the-internet.herokuapp.com/nested_frames");

        Frame topFrame = page.frame("frame-top");

        Frame leftFrame = topFrame.childFrames().get(0);
        Frame middleFrame = topFrame.childFrames().get(1);
        Frame rightFrame = topFrame.childFrames().get(2);

        Frame bottomFrame = page.frame("frame-bottom");

        assertEquals("LEFT", leftFrame.locator("body").textContent().trim());
        assertEquals("MIDDLE", middleFrame.locator("body").textContent().trim());
        assertEquals("RIGHT", rightFrame.locator("body").textContent().trim());
        assertEquals("BOTTOM", bottomFrame.locator("body").textContent().trim());
    }

    @Test
    void webStorage() {
        page.navigate("https://www.saucedemo.com/");

        page.evaluate("() => localStorage.setItem('testKey', 'testValue')");

        Object value = page.evaluate("() => localStorage.getItem('testKey')");
        assertEquals("testValue", value);

        page.evaluate("() => sessionStorage.setItem('sessionKey', 'sessionValue')");

        Object sessionValue = page.evaluate("() => sessionStorage.getItem('sessionKey')");
        assertEquals("sessionValue", sessionValue);

        page.evaluate("() => localStorage.clear()");
        Object clearedValue = page.evaluate("() => localStorage.getItem('testKey')");
        assertNull(clearedValue);
    }

    @Test
    void accessibilityTesting() {
        page.navigate("https://www.saucedemo.com/");

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("standard_user");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("secret_sauce");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

    @Test
    void waitForConditions() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Click the start button
        page.locator("button").click();

        // Wait for the loading indicator to disappear and the text to appear
        page.waitForSelector("#loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForSelector("#finish h4", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Assert the text is correct
        assertThat(page.locator("#finish h4")).hasText("Hello World!");
    }
}
