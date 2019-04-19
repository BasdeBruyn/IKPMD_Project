package nl.hsleiden.basenstefan.ikpmd.api;

import java.io.Serializable;

public class MovieDetailed extends Movie {
    private String imdbRating;
    private String Plot;

    public MovieDetailed(String Title, String Year, String imdbID, String Poster, String imdbRating, String Plot) {
        super(Title, Year, imdbID, Poster);
        this.imdbRating = imdbRating;
        this.Plot = Plot;
    }

    public String getTitle() { return super.getTitle(); }
    public void setTitle(String title) { super.setTitle(title); }
    public String getYear() { return super.getYear(); }
    public void setYear(String year) { super.getYear(); }
    public String getImdbID() { return super.getImdbID(); }
    public void setImdbID(String imdbID) { super.setImdbID(imdbID); }
    public String getPoster() { return super.getPoster(); }
    public void setPoster(String poster) { super.setPoster(poster); }
    public String getImdbRating() { return imdbRating; }
    public void setImdbRating(String imdbRating) { this.imdbRating = imdbRating; }
    public String getPlot() { return Plot; }
    public void setPlot(String plot) { this.Plot = plot; }

    @Override
    public String toString() {
        return "MovieDetailed{" +
                "title='" + Title + '\'' +
                ", year='" + Year + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", poster='" + Poster + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                ", plot='" + Plot + '\'' +
                '}';
    }
}
