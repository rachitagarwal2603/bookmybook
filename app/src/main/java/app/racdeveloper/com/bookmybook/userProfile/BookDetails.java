package app.racdeveloper.com.bookmybook.userProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 01-08-2016.
 */
public class BookDetails {
    private String name;
    private String description;
    private float rating;
    private int totalRating;
    public static List<BookDetails> details= new ArrayList<>();


/*
    public static final BookDetails[] details= {
        new BookDetails("Revolution 2020"," Once upon a time, in small-town India, there lived two intelligent boys. One wanted to use " +
                "his intelligence to make money. One wanted to use his intelligence to start a revolution. Both of them loved the same" +
                " girl. Revolution 2020 is the story of three childhood friends, Gopal, Raghav and Aarti, who struggle to find love," +
                "happiness and success in Varanasi, none of which is easily attainable in a society that favours unfairness and " +
                "corruption. Gopal gives into the system, Ravi continues to fight it. Who will win? From the bestselling author of" +
                " Five Point Someone and One Night @ the Call Center, comes another gripping tale from the heartland of India. Are " +
                "you ready for a revolution?\n\n Number of Copies - 8"),
        new BookDetails("2 States"," 'Love marriages around the world are simple: Boy loves girl. Girl loves boy. They get married." +
                " In India, there are a few more steps: Boy loves girl. Girl loves boy. Girl's family has to love boy. Boy's family has" +
                " to love girl. Girl's family has to love boy's family. Boy's family has to love girl's family. Girl and boy still love each" +
                " other. They get married. Welcome to 2 States, the story of Krish and Ananya, who are from two different states of India, " +
                "deeply in love with each other, and want to get married. of course, their parents don't agree. To convert their love story " +
                "into a love marriage, the couple has a tough battle ahead of them; for it is easy to fight and rebel, but harder to " +
                "convince. Will they make it? From the bestselling author Chetan Bhagat comes another witty tale about inter-community" +
                " marriages in modern India.\n\n Number of Copies - 1"),
        new BookDetails("The Alchemist","The protagonist of The Alchemist is a young Andalusian shepherd who lives in Spain. He begins" +
                " his journey to Egypt when he is chased by his frequent dreams of a mysterious child who coaxes him to look for treasure" +
                " buried at the base of the Egyptian pyramids. He comes across Melchizedek, the king of Salem during his journey to Egypt." +
                " Santiago is enlightened by him to the concept of a 'Personal Legend' or a quest that would help him realize the core and depth" +
                " of the desires buried in his heart. He meets an aspiring alchemist and a beautiful young woman en route. He also meets an" +
                "alchemist  wisdom, self-discovery and philosophy and finding your own 'Personal Legend' against the comparatively " +
                "invaluable material possessions.\n\n Number of Copies - 2"),
        new BookDetails("Five Point Someone","Five reasons why Hary, Ryan and Aloks lives are a complete mess: 1. They've messed up their grades" +
                " big time. 2. Alok and Ryan cant stop bickering with each other. 3. Hary is smitten with Neha who happens to be his professors" +
                " daughter. 4. As students of IIT, theyre expected to conquer the world, something they know isnt likely to happen. 5. They only " +
                "have each other.Welcome to Five Point Someone by bestselling author Chetan Bhagat. This is not a book that weill teach you how to" +
                " get into IIT or even survive it. In fact, it describes how bad things can get if you don't think straight. Funny, dark and " +
                "entertaining, Five Point Someone is the story of three friends whose measly five point something GPAs come in the wat of everything- " +
                "their friendship, their love life, their future. Will they make it?\n\nNumber of Copies - 3")
    };*/

    BookDetails(){
    }

    public BookDetails(String name, String description, float rating, int totalRating) {
        this.name= name;
        this.description= description;
        this.rating= rating;
        this.totalRating= totalRating;
    }
    public String getDescription(){
        return description;
    }
    public String getName(){
        return name;
    }
    public float getRating(){
        return rating;
    }
    public int getTotalRating(){
        return totalRating;
    }
    public void setName(String name){this.name = name;}
    public void setRating(float rating){this.rating = rating;}
    public void setTotalRating(int rating){this.totalRating = rating;}
    public void setDescription(String description){this.description= description;}
    @Override
    public String toString() {
        return this.name;
    }
}
