package Tiles.Enemies;

import Callbacks.EnemyDeathCallBack;
import Callbacks.GetPlayerCallBack;
import Utils.Position;
import Utils.Resource;
import Tiles.Empty;
import Tiles.Players.Player;
import Tiles.Unit;
import Tiles.Wall;
import VisitorPattern.Visitor;

public abstract class Enemy extends Unit {
    protected int experienceValue;
    protected GetPlayerCallBack playerCallBack;
    protected EnemyDeathCallBack deathCallBack;
    protected boolean facingRight = true;



    public Enemy(char tile, Position position, String name, Resource health, int attack, int defense, int experienceValue){
        super(tile, position, name, health, attack, defense);
        this.experienceValue=experienceValue;
    }


    /// Visitor Pattern
    public void visit(Empty emptyTile){
        swapPosition(emptyTile);
    }

    public void visit(Wall wall){}

    public void visit(Player player){
        battle(player);
        if(!player.alive()){
            messageCallBack.send(String.format("%s was killed by %s.", player.getName(), getName()));
        }
    }
    public void visit(Enemy enemy){}

    public void accept(Visitor v){
        v.visit(this);
    }



    public int getExperienceValue() {
        return experienceValue;
    }

    public void setPlayerCallBack(GetPlayerCallBack playerCallBack) {
        this.playerCallBack = playerCallBack;
    }
    public void setDeathCallBack(EnemyDeathCallBack deathCallBack){
        this.deathCallBack = deathCallBack;
    }

    public void onDeath(){
        deathCallBack.Call();
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

}
