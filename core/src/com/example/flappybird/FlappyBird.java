package com.example.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
  SpriteBatch batch;
  Texture background;
  ShapeRenderer shapeRenderer;
  Circle birdCircle;
  Rectangle[] topPipeRectangles;
  Rectangle[] bottomPipeRectangles;
  Texture gameover;

  int score = 0;
  int scoringPipe = 0;
  BitmapFont font;

  Texture[] birds;
  int flapState = 0;
  float birdY = 0;
  float velocity = 0;

  int gameState = 0;
  float gravity = 2;

  Texture[] pipes;
  float gap = 400;
  float maxPipeOffset;
  Random randomGenerator;

  //moving tube left to right
  float pipeVelocity = 4;

  int numberOfPipes = 4;
  float[] pipeX = new float[numberOfPipes];
  float[] pipeOffset = new float[numberOfPipes];
  float distanceBetweenPipes;

  @Override
  public void create() {
    batch = new SpriteBatch();
    background = new Texture("bg.png");
    birds = new Texture[2];
    pipes = new Texture[2];
    shapeRenderer = new ShapeRenderer();
    birdCircle = new Circle();
    font = new BitmapFont();
    font.setColor(Color.WHITE);
    font.getData().setScale(10);

    gameover = new Texture("gameover.jpg");

    birds[0] = new Texture("bird.png");
    birds[1] = new Texture("bird2.png");

    pipes[0] = new Texture("toptube.png");
    pipes[1] = new Texture("bottomtube.png");
    topPipeRectangles = new Rectangle[numberOfPipes];
    bottomPipeRectangles = new Rectangle[numberOfPipes];

    //for shifting pipes position up and down
    maxPipeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
    randomGenerator = new Random();

    distanceBetweenPipes = Gdx.graphics.getWidth() * 3 / 4;

    startGame();
  }

  public void startGame() {
    birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

    for (int i = 0; i < numberOfPipes; i++) {
      pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
      //for moving tube left to right
      pipeX[i] = Gdx.graphics.getWidth() / 2 - pipes[0].getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenPipes;

      topPipeRectangles[i] = new Rectangle();     //rectangles for each pipe
      bottomPipeRectangles[i] = new Rectangle();

    }
  }

  //render() repeatedly display things
  @Override
  public void render() {

    batch.begin();
    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    if (gameState == 1) {

      // for score
      if (pipeX[scoringPipe] < Gdx.graphics.getWidth() / 2) {
        score++;
        System.out.println(score);

        if (scoringPipe < numberOfPipes - 1) {
          scoringPipe++;
        } else {
          scoringPipe = 0;
        }

      }

      if (Gdx.input.justTouched()) {    //called every-time screen touched
        velocity = -20;
      }

      //moving tube left to right
      for (int i = 0; i < numberOfPipes; i++) {
        //when all four pipes moves from screen thn bring them back again and again
        if (pipeX[i] < -pipes[0].getWidth()) {
          pipeX[i] += numberOfPipes * distanceBetweenPipes;
          pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
        } else {
          pipeX[i] -= pipeVelocity;
        }


        batch.draw(pipes[0], pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i]);    //top tube
        batch.draw(pipes[1], pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - pipes[1].getHeight() + pipeOffset[i]);   //bottom tube

        topPipeRectangles[i] = new Rectangle(pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i], pipes[0].getWidth(), pipes[0].getHeight());
        bottomPipeRectangles[i] = new Rectangle(pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - pipes[1].getHeight() + pipeOffset[i] + gap / 2 + pipeOffset[i], pipes[1].getWidth(), pipes[1].getHeight());
      }


      //increase and decrese speed and position
      if (birdY > 0) {
        velocity += gravity;
        birdY -= velocity;
      } else {
        gameState = 2;      //for gameover
      }

    } else if (gameState == 0) {
      if (Gdx.input.justTouched()) {    //called every-time screen touched
        gameState = 1;
      }
    } else if (gameState == 2) {      //means gameover
      batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

      //if user taps screen after gameover then start again
      if (Gdx.input.justTouched()) {
        gameState = 1;
        startGame();        //startgame again
        score = 0;
        scoringPipe = 0;
        velocity = 0;
      }

    }

    if (flapState == 0)
      flapState = 1;
    else
      flapState = 0;

    batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);    //in center
    font.draw(batch, String.valueOf(score), 100, 200);   //display score
    batch.end();

    // for collision of bird with pipes
    birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birdY + birds[flapState].getWidth() / 2);

    //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    //shapeRenderer.setColor(Color.RED);
    //shapeRenderer.circle(birdCircle.x, birdCircle.y, 70);

    for (int i = 0; i < numberOfPipes; i++) {
      //shapeRenderer.rect(pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i], pipes[0].getWidth(), pipes[0].getHeight());
      //shapeRenderer.rect(pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - pipes[1].getHeight() + pipeOffset[i] + gap / 2 + pipeOffset[i], pipes[1].getWidth(), pipes[1].getHeight());

      //check if circle and rectangle collided
      if (Intersector.overlaps(birdCircle, topPipeRectangles[i]) || Intersector.overlaps(birdCircle, bottomPipeRectangles[i])) {
        gameState = 2;      //for gameover
      }

    }

    //shapeRenderer.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    background.dispose();
  }
}
