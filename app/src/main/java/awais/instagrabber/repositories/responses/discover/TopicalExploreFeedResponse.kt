package awais.instagrabber.repositories.responses.discover

import awais.instagrabber.repositories.responses.Media
import awais.instagrabber.repositories.responses.WrappedMedia

data class TopicalExploreFeedResponse(
    val moreAvailable: Boolean = false,
    val nextMaxId: String? = null,
    val maxId: String? = null,
    val status: String? = null,
    val numResults: Int = 0,
    val clusters: List<TopicCluster>? = null,
    val items: List<WrappedMedia>? = null,
    // Meta now answers with sections instead of a flat `items` array; both are accepted.
    val sectionalItems: List<SectionalItem>? = null
) {
    /** Posts from whichever shape the endpoint returned (flat `items`, or flattened sections). */
    val feedItems: List<Media>
        get() = items?.mapNotNull { it.media }
            ?: sectionalItems?.flatMap { it.layoutContent?.media() ?: emptyList() }
            ?: emptyList()
}
