package io.github.densudas;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Factory class for creating and managing browser instances.
 */
public class BrowserFactory {
    private static Playwright playwright;
    private static Browser browser;
    private static ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();
    private static ThreadLocal<Page> page = new ThreadLocal<>();

    private BrowserFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize the Playwright and Browser instances.
     */
    public static void initialize() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        if (browser == null) {
            browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
            );
        }
    }

    /**
     * Create a new browser context and page.
     * @return The created page
     */
    public static Page createPage() {
        if (playwright == null || browser == null) {
            initialize();
        }

        BrowserContext context = browser.newContext();
        browserContext.set(context);

        Page newPage = context.newPage();
        page.set(newPage);

        return newPage;
    }

    /**
     * Get the current browser context.
     * @return The current browser context
     */
    public static BrowserContext getBrowserContext() {
        return browserContext.get();
    }

    /**
     * Get the current page.
     * @return The current page
     */
    public static Page getPage() {
        return page.get();
    }

    /**
     * Get the browser instance.
     * @return The browser instance
     */
    public static Browser getBrowser() {
        if (browser == null) {
            initialize();
        }
        return browser;
    }

    /**
     * Close the current browser context and page.
     */
    public static void closeContext() {
        if (browserContext.get() != null) {
            browserContext.get().close();
            browserContext.remove();
        }
        if (page.get() != null) {
            page.remove();
        }
    }

    /**
     * Close the Playwright instance and browser.
     */
    public static void close() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
