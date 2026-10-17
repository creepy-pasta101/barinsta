package awais.instagrabber.repositories.responses.feed;

import awais.instagrabber.repositories.responses.Media;

/**
 * One entry of the {@code feed_items} array returned by {@code /api/v1/feed/timeline/}.
 * <p>
 * The timeline response does not put the media at the top level of the item: each entry is a
 * wrapper whose post lives under {@code media_or_ad}. Entries that carry no media (for example
 * the end-of-feed demarcator, which instead carries {@code end_of_feed_demarcator}) are skipped
 * by the parser.
 */
public class FeedItem {
    private final Media mediaOrAd;
    private final EndOfFeedDemarcator endOfFeedDemarcator;

    public FeedItem(final Media mediaOrAd, final EndOfFeedDemarcator endOfFeedDemarcator) {
        this.mediaOrAd = mediaOrAd;
        this.endOfFeedDemarcator = endOfFeedDemarcator;
    }

    public Media getMediaOrAd() {
        return mediaOrAd;
    }

    public EndOfFeedDemarcator getEndOfFeedDemarcator() {
        return endOfFeedDemarcator;
    }
}
