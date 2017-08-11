package app.racdeveloper.com.bookmybook.ebookList;

/**
 * Created by Rachit on 4/29/2017.
 */

public class EBookData {

    private int id;
    private String bookName, bookUrl;

    public EBookData(int id, String bookName, String bookUrl) {
        this.id = id;
        this.bookName = bookName;
        this.bookUrl = bookUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setResumeName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
}
