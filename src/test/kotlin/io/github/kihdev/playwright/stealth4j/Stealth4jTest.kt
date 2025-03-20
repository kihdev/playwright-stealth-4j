package io.github.kihdev.playwright.stealth4j

import io.github.kihdev.playwright.stealth4j.Stealth4j.stealth
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.test.assertTrue

class Stealth4jTest {

    @Test
    fun plain() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext()
                val page = context.newPage()
                page.onConsoleMessage { msg -> println(msg.text()) }
                page.navigate("https://bot.sannysoft.com/")

                val webDriverFlag = page.evaluate("""() => { return window.navigator.webdriver }""")
                assertTrue(webDriverFlag is Boolean)
                assert(webDriverFlag == true)

                page.screenshot(
                    Page.ScreenshotOptions()
                        .setPath(Paths.get("build/tmp/screenshots/plain.png"))
                        .setFullPage(true)
                )
            }
        }
    }

    @Test
    fun stealth() {
        Playwright.create().use { playwright ->
            val chromium = playwright.chromium()
            chromium.launch().use { browser ->
                val context = browser.newContext().stealth()
                val page = context.newPage()
                page.onConsoleMessage { msg -> println(msg.text()) }
                page.navigate("https://bot.sannysoft.com/")

                val webDriverFlag = page.evaluate("""() => { return window.navigator.webdriver }""")
                assertTrue(webDriverFlag is Boolean)
                assert(webDriverFlag == false)

                page.screenshot(
                    Page.ScreenshotOptions()
                        .setPath(Paths.get("build/tmp/screenshots/stealth.png"))
                        .setFullPage(true)
                )
            }
        }
    }
}