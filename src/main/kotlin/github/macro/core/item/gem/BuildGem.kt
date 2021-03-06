package github.macro.core.item.gem

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import github.macro.Util
import github.macro.core.item.BaseBuildItem
import github.macro.core.item.BuildItemSerializer
import github.macro.core.item.Items
import org.apache.logging.log4j.LogManager
import java.io.IOException

/**
 * Created by Macro303 on 2020-Sep-22
 */
@JsonDeserialize(using = BuildGemDeserializer::class)
@JsonSerialize(using = BuildItemSerializer::class)
class BuildGem(
	item: Gem,
	nextItem: BuildGem? = null,
	reason: String? = null
) : BaseBuildItem(item = item, nextItem = nextItem, reason = reason)

private class BuildGemDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<BuildGem?>(vc) {

	@Throws(IOException::class, JsonProcessingException::class)
	override fun deserialize(parser: JsonParser, ctx: DeserializationContext?): BuildGem {
		val node: JsonNode = parser.codec.readTree(parser)

		val item = Items.getGemById(node["Item"].asText())
		val nextItem = if (node["Next Item"].isNull)
			null
		else
			Util.YAML_MAPPER.treeToValue(node["Next Item"], BuildGem::class.java)
		val reason = if (node["Reason"]?.isNull != false) null else node["Reason"]?.asText()

		return BuildGem(
			item = item,
			nextItem = nextItem,
			reason = reason
		)
	}

	companion object {
		private val LOGGER = LogManager.getLogger()
	}
}