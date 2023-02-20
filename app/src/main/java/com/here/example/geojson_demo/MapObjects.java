package com.here.example.geojson_demo;

import java.util.ArrayList;

/*HERE SDK IMPORTS*/
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolygon;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapPolygon;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapView;
/*HERE SDK IMPORTS*/

public class MapObjects {

    private final MapScene mapScene;
    private MapPolyline mapPolyline;
    private MapPolygon mapPolygon;

    public MapObjects(MapView mapView) {
        mapScene = mapView.getMapScene();
    }

    public void createPolyline(ArrayList<GeoCoordinates> pointsCoordinates) {
        clearMap();

        GeoPolyline geoPolyline;
        try {
            geoPolyline = new GeoPolyline(pointsCoordinates);

            float widthInPixels = 20;
            Color lineColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f); // RGBA

            mapPolyline = new MapPolyline(geoPolyline, widthInPixels, lineColor);

            mapScene.addMapPolyline(mapPolyline);
        } catch (InstantiationErrorException e) {
            // Thrown when less than two vertices.
            System.out.println(e.toString());
        }
    }

    public void createPolygon(ArrayList<GeoCoordinates> pointsCoordinates) {
        clearMap();

        GeoPolygon geoPolygon;
        try {
            geoPolygon = new GeoPolygon(pointsCoordinates);

            Color fillColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f); // RGBA
            mapPolygon = new MapPolygon(geoPolygon, fillColor);

            mapScene.addMapPolygon(mapPolygon);

        } catch (InstantiationErrorException e) {
            // Thrown when less than two vertices.
            System.out.println(e.toString());
        }
    }

    private void clearMap() {
        if (mapPolyline != null) {
            mapScene.removeMapPolyline(mapPolyline);
        }

        if (mapPolygon != null) {
            mapScene.removeMapPolygon(mapPolygon);
        }
    }
}
