package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page

/**
 * Utility object for applying stealth modifications to Playwright Browser, BrowserContext, and Page.
 */
object Stealth4j {

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
}
