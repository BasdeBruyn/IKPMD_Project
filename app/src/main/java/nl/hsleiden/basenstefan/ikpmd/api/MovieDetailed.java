package nl.hsleiden.basenstefan.ikpmd.api;

public class MovieDetailed {
    private String Title;
    private String Year;
    private String imdbID;
    private String Poster;
    private String imdbRating;
    private String Plot;

    public MovieDetailed(String Title, String Year, String imdbID, String Poster, String imdbRating, String Plot) {
        this.Title = Title;
        this.Year = Year;
        this.imdbID = imdbID;
        this.Poster = Poster;
        this.imdbRating = imdbRating;
        this.Plot = Plot;
    }

    public String getTitle() { return Title; }
    public void setTitle(String title) { Title = title; }
    public String getYear() { return Year; }
    public void setYear(String year) { Year = year; }
    public String getImdbID() { return imdbID; }
    public void setImdbID(String imdbID) { this.imdbID = imdbID; }
    public String getPoster() { return Poster; }
    public void setPoster(String poster) { Poster = poster; }
    public String getImdbRating() { return imdbRating; }
    public void setImdbRating(String imdbRating) { this.imdbRating = imdbRating; }
    public String getPlot() { return Plot; }
    public void setPlot(String plot) { Plot = plot; }

    @Override
    public String toString() {
        return "MovieDetailed{" +
                "Title='" + Title + '\'' +
                ", Year='" + Year + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", Poster='" + Poster + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                ", Plot='" + Plot + '\'' +
                '}';
    }
}
