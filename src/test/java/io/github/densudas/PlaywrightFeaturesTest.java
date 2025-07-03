package io.github.densudas;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Geolocation;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    @Test
    void keyboardInput() {
        page.navigate("https://the-internet.herokuapp.com/key_presses");
        Keyboard keyboard = page.keyboard();

        // Press a key and check the result
        keyboard.press("A");
        assertThat(page.locator("#result")).hasText("You entered: A");

        // Press a special key
        keyboard.press("Tab");
        assertThat(page.locator("#result")).hasText("You entered: TAB");

        // Type a sequence of characters
        page.locator("#target").click();
        keyboard.type("Hello Playwright");
        assertEquals("Hello Playwright", page.evaluate("() => document.activeElement.value"));
    }

    @Test
    void mouseInteractions() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        // Get the bounding box of the elements
        ElementHandle columnA = page.querySelector("#column-a");
        ElementHandle columnB = page.querySelector("#column-b");

        // Perform drag and drop using mouse actions
        Mouse mouse = page.mouse();

        // Get the center points of the elements
        BoundingBox boxA = columnA.boundingBox();
        BoundingBox boxB = columnB.boundingBox();

        // Perform the drag and drop
        mouse.move(boxA.x + boxA.width / 2, boxA.y + boxA.height / 2);
        mouse.down();
        mouse.move(boxB.x + boxB.width / 2, boxB.y + boxB.height / 2);
        mouse.up();

        // Verify the drag and drop worked by checking the text of the elements
        // Note: This specific site's drag and drop might not work with Playwright's mouse actions
        // This is just an example of the API usage
    }

    @Test
    void evaluateJavaScript() {
        page.navigate("https://www.saucedemo.com/");

        // Execute JavaScript in the page context
        Object title = page.evaluate("() => document.title");
        assertEquals("Swag Labs", title);

        // Pass arguments to the JavaScript function
        Object result = page.evaluate("([a, b]) => a + b", Arrays.asList(2, 3));
        assertEquals(5, result);

        // Modify the page using JavaScript
        page.evaluate("() => document.body.style.backgroundColor = 'red'");

        // Get information about the page
        int height = (int) page.evaluate("() => document.body.clientHeight");
        assertTrue(height > 0);
    }

    @Test
    void fileUploads() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        // Create a temporary file to upload
        Path filePath = Paths.get("some-file.txt");

        // Set up the file chooser
        FileChooser fileChooser = page.waitForFileChooser(() -> {
            page.locator("#file-upload").click();
        });

        // Select the file
        fileChooser.setFiles(filePath);

        // Submit the form
        page.locator("#file-submit").click();

        // Verify the upload was successful
        assertThat(page.locator("#uploaded-files")).containsText("some-file.txt");
    }

    @Test
    void framesAndIframes() {
        page.navigate("https://the-internet.herokuapp.com/nested_frames");

        // Get the top frame
        Frame topFrame = page.frame("frame-top");

        // Get the left frame inside the top frame
        Frame leftFrame = topFrame.childFrames().get(0);

        // Get the middle frame inside the top frame
        Frame middleFrame = topFrame.childFrames().get(1);

        // Get the right frame inside the top frame
        Frame rightFrame = topFrame.childFrames().get(2);

        // Get the bottom frame
        Frame bottomFrame = page.frame("frame-bottom");

        // Verify the content of each frame
        assertEquals("LEFT", leftFrame.locator("body").textContent().trim());
        assertEquals("MIDDLE", middleFrame.locator("body").textContent().trim());
        assertEquals("RIGHT", rightFrame.locator("body").textContent().trim());
        assertEquals("BOTTOM", bottomFrame.locator("body").textContent().trim());
    }

    @Test
    void webStorage() {
        page.navigate("https://www.saucedemo.com/");

        // Set localStorage item
        page.evaluate("() => localStorage.setItem('testKey', 'testValue')");

        // Get localStorage item
        Object value = page.evaluate("() => localStorage.getItem('testKey')");
        assertEquals("testValue", value);

        // Set sessionStorage item
        page.evaluate("() => sessionStorage.setItem('sessionKey', 'sessionValue')");

        // Get sessionStorage item
        Object sessionValue = page.evaluate("() => sessionStorage.getItem('sessionKey')");
        assertEquals("sessionValue", sessionValue);

        // Clear storage
        page.evaluate("() => localStorage.clear()");
        Object clearedValue = page.evaluate("() => localStorage.getItem('testKey')");
        assertEquals(null, clearedValue);
    }

    @Test
    void accessibilityTesting() {
        page.navigate("https://www.saucedemo.com/");

        // Use role-based selectors for accessibility testing
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
