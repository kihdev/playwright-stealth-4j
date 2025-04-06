package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EvasionTest {

    @Test
    fun noEvasions() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext()
                val page = AntibotPage("no-evasions", context.newPage())
                page.screenshot()
                println(page.info)
                assertFalse(page.info.hasChrome)
                assertTrue(page.info.detailChrome is DetailChrome.Unknown)
            }
        }
    }

    @Test
    fun chromeApp() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext()
                val stealthPage = AntibotPage("chrome.app", context.newPage().stealth(
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

    @Test
    fun chromeCsi() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext()
                val stealthPage = AntibotPage("chrome.csi", context.newPage().stealth(
                    Stealth4jConfig.builder()
                        .disableAll()
                        .chromeCsi(true)
                        .build()
                ))
                stealthPage.screenshot()
                assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                        (stealthPage.info.detailChrome as DetailChrome.Details).value["csi"] == "function Function() { [native code] }")
            }
        }
    }
}