package com.minesweeper.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

public class Grid {
    private int width;
    private int height;

    private int windowHeight;
    private int windowWidth;

    private int tileSize;
    private int heightOffset;
    private int widthOffset;

    private int numMines;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;

    private Tile[][] grid;

    public Grid(int width, int height, int numMines, ShapeRenderer shapeRenderer, BitmapFont font, SpriteBatch batch) {
        this.width = width;
        this.height = height;
        this.numMines = numMines;

        this.windowHeight = Gdx.graphics.getHeight();
        this.windowWidth = Gdx.graphics.getWidth();

        tileSize = Math.min((((windowWidth - windowWidth / 8) - windowWidth / 40) / width), 
            (((windowHeight - windowHeight / 4) - windowHeight / 20) / height));

        heightOffset = (windowHeight - (tileSize * height)) / 2 + windowHeight / 14;
        widthOffset = (windowWidth - (tileSize * width)) / 2;

        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.batch = batch;
    }

    /**
     * Generates a 2d array with a given width and height.
     */
    public void generateGrid() {
        Tile[] unsortedList =  new Tile[width * height];
        Tile[] sortedList =  new Tile[width * height];

        for(int i = 0; i < height * width; i++) {
            if(i < numMines) {
                unsortedList[i] = new Tile(true, font, batch);
            } else {
                unsortedList[i] = new Tile(false, font, batch);
            }
        }
        
        List<Tile> unsortedArray = Arrays.asList(unsortedList);
        Collections.shuffle(unsortedArray);
        unsortedArray.toArray(sortedList);

        int index = 0;
        grid = new Tile[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                grid[i][j] = sortedList[index];
                index += 1;
            }
        }
    }
    
    public void resetGrid() {
        generateGrid();
        generateAdjacentMines();

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                grid[i][j].setState(Tile.State.HIDDEN);
            }
        }
    }

    public void render() {
        for(int i = 0; i < height * 2; i++) {
            for(int j = 0; j < width; j++) {
                if(i < height) {
                    grid[i][j].renderTile(j * tileSize + widthOffset, i * tileSize + heightOffset, tileSize, shapeRenderer);
                    grid[i][j].renderNumbers(j * tileSize + widthOffset + tileSize / 2, windowHeight - (i * tileSize + heightOffset + tileSize / 2));
                }

            }
        }
    }
    
    public void touchDetection(int x, int y) {
        int xIndex = (x - widthOffset) / tileSize;
        int yIndex = (((-1 * y) - heightOffset + windowHeight)) / tileSize;
        if(xIndex >= 0 && xIndex < width && yIndex >= 0 && yIndex < height) {
            grid[yIndex][xIndex].tapped();
            if(grid[yIndex][xIndex].getAdjacentMines() == 0) {
                autoReveal(xIndex, yIndex);
            }
        }
    }

    public void autoReveal(int x, int y) {
        if(y > 0) {
            if(x > 0) {
                if(!grid[y - 1][x - 1].isMine() && grid[y - 1][x - 1].isRevealed() == false) {
                    grid[y - 1][x - 1].reveal();
                    if(grid[y - 1][x - 1].getAdjacentMines() == 0 ){
                        autoReveal(x - 1, y - 1);
                    }
                }
            }
            if(!grid[y - 1][x].isMine() && grid[y - 1][x].isRevealed() == false) {
                grid[y - 1][x].reveal();
                if(grid[y - 1][x].getAdjacentMines() == 0 ){
                    autoReveal(x, y - 1);
                }
            }
            if(x < width - 1) {
                if(!grid[y - 1][x + 1].isMine() && grid[y - 1][x + 1].isRevealed() == false) {
                    grid[y - 1][x + 1].reveal();
                    if(grid[y - 1][x + 1].getAdjacentMines() == 0) {
                        autoReveal(x + 1, y - 1);
                    }
                }
            } 
        }
        if(x > 0 && x < width - 1) {
            if(y < height - 1) {
                if(!grid[y + 1][x - 1].isMine() && grid[y + 1][x - 1].isRevealed() == false) {
                    grid[y + 1][x - 1].reveal();
                    if(grid[y + 1][x - 1].getAdjacentMines() == 0) {
                        autoReveal(x - 1, y + 1);
                    }
                }
            }
            if(!grid[y][x - 1].isMine() && !grid[y][x - 1].isRevealed()) {
                grid[y][x - 1].reveal();
                if(grid[y][x - 1].getAdjacentMines() == 0) {
                    autoReveal(x - 1, y);
                }
            }
        }
        if(y < height - 1) {
            if(x < width - 1) {
                if(!grid[y + 1][x + 1].isMine() && grid[y + 1][x + 1].isRevealed() == false) {
                    grid[y + 1][x + 1].reveal();
                    if(grid[y + 1][x + 1].getAdjacentMines() == 0) {
                        autoReveal(x + 1, y + 1);
                    }
                }
            }
            if(!grid[y + 1][x].isMine() && grid[y + 1][x].isRevealed() == false) {
                grid[y + 1][x].reveal();
                if(grid[y + 1][x].getAdjacentMines() == 0) {
                    autoReveal(x, y + 1);
                }
            }
        }
        if(x < width - 1) {
            if(!grid[y][x + 1].isMine() && grid[y][x + 1].isRevealed() == false) {
                grid[y][x + 1].reveal();
                if(grid[y][x + 1].getAdjacentMines() == 0) {
                    autoReveal(x + 1, y);
                }
            }
        }
    }

    public void generateAdjacentMines() {
        int mineCount;
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                mineCount = 0;
                if(i != 0) {
                    if(j != 0) {
                        if(grid[i - 1][j - 1].isMine()) {
                            mineCount += 1;
                        }
                    }
                    if(grid[i - 1][j].isMine()) {
                        mineCount += 1;
                    }
                    if(j != width - 1) {
                        if(grid[i - 1][j + 1].isMine()) {
                            mineCount += 1;
                        }
                    }
                }
                if(j != 0) {
                    if(i != height - 1) {
                        if(grid[i + 1][j - 1].isMine()) {
                            mineCount += 1;
                        }
                    }
                    if(grid[i][j - 1].isMine()) {
                        mineCount += 1;
                    }
                }
                if(i != height - 1) {
                    if(j != width - 1) {
                        if(grid[i + 1][j + 1].isMine()) {
                            mineCount += 1;
                        }
                    }
                    if(grid[i + 1][j].isMine()) {
                        mineCount += 1;
                    }
                }
                if(j != width - 1) {
                    if(grid[i][j + 1].isMine()) {
                        mineCount += 1;
                    }
                }
                grid[i][j].setAdjacentMines(mineCount);
            }
        }
     }
}
