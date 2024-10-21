package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    private static final Instant d1 = Instant.parse("2020-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2020-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2020-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "ernie", "hello world", d1);
    private static final Tweet tweet2 = new Tweet(2, "ernie", "@bert what's up?", d2);
    private static final Tweet tweet3 = new Tweet(3, "bert", "just hanging out", d3);
    private static final Tweet tweet4 = new Tweet(4, "ernie", "@bert @elmo let's hang out!", d3);
    private static final Tweet tweet5 = new Tweet(5, "elmo", "@ernie yay!", d1);

    // Test for empty list of tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // Test for tweets with no mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        List<Tweet> tweets = Arrays.asList(tweet1, tweet3);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected empty graph, no mentions", followsGraph.isEmpty());
    }

    // Test for a single mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        List<Tweet> tweets = Arrays.asList(tweet2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected ernie to follow bert", followsGraph.containsKey("ernie"));
        assertEquals("ernie should follow bert", Set.of("bert"), followsGraph.get("ernie"));
    }

    // Test for multiple mentions in a single tweet
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        List<Tweet> tweets = Arrays.asList(tweet4);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected ernie to follow bert and elmo", followsGraph.containsKey("ernie"));
        assertEquals("ernie should follow bert and elmo", Set.of("bert", "elmo"), followsGraph.get("ernie"));
    }

    // Test for multiple tweets from the same user
    @Test
    public void testGuessFollowsGraphMultipleTweets() {
        List<Tweet> tweets = Arrays.asList(tweet2, tweet4);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected ernie to follow bert and elmo", followsGraph.containsKey("ernie"));
        assertEquals("ernie should follow bert and elmo", Set.of("bert", "elmo"), followsGraph.get("ernie"));
    }

    // Test for influencers with an empty graph
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

    // Test for a single user without followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", new HashSet<>());

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

    // Test for a single influencer
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected bert to be the only influencer", List.of("bert"), influencers);
    }

    // Test for multiple influencers
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert", "elmo"));
        followsGraph.put("elmo", Set.of("ernie"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected influencers to be bert, elmo, ernie", List.of("bert", "elmo", "ernie"), influencers);
    }

    // Test for tied influence
    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert"));
        followsGraph.put("elmo", Set.of("bert"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected bert to be the only influencer", List.of("bert"), influencers);
    }
}
