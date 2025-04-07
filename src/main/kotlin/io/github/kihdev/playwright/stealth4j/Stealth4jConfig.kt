package io.github.kihdev.playwright.stealth4j

import java.net.URL

/**
 * Configuration class for Stealth4j, which holds the settings for various evasions.
 *
 * @property evasions List of evasions to be applied.
 */
class Stealth4jConfig private constructor(
    private val evasions: List<Evasion>
){
    /**
     * List of enabled scripts based on the configured evasions.
     */
    val enabledScripts: List<Script> by lazy { enabledScripts() }

    companion object {
        /**
         * Default configuration for Stealth4jConfig.
         */
        val DEFAULT = builder().build()

        /**
         * Creates a new Builder instance for configuring Stealth4jConfig.
         *
         * @return A new Builder instance.
         */
        @JvmStatic
        fun builder() = Builder()

        @Suppress("UNCHECKED_CAST")
        fun mergeMaps(a: Map<String, Any?>, b: Map<String, Any?>): Map<String, Any?> {
            return (a.keys + b.keys).associateWith { key ->
                val aVal = a[key]
                val bVal = b[key]
                when {
                    aVal is Map<*, *> && bVal is Map<*, *> ->
                        mergeMaps(aVal as Map<String, Any?>, bVal as Map<String, Any?>)
                    bVal != null -> bVal
                    else -> aVal
                }
            }
        }

        fun mapToJson(map: Map<String, Any?>): String {
            @Suppress("UNCHECKED_CAST")
            fun valueToJson(value: Any?): String = when (value) {
                is String -> "\"$value\""
                is Number, is Boolean -> value.toString()
                is Map<*, *> -> mapToJson(value as Map<String, Any?>) // Recursive call for nested maps
                is List<*> -> value.joinToString(prefix = "[", postfix = "]") { valueToJson(it) }
                else -> throw IllegalArgumentException("Unsupported type: ${value?.javaClass}")
            }

            return map.entries.filter { it.value != null } // Remove null values
                .joinToString(prefix = "{", postfix = "}") { "\"${it.key}\": ${valueToJson(it.value)}" }
        }
    }

    /**
     * Builder class for constructing a Stealth4jConfig instance.
     */
    class Builder {
        private var chromeApp: Boolean = true
        private var chromeCsi: Boolean = true
        private var chromeLoadTimes: Boolean = true

        private var chromeRuntime: Boolean = true
        private var chromeRuntimeRunOnInsecureOrigins: Boolean = true

        private var iframeContentWindow: Boolean = true
        private var mediaCodecs: Boolean = true

        private var navigatorHardwareConcurrency: Boolean = true
        private var navigatorHardwareConcurrencyNumber: Int = 4

        private var navigatorLanguages: Boolean = true
        private var navigatorLanguagesList: List<String> = listOf("en-US", "en")

        private var navigatorPermissions: Boolean = true
        private var navigatorPlugins: Boolean = true

        private var navigatorUserAgent: Boolean = true
        private var navigatorUserAgentString: String? = null

        private var navigatorVendor: Boolean = true
        private var navigatorVendorString: String = "Google Inc."

        private var navigatorWebDriver: Boolean = true

        private var webglVendor : Boolean = true
        private var webglVendorString: String = "Intel Inc."
        private var webglRenderer: String = "Intel Iris OpenGL Engine"

        private var windowOuterDimensions : Boolean = true

        /**
         * Disables all evasions.
         *
         * @return The Builder instance.
         */
        fun disableAll() = apply {
            chromeApp = false
            chromeCsi = false
            chromeLoadTimes = false
            chromeRuntime = false
            iframeContentWindow = false
            mediaCodecs = false
            navigatorHardwareConcurrency = false
            navigatorLanguages = false
            navigatorPermissions = false
            navigatorPlugins = false
            navigatorUserAgent = false
            navigatorVendor = false
            navigatorWebDriver = false
            webglVendor = false
            windowOuterDimensions = false
        }

        fun chromeApp(enabled: Boolean) = apply { this.chromeApp = enabled }
        fun chromeCsi(enabled: Boolean) = apply { this.chromeCsi = enabled }
        fun chromeLoadTimes(enabled: Boolean) = apply { this.chromeLoadTimes = enabled }
        fun chromeRuntime(enabled: Boolean, runOnInsecureOrigins: Boolean = true) = apply {
            this.chromeRuntime = enabled
            this.chromeRuntimeRunOnInsecureOrigins = runOnInsecureOrigins
        }
        fun iframeContentWindow(enabled: Boolean) = apply { this.iframeContentWindow = enabled }
        fun mediaCodecs(enabled: Boolean) = apply { this.mediaCodecs = enabled }
        fun navigatorHardwareConcurrency(enabled: Boolean, concurrency: Int = 4) = apply {
            this.navigatorHardwareConcurrency = enabled
            this.navigatorHardwareConcurrencyNumber = concurrency
        }
        fun navigatorLanguages(enabled: Boolean, languages: List<String> = listOf("en-US", "en")) = apply {
            this.navigatorLanguages = enabled
            this.navigatorLanguagesList = languages
        }
        fun navigatorPermissions(enabled: Boolean) = apply { this.navigatorPermissions = enabled }
        fun navigatorPlugins(enabled: Boolean) = apply { this.navigatorPlugins = enabled }
        fun navigatorUserAgent(enabled: Boolean, userAgent: String? = null) = apply {
            this.navigatorUserAgent = enabled
            this.navigatorUserAgentString = userAgent
        }
        fun navigatorVendor(enabled: Boolean, vendor: String = "Google Inc.") = apply {
            this.navigatorVendor = enabled
            this.navigatorVendorString = vendor
        }
        fun navigatorWebDriver(enabled: Boolean) = apply { this.navigatorWebDriver = enabled }
        fun webglVendor(enabled: Boolean, vendor: String = "Intel Inc.", renderer: String = "Intel Iris OpenGL Engine") = apply {
            this.webglVendor = enabled
            this.webglVendorString = vendor
            this.webglRenderer = renderer
        }
        fun windowOuterDimensions(enabled: Boolean) = apply { this.windowOuterDimensions = enabled }

        /**
         * Builds the Stealth4jConfig instance with the configured settings.
         *
         * @return The Stealth4jConfig instance.
         */
        fun build(): Stealth4jConfig {
            val configuredEvasions = mutableListOf<Evasion>()
            if (chromeApp) {
                configuredEvasions += Evasion(JsScript.CHROME_APP)
            }
            if (chromeCsi) {
                configuredEvasions += Evasion(JsScript.CHROME_CSI)
            }
            if (chromeLoadTimes) {
                configuredEvasions += Evasion(JsScript.CHROME_LOAD_TIMES)
            }
            if (chromeRuntime) {
                configuredEvasions += Evasion(
                    JsScript.CHROME_RUNTIME,
                    mapOf("runOnInsecureOrigins" to chromeRuntimeRunOnInsecureOrigins))
            }
            if (iframeContentWindow) {
                configuredEvasions += Evasion(JsScript.IFRAME_CONTENT_WINDOW)
            }
            if (mediaCodecs) {
                configuredEvasions += Evasion(JsScript.MEDIA_CODECS)
            }
            if (navigatorHardwareConcurrency) {
                configuredEvasions += Evasion(
                    JsScript.NAVIGATOR_HARDWARE_CONCURRENCY,
                    mapOf("navigator" to mapOf("hardwareConcurrency" to navigatorHardwareConcurrencyNumber)))
            }
            if (navigatorLanguages) {
                configuredEvasions += Evasion(
                    JsScript.NAVIGATOR_LANGUAGES,
                    mapOf("navigator" to mapOf("languages" to navigatorLanguagesList)))
            }
            if (navigatorPermissions) {
                configuredEvasions += Evasion(JsScript.NAVIGATOR_PERMISSIONS)
            }
            if (navigatorPlugins) {
                configuredEvasions += Evasion(JsScript.NAVIGATOR_PLUGINS, dependsFrom = listOf(JsScript.GENERATE_MAGIC_ARRAYS))
            }
            if (navigatorUserAgent) {
                configuredEvasions += Evasion(
                    JsScript.NAVIGATOR_USER_AGENT,
                    mapOf("navigator_user_agent" to navigatorUserAgentString))
            }
            if (navigatorVendor) {
                configuredEvasions += Evasion(
                    JsScript.NAVIGATOR_VENDOR,
                    mapOf("navigator" to mapOf("vendor" to navigatorVendorString)))
            }
            if (navigatorWebDriver) {
                configuredEvasions += Evasion(JsScript.NAVIGATOR_WEBDRIVER)
            }
            if (webglVendor) {
                configuredEvasions += Evasion(
                    JsScript.WEBGL_VENDOR,
                    mapOf(
                        "webgl_vendor" to webglVendorString,
                        "webgl_renderer" to webglRenderer
                    )
                )
            }
            if (windowOuterDimensions) {
                configuredEvasions += Evasion(JsScript.WINDOW_OUTER_DIMENSIONS)
            }
            return Stealth4jConfig(configuredEvasions)
        }
    }

    private fun enabledScripts(): List<Script> {
        if (evasions.isEmpty()) {
            return emptyList()
        }

        val scripts = mutableListOf<Script>()

        // build global json opts object
        val opts = mapToJson(
            evasions.map { it.opts }
                .fold(emptyMap()) { acc, map -> mergeMaps(acc, map) }
        )
        scripts += ContentScript("const opts = $opts;")
        // add utils script
        scripts += JsScript.UTILS

        // add dependencies
        evasions.forEach { evasion ->
            evasion.dependsFrom.distinct().forEach { scripts += it }
        }

        evasions.forEach { scripts += it.script }

        return scripts
    }
}

