package Tiles.Enemies;

import Utils.Position;
import Utils.Resource;
import Tiles.Players.Player;

import java.util.Random;

public class Monster extends Enemy {
    protected final int MOVE_UP=0;
    protected final int MOVE_DOWN=1;
    protected final int MOVE_LEFT=2;
    protected final int MOVE_RIGHT=3;

    protected int visionRange;

    public Monster(char tile, String name, int healthCapacity, int attack, int defense, int experienceValue, int visionRange){
        super(tile, null, name, new Resource(healthCapacity,healthCapacity), attack, defense, experienceValue);
        this.visionRange=visionRange;
    }

    public Position performAction(){
        Player player = playerCallBack.getPlayer();
        Position newPosition = getPosition();
        if(getPosition().range(player.getPosition()) < visionRange){
            int dx = getPosition().getX()-player.getPosition().getX();
            int dy = getPosition().getY()-player.getPosition().getY();
            if(Math.abs(dx)>Math.abs(dy)){
                if(dx > 0)
                    newPosition = getPosition().add(-1,0);
                else
                    newPosition = getPosition().add(1,0);
            }
            else{
                if(dy > 0)
                    newPosition = getPosition().add(0,-1);
                else
                    newPosition = getPosition().add(0,1);
            }
        }
        else{
            Random rnd = new Random();
            int randomMove = rnd.nextInt(5);
            if(randomMove==MOVE_UP)
                newPosition = getPosition().add(0, -1);
            else if(randomMove==MOVE_DOWN)
                newPosition = getPosition().add(0,1);
            else if(randomMove==MOVE_LEFT)
                newPosition = getPosition().add(-1,0);
            else if(randomMove==MOVE_RIGHT)
                newPosition = getPosition().add(1,0);
        }

        if (newPosition.getX() > getPosition().getX()) {
            this.setFacingRight(true);
        } else if (newPosition.getX() < getPosition().getX()) {
            this.setFacingRight(false);
        }

        return newPosition;
    }

}
