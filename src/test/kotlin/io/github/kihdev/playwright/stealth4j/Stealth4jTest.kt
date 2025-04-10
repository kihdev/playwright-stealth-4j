package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Stealth4jTest {

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
    fun plain() {
        val page = AntibotPage("plain", context.newPage())
        page.screenshot()
        assertFalse(page.info.hasChrome)
        assertTrue(page.info.detailChrome is DetailChrome.Unknown)
        assertFalse(page.iframeContentWindow())
        assertTrue(page.info.audioCodecs.values.contains(""))
        assertNotEquals(4, page.navigatorHardwareConcurrency())
        assertEquals(listOf("en-US"), page.info.languages)
        assertEquals("denied", page.info.permissions["permission"])
        assertTrue(page.info.plugins.isEmpty())
        assertTrue(page.navigatorWebdriver() == true)
        assertNotEquals(listOf("Intel Inc.", "Intel Iris OpenGL Engine"), page.info.videoCard)
    }

    @Test
    fun stealth() {
        val stealthPage = AntibotPage("stealth", context.newPage().stealth())
        stealthPage.screenshot()
        assertTrue(stealthPage.info.hasChrome)
        assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                (stealthPage.info.detailChrome as DetailChrome.Details).value["csi"] == "function Function() { [native code] }")
        assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                (stealthPage.info.detailChrome as DetailChrome.Details).value["loadTimes"] == "function Function() { [native code] }")
        assertTrue(stealthPage.info.detailChrome is DetailChrome.Details &&
                (stealthPage.info.detailChrome as DetailChrome.Details).value["runtime"] == "function Object() { [native code] }")
        assertTrue(stealthPage.iframeContentWindow())
        assertFalse(stealthPage.info.audioCodecs.values.contains(""))
        assertEquals(4, stealthPage.navigatorHardwareConcurrency())
        assertEquals(listOf("en-US", "en"), stealthPage.info.languages)
        assertEquals("default", stealthPage.info.permissions["permission"])
        assertTrue(stealthPage.info.plugins.isNotEmpty())
        assertNull(stealthPage.navigatorWebdriver())
        assertEquals(listOf("Intel Inc.", "Intel Iris OpenGL Engine"), stealthPage.info.videoCard)
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

    @Test
    fun navigatorLanguages() {
        val stealthPage = AntibotPage("navigator.languages", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorLanguages(true)
                .build()
        ))
        stealthPage.screenshot()
        assertEquals(listOf("en-US", "en"), stealthPage.info.languages)
    }

    @Test
    fun navigatorPermissions() {
        val stealthPage = AntibotPage("navigator.permissions", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorPermissions(true)
                .build()
        ))
        stealthPage.screenshot()
        assertEquals("default", stealthPage.info.permissions["permission"])
    }

    @Test
    fun navigatorPlugins() {
        val stealthPage = AntibotPage("navigator.plugins", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorPlugins(true)
                .build()
        ))
        stealthPage.screenshot()
        assertTrue(stealthPage.info.plugins.isNotEmpty())
    }

    @Test
    fun navigatorVendor() {
        val stealthPage = AntibotPage("navigator.vendor", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorVendor(true, "ACME Inc.")
                .build()
        ))
        stealthPage.screenshot()
        assertEquals("ACME Inc.", stealthPage.navigatorVendor())
    }

    @Test
    fun navigatorWebdriver() {
        val stealthPage = AntibotPage("navigator.webdriver", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .navigatorWebDriver(true)
                .build()
        ))
        stealthPage.screenshot()
        assertNull(stealthPage.navigatorWebdriver())
    }

    @Test
    fun webglVendor() {
        val stealthPage = AntibotPage("webgl.vendor", context.newPage().stealth(
            Stealth4jConfig.builder()
                .disableAll()
                .webglVendor(true, "ACME Inc.")
                .build()
        ))
        stealthPage.screenshot()
        assertEquals("ACME Inc.", stealthPage.info.videoCard[0])
    }
}