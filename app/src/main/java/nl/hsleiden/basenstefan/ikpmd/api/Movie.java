package nl.hsleiden.basenstefan.ikpmd.api;

public class Movie {
    private String Title;
    private String Year;
    private String imdbID;
    private String Poster;

    public Movie(String Title, String Year, String imdbID, String Poster) {
        this.Title = Title;
        this.Year = Year;
        this.imdbID = imdbID;
        this.Poster = Poster;
    }

    public String getTitle() { return Title; }
    public void setTitle(String title) { this.Title = title; }
    public String getYear() { return Year; }
    public void setYear(String year) { this.Year = year; }
    public String getImdbID() { return imdbID; }
    public void setImdbID(String imdbID) { this.imdbID = imdbID; }

    public String getPoster() { return Poster; }
    public void setPoster(String poster) { this.Poster = poster; }

    @Override
    public String toString() {
        return "Movie{" +
                "Title='" + Title + '\'' +
                ", Year='" + Year + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", Poster='" + Poster + '\'' +
                '}';
    }
}
