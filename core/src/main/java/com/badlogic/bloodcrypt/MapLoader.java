package com.badlogic.bloodcrypt;


import com.badlogic.gdx.Gdx;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class MapLoader {


    // Loads a CSV map file into a 2D int array
    public static int[][] loadCsv(String filePath) {
        ArrayList<int[]> rows = new ArrayList<>();


        try {
            BufferedReader reader = new BufferedReader(Gdx.files.internal(filePath).reader());
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split(",");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    try {
                        row[i] = Integer.parseInt(tokens[i].trim());
                    } catch (NumberFormatException e) {
                        row[i] = 0; // default to floor if invalid
                    }
                }
                rows.add(row);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            return new int[0][0];
        }


        int[][] map = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            map[i] = rows.get(i);
        }
        return map;
    }


    // Finds the first occurrence of a specific tile ID
    public static int[] findTile(int[][] map, int targetId) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == targetId) {
                    return new int[]{x, y};
                }
            }
        }
        return null; // not found
    }


    // Finds all occurrences of a specific tile ID (e.g. all enemy spawns)
    public static ArrayList<int[]> findAllTiles(int[][] map, int targetId) {
        ArrayList<int[]> positions = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == targetId) {
                    positions.add(new int[]{x, y});
                }
            }
        }
        return positions;
    }
}

