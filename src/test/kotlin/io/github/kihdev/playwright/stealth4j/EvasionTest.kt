package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EvasionTest {

    @Test
    fun chromeApp() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext()
                val page = AntibotPage("chrome.app", context.newPage())
                page.screenshot()
                assertFalse(page.info.hasChrome)

                val stealthPage = AntibotPage("chrome.app-stealth", context.newPage().stealth(
                    Stealth4jConfig.builder()
                        .disableAll()
                        .chromeApp(true)
                        .build()
                ))
                stealthPage.screenshot()
                assertTrue(stealthPage.info.hasChrome)
            }
        }
    }
}