package awais.instagrabber.repositories.responses.discover

import awais.instagrabber.repositories.responses.Media
import java.io.Serializable

/** One entry of `sectional_items` from `/api/v1/discover/topical_explore/`. */
data class SectionalItem(
    val layoutContent: LayoutContent? = null
) : Serializable

data class LayoutContent(
    val medias: List<DiscoverMedia>? = null,
    val fillItems: List<DiscoverMedia>? = null
) : Serializable {
    fun media(): List<Media> =
        ((medias ?: emptyList()) + (fillItems ?: emptyList())).flatMap { it.flatten() }
}

/**
 * A `medias` / `fill_items` entry is either a plain post (`media`) or a reels cluster whose posts
 * live under `clips.items` (each of which is again one of these).
 */
data class DiscoverMedia(
    val media: Media? = null,
    val clips: Clips? = null
) : Serializable {
    fun flatten(): List<Media> =
        listOfNotNull(media) + (clips?.items?.flatMap { it.flatten() } ?: emptyList())
}

data class Clips(
    val items: List<DiscoverMedia>? = null
) : Serializable
