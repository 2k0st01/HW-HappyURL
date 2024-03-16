package prog.academy;

public class UrlResultDTO extends UrlDTO {
    protected String shortUrl;
    protected int min;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }


}
