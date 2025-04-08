package io.github.kihdev.playwright.stealth4j

import com.microsoft.playwright.Page
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.nio.file.Paths

class AntibotPage (private val name: String, private val page: Page) {

    companion object {
        val json = Json { ignoreUnknownKeys = true }

        val iframeScript = """
            async () => {
                const iframe = document.createElement('iframe');
                iframe.srcdoc = 'blank page';
                document.body.appendChild(iframe);
                const contentWindow = iframe.contentWindow instanceof Window;
                iframe.remove();
                return contentWindow;
            }
        """.trimIndent()
    }

    init {
        page.navigate("https://bot.sannysoft.com/")
    }

    val info by lazy { json.decodeFromString<FpCollectInfo>(page.locator("pre#fp").textContent()) }

    fun iframeContentWindow() = page.evaluate(iframeScript) as Boolean

    fun navigatorHardwareConcurrency() =
        page.evaluate("""() => { return navigator.hardwareConcurrency }""") as Int

    fun navigatorVendor() =
        page.evaluate("""() => { return navigator.vendor }""") as String

    fun navigatorWebdriver() =
        page.evaluate("""() => { return navigator.webdriver }""") as Boolean?

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
    val hasChrome: Boolean,
    @Serializable(with = DetailChromeSerializer::class) val detailChrome: DetailChrome,
    val audioCodecs: Map<String, String>,
    val videoCodecs: Map<String, String>,
    val navigatorPrototype: List<String>,
    val languages: List<String>,
    val permissions: Map<String, String>,
    val plugins: List<String>,
    val videoCard: List<String>
)

@Serializable
sealed class DetailChrome {
    data class Unknown(val value: String) : DetailChrome()

    data class Details(val value: Map<String, String>) : DetailChrome()
}

object DetailChromeSerializer : JsonContentPolymorphicSerializer<DetailChrome>(DetailChrome::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DetailChrome> {
        return when (element) {
            is JsonPrimitive -> UnknownSerializer
            is JsonObject -> DetailsSerializer
            else -> throw SerializationException("Unsupported type for StringOrMap")
        }
    }

    private val UnknownSerializer = object : KSerializer<DetailChrome.Unknown> {
        override val descriptor = PrimitiveSerialDescriptor("String", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): DetailChrome.Unknown {
            val value = decoder.decodeString()
            return DetailChrome.Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: DetailChrome.Unknown) {
            encoder.encodeString(value.value)
        }
    }

    private val DetailsSerializer = object : KSerializer<DetailChrome.Details> {
        private val delegate = MapSerializer(String.serializer(), String.serializer())

        override val descriptor = delegate.descriptor
        override fun deserialize(decoder: Decoder): DetailChrome.Details {
            val map = delegate.deserialize(decoder)
            return DetailChrome.Details(map)
        }

        override fun serialize(encoder: Encoder, value: DetailChrome.Details) {
            delegate.serialize(encoder, value.value)
        }
    }
}

