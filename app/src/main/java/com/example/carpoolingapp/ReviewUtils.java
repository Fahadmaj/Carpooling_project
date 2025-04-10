package com.example.carpoolingapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReviewUtils {

    public static double getAverageRating(Context context, String driverName) {
        try {
            File file = new File(context.getFilesDir(), "reviews.json");
            if (!file.exists()) return 0;

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(builder.toString());
            if (root.has(driverName)) {
                JSONArray ratings = root.getJSONObject(driverName).getJSONArray("ratings");
                double sum = 0;
                for (int i = 0; i < ratings.length(); i++) {
                    sum += ratings.getInt(i);
                }
                return ratings.length() > 0 ? sum / ratings.length() : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
