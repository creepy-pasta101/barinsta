package awais.instagrabber.repositories.responses.feed;

import java.util.List;

public class FeedFetchResponse {
    private final List<FeedItem> feedItems;
    private final int numResults;
    private final boolean moreAvailable;
    private final String nextMaxId;
    private final String status;

    public FeedFetchResponse(final List<FeedItem> feedItems,
                             final int numResults,
                             final boolean moreAvailable,
                             final String nextMaxId,
                             final String status) {
        this.feedItems = feedItems;
        this.numResults = numResults;
        this.moreAvailable = moreAvailable;
        this.nextMaxId = nextMaxId;
        this.status = status;
    }

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    public int getNumResults() {
        return numResults;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }

    public String getNextMaxId() {
        return nextMaxId;
    }

    public String getStatus() {
        return status;
    }
}
