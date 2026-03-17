package Tiles.Players;

import Utils.Resource;
import Tiles.Enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

public class Rogue extends Player {
    private int cost;
    private int currentEnergy;
    private static final int ROUGE_ATTACK_BONUS=3;
    private static final int ROUGE_MAX_ENERGY=100;
    private static final int ROUGE_ENERGY_BONUS=10;
    private static final int ROUGE_SPECIAL_ABILITY_RANGE=2;

    public Rogue(String name, int healthCapacity, int attack, int defense, int cost) {
        super(null , name, new Resource(healthCapacity,healthCapacity), attack, defense);
        this.cost = cost;
        this.currentEnergy = ROUGE_MAX_ENERGY;
        specialAbilityName = "Fan of Knives";
    }

    @Override
    public void onGameTick(){
        this.currentEnergy= Math.min(currentEnergy+ROUGE_ENERGY_BONUS,ROUGE_MAX_ENERGY);
    }

    @Override
    public void gainSpecialAbility() {
            currentEnergy = ROUGE_MAX_ENERGY;
    }

    protected int gainAttack(){
        return level*(ROUGE_ATTACK_BONUS+ATTACK_BONUS) ;
    }

    public int getCost() {
        return cost;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    @Override
    public void castAbility() {
        if(currentEnergy>=cost){
            currentEnergy = currentEnergy - cost;
            messageCallBack.send(String.format("%s cast %s.", getName(), specialAbilityName));
            List<Enemy> enemies = enemiesInRangeCallBack.getEnemies(ROUGE_SPECIAL_ABILITY_RANGE);
            List<Enemy> deadEnemies = new ArrayList<>();
            for(Enemy enemy : enemies){
                int defensePoints = enemy.defense();
                int actualAttack = Math.max(attack - defensePoints,0);
                messageCallBack.send(String.format("%s hit %s for %d ability damage.",getName(), enemy.getName(), actualAttack));
                enemy.getHealth().ReduceAmount(actualAttack);
                if(!enemy.alive()){
                    deadEnemies.add(enemy);
                    messageCallBack.send(String.format("%s died. %s gained %d experience.", enemy.getName(), getName(), enemy.getExperienceValue()));
                    addExperience(enemy.getExperienceValue());
                }
            }
            for(Enemy deadEnemy : deadEnemies){
                deadEnemy.onDeath();
            }
        }
        else{
            onGameTick();
            messageCallBack.send(String.format("%s tried to cast %s, but there was not enough energy: %d/%d", getName(), specialAbilityName, getCurrentEnergy(), getCost()));
        }
    }

    @Override
    public String describe(){
        return String.format("%s\t\tHealth: %s\t\tAttack: %d\t\tDefense: %d\t\tLevel: %d\t\tExperience: %d/%d\t\tEnergy: %d/%d", getName(), getHealth(), getAttack(), getDefense(),
                getLevel(), getExperience(),getLevel()*REQ_EXP, getCurrentEnergy(), ROUGE_MAX_ENERGY);
    }

}
