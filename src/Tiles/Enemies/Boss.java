package Tiles.Enemies;

import Utils.Position;
import Tiles.HeroicUnit;
import Tiles.Players.Player;

import java.util.Random;

public class Boss extends Monster implements HeroicUnit {

    private int abilityFrequency;
    private int combatTicks;


    public Boss(char tile, String name, int healthCapacity, int attack, int defense, int experienceValue, int visionRange, int abilityFrequency){
        super(tile, name, healthCapacity, attack, defense, experienceValue, visionRange);
        this.abilityFrequency=abilityFrequency;
        this.combatTicks=0;
    }

    public Position performAction(){
        Player player = playerCallBack.getPlayer();
        Position newPosition = getPosition();
        if(getPosition().range(player.getPosition()) < visionRange){
            combatTicks++;
            if(combatTicks == abilityFrequency){
                combatTicks=0;
                castAbility();
            }
            else {
                int dx = getPosition().getX() - player.getPosition().getX();
                int dy = getPosition().getY() - player.getPosition().getY();
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0)
                        newPosition = getPosition().add(-1, 0);
                    else
                        newPosition = getPosition().add(1, 0);
                } else {
                    if (dy > 0)
                        newPosition = getPosition().add(0, -1);
                    else
                        newPosition = getPosition().add(0, 1);
                }
            }
        }
        else{
            combatTicks=0;
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

    @Override
    public void castAbility() {
        Player player = playerCallBack.getPlayer();
        if(getPosition().range(player.getPosition()) < visionRange) {
            messageCallBack.send(String.format("%s shoots %s for %d damage.", getName(), player.getName(), getAttack()));
            int defensePoints = player.defense();
            int actualAttack = Math.max(getAttack() - defensePoints, 0);
            messageCallBack.send(String.format("%s hit %s for %d ability damage.", getName(), player.getName(), actualAttack));
            player.getHealth().ReduceAmount(actualAttack);
            if(!player.alive()){
                messageCallBack.send(String.format("%s was killed by %s.", player.getName(), getName()));
            }
        }
    }
}
