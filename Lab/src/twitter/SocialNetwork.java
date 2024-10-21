package twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {
	
    /**
     * Constructs a social network graph based on the mentions in a list of tweets.
     * 
     * @param tweets the list of tweets to analyze
     * @return a Map where each key is a user (tweet author) and the value is a set of users they "follow" (based on mentions)
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        
        for (Tweet tweet : tweets) {
            String user = tweet.getAuthor().toLowerCase();
            Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet));

            // Only add the user to the graph if they have mentioned someone
            if (!mentionedUsers.isEmpty()) {
                followsGraph.putIfAbsent(user, new HashSet<>());
                
                for (String mentionedUser : mentionedUsers) {
                    if (!mentionedUser.equalsIgnoreCase(user)) {
                        followsGraph.get(user).add(mentionedUser.toLowerCase());
                    }
                }
            }
        }
        
        return followsGraph;
    }

    /**
     * Returns a list of people sorted by their influence (total number of followers).
     * 
     * @param followsGraph a social network represented as a Map where each key is a user, 
     *                     and the value is the set of users they follow
     * @return a list of all distinct Twitter usernames in followsGraph, 
     *         sorted in descending order of follower count
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // Count followers for each user
        for (Set<String> following : followsGraph.values()) {
            for (String followedUser : following) {
                followerCount.put(followedUser, followerCount.getOrDefault(followedUser, 0) + 1);
            }
        }

        List<String> users = new ArrayList<>(followerCount.keySet());

        // Sort by follower count in descending order, then by username in ascending order
        users.sort((user1, user2) -> {
            int countCompare = followerCount.get(user2).compareTo(followerCount.get(user1));
            if (countCompare == 0) {
                return user1.compareTo(user2); // Sort alphabetically if counts are equal
            }
            return countCompare;
        });

        return users;
    }

}
