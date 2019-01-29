package com.ilmale.doodlejump.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.ilmale.doodlejump.Constants;
import com.ilmale.doodlejump.GameActivity;
import com.ilmale.doodlejump.Records;
import com.ilmale.doodlejump.domain.*;

import java.util.List;
import java.util.ArrayList;

@SuppressLint("Registered")
public class GameEngine {

    private static final String LOG_TAG = GameEngine.class.getSimpleName();

    // gameView will be the view of the game
    // and respond to screen touches as well
    //GameView gameView;
    private long lastUpdate;

    List<AbstractGameObject> objects = new ArrayList<>();

    public int contatore; //contatore per operazioni di ottimizzazione

    public Player player;
    public List<Platform> platforms = new ArrayList<>();
    public List<Platform> platformsNearThePlayer = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public Jetpack jetpack;
    public Enemy enemy;
    private Constants constants = Constants.getInstance();
    private Records records = Records.getInstance();

    private boolean gameOver=false;

    private int points=0;

    private int jumpForce = 50;

    public GameEngine(){
        lastUpdate = System.currentTimeMillis();
        player = new Player();

        Platform platform = new Platform();
        platform.createRandomPlatform(platforms);
        player.setPlatforms(platforms);
        player.setBullets(bullets);

        enemy = new Enemy();
        jetpack = new Jetpack();
        player.setEnemy(enemy);
        player.setJetpack(jetpack);
    }

    public void update() {
        //Log.d(LOG_TAG, "updating gameengine");
        player.update();

        for (Platform p: platformsNearThePlayer) {
            if (collidesFromAbove(player, p)){
                //Log.d(LOG_TAG, "collision!");
                if (p.hasSprings()) {
                    player.jump(jumpForce * 3);
                }
                else {
                    player.jump(jumpForce);
                }
            }
        }

        for (Bullet b: bullets){
            b.update();
        }

        if (player.getpY() < constants.getPixelHeight()/3) {
            player.setpY(constants.getPixelHeight()/3);
            for(Platform p: platforms){
                p.setyS(player.getVelY() / 2);
                p.update();
            }
            enemy.setyS((player.getVelY() / 2));
            enemy.update();
            jetpack.setyS((player.getVelY() / 2));
            jetpack.update();
        }

        if (contatore == 0){
            aggiornaPiattaforme();
        }

        takeJetpack();
        killEnemy();

        if(isDeath()){
            endGame();
        }

        if (contatore > 10) {
            contatore = 0;
        }
        contatore++;
    }

    public void updatePlayer() {
        player.updateControls();
    }

    private boolean collide(AbstractGameObject obj1, AbstractGameObject obj2) {
        float x11 = obj1.getpX();
        float x12 = x11 + obj1.getWidth();
        float x21 = obj2.getpX();
        float x22 = x21 + obj2.getWidth();
        float y11 = obj1.getpY();
        float y12 = y11 + obj1.getHeight();
        float y21 = obj2.getpY();
        float y22 = y21 + obj2.getHeight();
        if ((x11 >= x21 && x11 <= x22) || (x12 >= x21 && x12 <= x22)) {
            return (y11 >= y21 && y11 <= y22) || (y12 >= y21 && y12 <= y22);
        }
        return false;
    }

    private boolean collidesFromAbove(AbstractGameObject obj1, AbstractGameObject obj2) {
        float x11 = obj1.getpX();
        float x12 = x11 + obj1.getWidth();
        float x21 = obj2.getpX();
        float x22 = x21 + obj2.getWidth();
        float y11 = obj1.getpY();
        float y12 = y11 + obj1.getHeight();
        float y21 = obj2.getpY();
        if ((x11 >= x21 && x11 <= (x22 - player.getHeight()/4)) || (x12 >= (x21 + player.getHeight()/4) && x12 <= x22)) {
            if (y12 <= y21 + 15 && y12 >= y21 - 45){
                return player.getVelY() >= 0;
            }
        }
        return false;
    }

    public List<Platform> getPlatforms(){
        return platforms;
    }

    public void takeJetpack(){
        if (collide(player,jetpack)){
            if(!player.hasJetpack()){
                player.pickJetpack();
                jetpack.replace();
            }
        }
    }

    public void killEnemy(){

        for(Bullet b:bullets){
            if (collide(b,enemy)) {
                enemy.replace();
            }
        }
        if (collidesFromAbove(player,enemy)){
            enemy.replace();
            player.jump(40);
        }
    }

    public boolean killedByEnemy(){
        if(!player.hasJetpack()){
            if(!collidesFromAbove(player,enemy)){
                if (collide(player, enemy)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeath(){
        if(player.getpY()>constants.getPixelHeight() || killedByEnemy()){
            return true;
        }
        return false;
    }

    public void shoot() {
        Bullet bullet = new Bullet(player.getpX()+53, player.getpY());
        bullets.add(bullet);
    }

    public void removeBullets(){
        for(Bullet b: bullets){
            if(b.getpY()<0){
                bullets.remove(b);
            }
        }
    }

    public void endGame(){
        if(!gameOver){
            records.updateRecords();
        }
        gameOver=true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void aggiornaPiattaforme(){
        for (Platform p: platforms){
            if (p.getpY() >= player.getpY() - 1000 || p.getpY() <= player.getpY() + 1000){
                platformsNearThePlayer.add(p);
            }
            else if (platformsNearThePlayer.contains(p)){
                platformsNearThePlayer.remove(p);
            }
        }
    }
}
