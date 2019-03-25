package nl.hsleiden.basenstefan.ikpmd.api;

import java.util.Arrays;

public class SearchResponse {
    private Movie[] Search;
    private String totalResults;
    private String Response;

    public SearchResponse(Movie[] search, String totalResults, String response) {
        this.Search = search;
        this.totalResults = totalResults;
        this.Response = response;
    }

    public Movie[] getSearch() { return Search; }
    public void setSearch(Movie[] search) { this.Search = search; }
    public String getTotalResults() { return totalResults; }
    public void setTotalResults(String totalResults) { this.totalResults = totalResults; }
    public String getResponse() { return Response; }
    public void setResponse(String response) { this.Response = response; }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "Search=" + Arrays.toString(Search) +
                ", totalResults='" + totalResults + '\'' +
                ", Response='" + Response + '\'' +
                '}';
    }
}
