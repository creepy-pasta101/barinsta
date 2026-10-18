package awais.instagrabber.repositories.responses.discover

import awais.instagrabber.repositories.responses.WrappedMedia
import java.io.Serializable

/**
 * One entry of `sectional_items` from `/api/v1/discover/topical_explore/` (the shape Meta returns
 * when `use_sectional_payload` is on). The posts live under `layout_content.medias` / `.fill_items`.
 */
data class SectionalItem(
    val layoutContent: LayoutContent? = null
) : Serializable

data class LayoutContent(
    val medias: List<WrappedMedia>? = null,
    val fillItems: List<WrappedMedia>? = null
) : Serializable {
    fun mediaWrappers(): List<WrappedMedia> = (medias ?: emptyList()) + (fillItems ?: emptyList())
}
