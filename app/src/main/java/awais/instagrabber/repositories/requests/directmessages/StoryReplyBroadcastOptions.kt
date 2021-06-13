package awais.instagrabber.repositories.requests.directmessages

import awais.instagrabber.models.enums.BroadcastItemType

class StoryReplyBroadcastOptions(
    clientContext: String,
    threadIdOrUserIds: ThreadIdOrUserIds,
    val text: String,
    val mediaId: String,
    val reelId: String // or user id, usually same
) : BroadcastOptions(clientContext, threadIdOrUserIds, BroadcastItemType.REELSHARE) {
    override val formMap: Map<String, String>
        get() = mapOf(
            "text" to text,
            "media_id" to mediaId,
            "reel_id" to reelId,
            "entry" to "reel",
        )
}