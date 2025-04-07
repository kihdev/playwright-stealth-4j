package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EvasionTest {

    companion object {
        private val playwright = Playwright.create()
        private val browser = playwright.chromium().launch()
        val context: BrowserContext = browser.newContext()

        @JvmStatic
        @BeforeAll
        fun setup() {
            context.onConsoleMessage { msg -> println(msg.text()) }
            context.onWebError { error -> println("Web error: ${error.error()}") }
        }

        @JvmStatic
        @AfterAll
        fun shutdown() {
            context.close()
            browser.close()
            playwright.close()
        }
    }

    @Test
    fun noEvasions() {
        val page = AntibotPage("no-evasions", context.newPage())
        page.screenshot()
        assertFalse(page.info.hasChrome)
        assertTrue(page.info.detailChrome is DetailChrome.Unknown)
        assertFalse(page.iframeContentWindow())
        assertTrue(page.info.audioCodecs.values.contains(""))
        assertNotEquals(4, page.navigatorHardwareConcurrency())
    }

    @Test
    fun chromeApp() {
        val stealthPage = AntibotPage("chrome.app", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .chromeApp(true)
                .build()
        ))
        stealthPage.screenshot()
        assertTrue(stealthPage.info.hasChrome)
    }

    @Test
    fun chromeCsi() {
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

    @Test
    fun chromeLoadTimes() {
        val stealthPage = AntibotPage("chrome.loadTimes", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .chromeLoadTimes(true)
                .build()
        ))
        stealthPage.screenshot()
        assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                (stealthPage.info.detailChrome as DetailChrome.Details).value["loadTimes"] == "function Function() { [native code] }")
    }

    @Test
    fun chromeRuntime() {
        val stealthPage = AntibotPage("chrome.runtime", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .chromeRuntime(true)
                .build()
        ))
        stealthPage.screenshot()
        assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                (stealthPage.info.detailChrome as DetailChrome.Details).value["runtime"] == "function Object() { [native code] }")
    }

    @Test
    fun iframeContentWindow() {
        val stealthPage = AntibotPage("iframe.contentWindow", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .iframeContentWindow(true)
                .build()
        ))
        stealthPage.screenshot()
        assertTrue(stealthPage.iframeContentWindow())
    }

    @Test
    fun mediaCodecs() {
        val stealthPage = AntibotPage("media.codecs", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .mediaCodecs(true)
                .build()
        ))
        stealthPage.screenshot()
        assertFalse(stealthPage.info.audioCodecs.values.contains(""))
    }

    @Test
    fun navigatorHardwareConcurrency() {
        val stealthPage = AntibotPage("navigator.hardwareConcurrency", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorHardwareConcurrency(true)
                .build()
        ))
        stealthPage.screenshot()
        assertEquals(4, stealthPage.navigatorHardwareConcurrency())
    }
}