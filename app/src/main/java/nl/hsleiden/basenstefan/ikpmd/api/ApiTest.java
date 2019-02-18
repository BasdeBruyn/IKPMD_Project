package nl.hsleiden.basenstefan.ikpmd.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ApiTest {
    public ApiTest(Context context) {
        Type type = new TypeToken<SearchResponse>(){}.getType();

        GsonRequest<SearchResponse> request = new GsonRequest<>("http://www.omdbapi.com/?s=fast&type=movie&apikey=b2ecc309",
                type, null, this::printResponse, this::printError);
        VolleyHelper.getInstance(context).addToRequestQueue(request);
    }

    private void printResponse(SearchResponse searchResponse) {
        Log.d("movies", searchResponse.toString());
    }

    private void printError(VolleyError volleyError) {
        Log.d("Error", volleyError.getMessage());
    }


}