/**
 * Data class representing an evasion script with its options and dependencies.
 *
 * @property script The evasion script.
 * @property opts The options for the evasion script.
 * @property dependsFrom The list of scripts that this evasion script depends on.
 */
data class Evasion(val script: Script, val opts: Map<String, Any?> = emptyMap(),
                   val dependsFrom: List<Script> = emptyList())

/**
 * Sealed class representing a script.
 */
sealed class Script {
    abstract val content: String
}

/**
 * Data class representing a JavaScript script.
 *
 * @property url The URL of the script.
 */
data class JsScript(val url: URL): Script() {
    override val content: String by lazy { url.readText(Charsets.UTF_8) }

    companion object {
        private fun getResourceURL(resourceName: String): URL {
            val path = if (resourceName.startsWith("/")) resourceName else "/$resourceName"
            return Companion::class.java.getResource(path)
                ?: throw IllegalArgumentException("Resource not found: $resourceName")
        }

        val CHROME_APP = JsScript(getResourceURL("js/chrome.app.js"))
        val CHROME_CSI = JsScript(getResourceURL("js/chrome.csi.js"))
        val CHROME_LOAD_TIMES = JsScript(getResourceURL("js/chrome.loadTimes.js"))
        val CHROME_RUNTIME = JsScript(getResourceURL("js/chrome.runtime.js"))
        val IFRAME_CONTENT_WINDOW = JsScript(getResourceURL("js/iframe.contentWindow.js"))
        val MEDIA_CODECS = JsScript(getResourceURL("js/media.codecs.js"))
        val NAVIGATOR_HARDWARE_CONCURRENCY = JsScript(getResourceURL("js/navigator.hardwareConcurrency.js"))
        val NAVIGATOR_LANGUAGES = JsScript(getResourceURL("js/navigator.languages.js"))
        val NAVIGATOR_PERMISSIONS = JsScript(getResourceURL("js/navigator.permissions.js"))
        val NAVIGATOR_PLUGINS = JsScript(getResourceURL("js/navigator.plugins.js"))
        val NAVIGATOR_USER_AGENT = JsScript(getResourceURL("js/navigator.userAgent.js"))
        val NAVIGATOR_VENDOR = JsScript(getResourceURL("js/navigator.vendor.js"))
        val NAVIGATOR_WEBDRIVER = JsScript(getResourceURL("js/navigator.webdriver.js"))
        val WEBGL_VENDOR = JsScript(getResourceURL("js/webgl.vendor.js"))
        val WINDOW_OUTER_DIMENSIONS = JsScript(getResourceURL("js/window.outerdimensions.js"))

        val GENERATE_MAGIC_ARRAYS = JsScript(getResourceURL("js/generate.magic.arrays.js"))
        val UTILS = JsScript(getResourceURL("js/utils.js"))
    }
}

data class ContentScript(override val content: String): Script()
