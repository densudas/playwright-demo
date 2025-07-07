package io.github.densudas;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base test class that handles browser initialization and cleanup.
 */
class BaseTest {

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void setUpAll() {
        BrowserFactory.initialize();
    }

    @AfterAll
    static void tearDownAll() {
        BrowserFactory.close();
    }

    @BeforeEach
    void setUp() {
        page = BrowserFactory.createPage();
        context = BrowserFactory.getBrowserContext();
    }

    @AfterEach
    void tearDown() {
        BrowserFactory.closeContext();
    }
}
