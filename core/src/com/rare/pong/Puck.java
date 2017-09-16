package com.rare.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Puck {
    private Vector2 pos = new Vector2(Gdx.graphics.getWidth()/2f - 15f/2f, Gdx.graphics.getHeight()/2f - 15f/2f);
    private Vector2 vel;
    private float size = 15f;
    private float minSpeed = 500f;
    private float speed;
    private float accelerationPercent = 1.002f;
    private float maxSpeed = 1200f;
    private ShapeRenderer shapeRenderer;
    private Score score;
    public boolean checkPaddles = false;

    public Puck(Score score){
        reset();
        shapeRenderer = new ShapeRenderer();
        this.score = score;
    }

    public void update(float dt, Paddle leftP, Paddle rightP){
        if(!score.gameOver) {
            glide(dt);
            if(checkPaddles)
                checkCollisions(leftP, rightP);

            // Accelerating
            if (vel.len() <= maxSpeed) {
                vel.scl(accelerationPercent, accelerationPercent);
                speed *= accelerationPercent;
            }
        }

        // Drawing the puck
        draw();
    }

    public void draw(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(pos.x, pos.y, size, size);
        shapeRenderer.end();
    }

    public void glide(float dt){
        pos.add(new Vector2(vel.x * dt, vel.y * dt)); // Applying velocity to the position of the puck

        if(checkPaddles) {
            if (pos.x <= -size) { // LEFT
                reset();
                score.scoreRight();
            } else if (pos.x >= Gdx.graphics.getWidth()) { // RIGHT
                reset();
                score.scoreLeft();
            }
        } else {
            // Bouncing off horizontal edges of the window
            if (pos.x <= 0f) { // LEFT
                vel.scl(-1f, 1f);
                pos.set(0f, pos.y);
            } else if (pos.x >= Gdx.graphics.getWidth() - size) { // RIGHT
                vel.scl(-1f, 1f);
                pos.set(Gdx.graphics.getWidth() - size, pos.y);
            }
        }

        // Bouncing off vertical edges of the window
        if(pos.y <= 0f) {
            vel.scl(1f, -1f);
            pos.set(pos.x, 0f);
        } else if(pos.y >= Gdx.graphics.getHeight() - size){
            vel.scl(1f, -1f);
            pos.set(pos.x, Gdx.graphics.getHeight() - size);
        }
    }

    private void checkCollisions(Paddle leftP, Paddle rightP){
        if(pos.y <= leftP.getY() + leftP.getHeight() && pos.y + size >= leftP.getY() && pos.x <= leftP.getX() + leftP.getWidth() && pos.x + size >= leftP.getX() + leftP.getWidth()/2f) {
            // Bouncing off with some angle depending on the certain place of the paddle
            float diff = (pos.y + size/2f) - (leftP.getY() + leftP.getHeight()/2f);
            float angle = diff / (leftP.getHeight()/2f) * 45f;
            angle = MathUtils.clamp(angle, -45f, 45f) * MathUtils.degRad;
            vel = new Vector2(Math.abs(speed * MathUtils.cos(angle)), Math.abs(speed) * MathUtils.sin(angle));
            pos.set(leftP.getX() + leftP.getWidth(), pos.y); // Avoiding bugs via positioning the puck just in front of the paddle
        } else if(pos.y <= rightP.getY() + rightP.getHeight() && pos.y + size >= rightP.getY() && pos.x + size >= rightP.getX() && pos.x <= rightP.getX() + rightP.getWidth()/2f) {
            // Same here
            float diff = (pos.y + size/2f) - (rightP.getY() + rightP.getHeight()/2);
            float angle = diff / (rightP.getHeight()/2f) * 45f;
            angle = MathUtils.clamp(angle, -45f, 45f) * MathUtils.degRad;
            vel = new Vector2(-Math.abs(speed * MathUtils.cos(angle)), Math.abs(speed) * MathUtils.sin(angle));
            pos.set(rightP.getX() - size, pos.y); // same here
        }
    }

    private void reset(){
        pos.set(Gdx.graphics.getWidth()/2f - size/2f, Gdx.graphics.getHeight()/2 - size/2); // Positioning the puck right in the centre
        speed = minSpeed * (MathUtils.random() < 0.5f ? -1f : 1f);
        float angle = MathUtils.random(-45f, 45f) * MathUtils.degRad;
        vel = new Vector2(speed * MathUtils.cos(angle), speed * MathUtils.sin(angle));
    }

    public void dispose(){
        shapeRenderer.dispose();
    }
}
