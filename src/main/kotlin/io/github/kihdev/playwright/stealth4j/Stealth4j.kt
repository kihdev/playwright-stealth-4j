package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page

/**
 * Utility class for applying stealth modifications to Playwright Browser, BrowserContext, and Page.
 */
class Stealth4j {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun newStealthContext(browser: Browser, config: Stealth4jConfig = Stealth4jConfig.DEFAULT): BrowserContext {
            return browser.newStealthContext(config)
        }

        @JvmStatic
        @JvmOverloads
        fun newStealthPage(context: BrowserContext, config: Stealth4jConfig = Stealth4jConfig.DEFAULT): Page {
            return context.newStealthPage(config)
        }

    }

    // Ensure that Playwright is available
    init {
        try {
            Class.forName("com.microsoft.playwright.Playwright")
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("Playwright is not found. Please add 'com.microsoft.playwright:playwright' as a dependency in your project.")
        }
    }
}

/**
 * Creates a new stealth BrowserContext with the specified configuration.
 *
 * @param config The stealth configuration to apply. Defaults to [Stealth4jConfig.DEFAULT].
 * @return A new stealth BrowserContext.
 */
fun Browser.newStealthContext(config: Stealth4jConfig = Stealth4jConfig.DEFAULT): BrowserContext {
    val context = this.newContext()
    context.stealth(config) // Apply stealth modifications
    return context
}

/**
 * Applies stealth modifications to the BrowserContext.
 *
 * @param config The stealth configuration to apply. Defaults to [Stealth4jConfig.DEFAULT].
 * @return The modified BrowserContext.
 */
fun BrowserContext.stealth(config: Stealth4jConfig = Stealth4jConfig.DEFAULT): BrowserContext {
    this.onPage { page -> page.stealth(config) }
    return this
}

/**
 * Creates a new stealth Page with the specified configuration.
 *
 * @param config The stealth configuration to apply. Defaults to [Stealth4jConfig.DEFAULT].
 * @return A new stealth Page.
 */
fun BrowserContext.newStealthPage(config: Stealth4jConfig = Stealth4jConfig.DEFAULT): Page {
    return this.newPage().stealth(config)
}

/**
 * Applies stealth modifications to the Page.
 *
 * @param config The stealth configuration to apply. Defaults to [Stealth4jConfig.DEFAULT].
 * @return The modified Page.
 */
fun Page.stealth(config: Stealth4jConfig = Stealth4jConfig.DEFAULT): Page {
    val scripts = config.enabledScripts.map { script -> script.content }
    // the order of evaluation of multiple scripts is not guaranteed so we need to add them all at once
    val initScript = scripts.joinToString("\n")
    this.addInitScript(initScript)
    return this
}
