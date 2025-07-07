# Playwright Demo

This repository demonstrates the usage of [Playwright](https://playwright.dev/) for browser automation and testing in Java. It showcases various features of the Playwright framework through practical examples.

## Features Demonstrated

### Core Browser Automation
- **Browser Contexts**: Creating and managing isolated browser contexts ([PlaywrightFeaturesTest.java:24](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L24))
- **Page Navigation**: Navigating to URLs and waiting for page load ([SauceDemoTest.java:11](src/test/java/io/github/densudas/SauceDemoTest.java#L11))
- **Browser Management**: Proper initialization and cleanup of browser resources ([BrowserFactory.java](src/test/java/io/github/densudas/BrowserFactory.java))

### Element Selection and Interaction
- **Locators**: Various ways to locate elements on a page ([PlaywrightFeaturesTest.java:33](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L33))
- **Keyboard Input**: Simulating keyboard events ([PlaywrightFeaturesTest.java:113](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L113))
- **Mouse Interactions**: Simulating mouse events including drag and drop ([PlaywrightFeaturesTest.java:129](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L129))
- **File Uploads**: Handling file upload dialogs ([PlaywrightFeaturesTest.java:166](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L166))

### Network and Resource Handling
- **Network Interception**: Intercepting and modifying network requests ([PlaywrightFeaturesTest.java:43](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L43))
- **File Downloads**: Handling file downloads ([PlaywrightFeaturesTest.java:92](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L92))

### Advanced Browser Features
- **JavaScript Execution**: Evaluating JavaScript in the browser context ([PlaywrightFeaturesTest.java:150](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L150))
- **Dialog Handling**: Managing JavaScript dialogs (alerts, confirms, prompts) ([PlaywrightFeaturesTest.java:103](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L103))
- **Web Storage**: Interacting with localStorage and sessionStorage ([PlaywrightFeaturesTest.java:201](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L201))
- **Frames and iFrames**: Working with frames and iframes ([PlaywrightFeaturesTest.java:183](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L183))

### Testing and Assertions
- **Assertions**: Using Playwright's built-in assertions ([SauceDemoTest.java:15](src/test/java/io/github/densudas/SauceDemoTest.java#L15))
- **Waiting Mechanisms**: Different ways to wait for conditions on the page ([PlaywrightFeaturesTest.java:231](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L231))
- **Accessibility Testing**: Testing accessibility using ARIA roles ([PlaywrightFeaturesTest.java:220](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L220))

### Device and Environment Simulation
- **Device Emulation**: Emulating mobile devices ([PlaywrightFeaturesTest.java:70](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L70))
- **Geolocation**: Setting and testing geolocation ([PlaywrightFeaturesTest.java:84](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L84))

### Debugging and Tracing
- **Screenshots and Traces**: Taking screenshots and recording traces for debugging ([PlaywrightFeaturesTest.java:53](src/test/java/io/github/densudas/PlaywrightFeaturesTest.java#L53))

## Project Structure
- **BaseTest.java**: Base test class that handles browser initialization and cleanup
- **BrowserFactory.java**: Factory class for creating and managing browser instances
- **PlaywrightFeaturesTest.java**: Demonstrates various Playwright features
- **SauceDemoTest.java**: Practical examples using the SauceDemo website

## Getting Started
1. Clone this repository
2. Run `./gradlew installPlaywrightBrowsers` to install the required browsers
3. Run `./gradlew test` to execute the tests

## Additional Resources
- [Playwright Documentation](https://playwright.dev/java/docs/intro)
- [Playwright API Reference](https://playwright.dev/java/docs/api/class-playwright)
