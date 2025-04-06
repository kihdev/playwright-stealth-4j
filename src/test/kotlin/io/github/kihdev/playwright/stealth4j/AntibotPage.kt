package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Page
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Paths

class AntibotPage (private val name: String, private val page: Page) {

    companion object {
        val json = Json { ignoreUnknownKeys = true }
    }

    init {
        page.navigate("https://bot.sannysoft.com/")
    }

    val info by lazy { json.decodeFromString<FpCollectInfo>(page.locator("pre#fp").textContent()) }

    fun screenshot() {
        page.screenshot(
            Page.ScreenshotOptions()
                .setPath(Paths.get("build/tmp/screenshots/$name.png"))
                .setFullPage(true)
        )
    }

}

@Serializable
data class FpCollectInfo(
    val hasChrome: Boolean
)