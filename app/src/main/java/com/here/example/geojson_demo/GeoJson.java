package com.here.example.geojson_demo;

import android.os.Environment;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*HERE SDK IMPORTS*/
import com.here.sdk.core.GeoCoordinates;
/*HERE SDK IMPORTS*/

public class GeoJson {
    public ArrayList<GeoCoordinates> read_file(Context context, String fileName) {

        File sdcard = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(sdcard.getAbsolutePath());
        ArrayList<GeoCoordinates> pointsCoordinates = new ArrayList<>();

        if(dir.exists()) {
                File file = new File(dir, fileName);
                StringBuilder sb = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    br.close();

                    JSONObject geoJson = new JSONObject(sb.toString());
                    JSONArray features = geoJson.getJSONArray("features");
                    for (int i = 0; i < features.length(); i++){
                        JSONObject featuresData = features.getJSONObject(i);
                        JSONObject geometryData = new JSONObject(featuresData.getString("geometry"));

                        String[] data = geometryData.getString("coordinates").replaceAll("\\[", "").replaceAll("]","").split(",");

                        pointsCoordinates.add(new GeoCoordinates(Double.parseDouble(data[1]), Double.parseDouble(data[0])));
                    }

                    return pointsCoordinates;
                } catch (IOException | JSONException e) {
                    Log.e("Error", e.toString());

                    Toast.makeText(context,
                            "The file was not found or an error occurred while trying to read it",
                            Toast.LENGTH_LONG).show();
                    return  null;
                }
        } else {
            Log.e("Error", "The desired directory does not exist");
            Toast.makeText(context,
                    "The desired directory does not exist",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
