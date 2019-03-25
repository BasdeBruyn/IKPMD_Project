package nl.hsleiden.basenstefan.ikpmd.api;

import android.content.Context;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MovieRepository {
    private static final String apiUrl = "http://www.omdbapi.com/";
    private static final String apiKey = "b2ecc309";

    public static void searchMovie(String title,
                                   Context context,
                                   Response.Listener<SearchResponse> listener,
                                   Response.ErrorListener errorListener) {
        Type type = new TypeToken<SearchResponse>(){}.getType();

        GsonRequest<SearchResponse> request = new GsonRequest<>(
                getSearchUrl(title),
                type,
                null,
                listener,
                errorListener
        );
        VolleyHelper.getInstance(context).addToRequestQueue(request);
    }

    public static void fetchMovie(String imdbId,
                                   Context context,
                                   Response.Listener<MovieDetailed> listener,
                                   Response.ErrorListener errorListener) {
        Type type = new TypeToken<MovieDetailed>(){}.getType();

        GsonRequest<MovieDetailed> request = new GsonRequest<>(
                getFetchUrl(imdbId),
                type,
                null,
                listener,
                errorListener
        );
        VolleyHelper.getInstance(context).addToRequestQueue(request);
    }

    private static String getSearchUrl(String title) {
        return String.format("%s?apikey=%s&s=%s&type=movie", apiUrl, apiKey, title);
    }

    private static String getFetchUrl(String imdbId) {
        return String.format("%s?apikey=%s&i=%s", apiUrl, apiKey, imdbId);
    }


}
