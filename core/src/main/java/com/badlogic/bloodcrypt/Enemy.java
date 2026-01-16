package com.badlogic.bloodcrypt;


class Enemy {
    float x, y;
    float speed1 = 2f;
    float speed2 = 3.5f;
    int type;


    public Enemy(float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }


    public void update(float targetX, float targetY, float deltaTime, int[][] tileMap, int mapWidth, int mapHeight) {
        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;


        float dirX = dx / dist;
        float dirY = dy / dist;


        // Use speed1 for type 1, speed2 for type 2
        float speed = (type == 2) ? speed2 : speed1;


        float newX = x + dirX * speed * deltaTime;
        float newY = y + dirY * speed * deltaTime;


        if (!isWallTile(newX, y, tileMap, mapWidth, mapHeight)) {
            x = newX;
        }
        if (!isWallTile(x, newY, tileMap, mapWidth, mapHeight)) {
            y = newY;
        }
    }




    private boolean isWallTile(float worldX, float worldY, int[][] tileMap, int mapWidth, int mapHeight) {
        int tileX = (int) Math.floor(worldX);
        int tileY = (int) Math.floor(worldY);


        return tileX < 0 || tileX >= mapWidth ||
            tileY < 0 || tileY >= mapHeight ||
            tileMap[tileY][tileX] == 8;
    }
}
