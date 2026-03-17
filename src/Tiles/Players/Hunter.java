package Tiles.Players;

import Utils.Resource;
import Tiles.Enemies.Enemy;

import java.util.List;

public class Hunter extends Player {

    private int range;
    private int arrowsCount;
    private int ticksCount ;
    private static final int HUNTER_ATTACK_BONUS=2;
    private static final int HUNTER_DEFENSE_BONUS=1;
    private static final int ARROWS_BONUS = 10;
    private static final int ARROW_ADDITION_TICKS = 10;

    public Hunter(String name, int healthCapacity, int attack, int defense, int range) {
        super(null, name, new Resource(healthCapacity,healthCapacity), attack, defense);
        this.range = range;
        ticksCount =0;
        arrowsCount = 10 * level;
        specialAbilityName = "Shoot Arrow";
    }

    protected int gainDefense(){
        return level*(HUNTER_DEFENSE_BONUS+DEFENSE_BONUS);
    }
    protected int gainAttack(){
        return level*(HUNTER_ATTACK_BONUS+ATTACK_BONUS) ;
    }

    @Override
    public void onGameTick() {
        if(ticksCount == ARROW_ADDITION_TICKS ){
            arrowsCount = arrowsCount + level;
            ticksCount = 0;
        }else{
            ticksCount = ticksCount + 1;
        }

    }

    public int getRange() {
        return range;
    }

    public int getArrowsCount() {
        return arrowsCount;
    }

    public int getTicksCount() {
        return ticksCount;
    }

    @Override
    public void gainSpecialAbility() {
        arrowsCount = arrowsCount + ARROWS_BONUS*level;
    }

    @Override
    public void castAbility() {
        if(arrowsCount != 0){
            Enemy closestEnemy = null;
            List<Enemy> enemies = enemiesInRangeCallBack.getEnemies(range);
            for(Enemy enemy : enemies){
                double distanceEnemy =  getPosition().range(enemy.getPosition());
                if(closestEnemy == null || getPosition().range(closestEnemy.getPosition()) > distanceEnemy) {
                    closestEnemy = enemy;
                }
            }

            if(closestEnemy!=null){
                arrowsCount = arrowsCount - 1;
                messageCallBack.send(String.format("%s fired an arrow at %s.", getName(), closestEnemy.getName()));
                int defensePoints = closestEnemy.defense();
                int actualAttack = Math.max(0, attack - defensePoints);
                messageCallBack.send(String.format("%s hit %s for %d ability damage.",getName(), closestEnemy.getName(), actualAttack));
                closestEnemy.getHealth().ReduceAmount(actualAttack);
                if(!closestEnemy.alive()){
                    closestEnemy.onDeath();
                    messageCallBack.send(String.format("%s died. %s gained %d experience.", closestEnemy.getName(), getName(), closestEnemy.getExperienceValue()));
                    addExperience(closestEnemy.getExperienceValue());
                }

            }else{
                messageCallBack.send(String.format("%s tried to shoot an arrow but there were no enemies in range.", getName()));
            }

        }else{
            onGameTick();
            messageCallBack.send(String.format("%s tried to shoot an arrow, but there was no arrows left.", getName()));
        }
    }

    @Override
    public String describe(){
        return String.format("%s\t\tHealth: %s\t\tAttack: %d\t\tDefense: %d\t\tLevel: %d\t\tExperience: %d/%d\t\tArrows: %d\t\tRange: %d", getName(), getHealth(), getAttack(), getDefense(),
                getLevel(), getExperience(),getLevel()*REQ_EXP, arrowsCount, range);
    }

}
