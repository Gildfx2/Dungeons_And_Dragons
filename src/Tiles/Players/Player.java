package Tiles.Players;

import Callbacks.EnemiesInRangeCallBack;
import CLI.InputQuery;
import Utils.Action;
import Utils.Resource;
import Utils.Position;
import Tiles.Empty;
import Tiles.Enemies.Enemy;
import Tiles.HeroicUnit;
import Tiles.Unit;
import Tiles.Wall;
import VisitorPattern.Visitor;

public abstract class Player extends Unit implements HeroicUnit {
    public static final char playerTile = '@';
    protected static final int REQ_EXP=50;
    protected static final int ATTACK_BONUS=4;
    protected static final int DEFENSE_BONUS=1;
    protected static final int HEALTH_BONUS=10;



    public int getExperience() {
        return experience;
    }


    public int getLevel() {
        return level;
    }

    public String getSpecialAbilityName(){
        return specialAbilityName;
    }

    protected String specialAbilityName;
    protected int experience;
    protected int level;


    protected InputQuery inputProvider;
    protected EnemiesInRangeCallBack enemiesInRangeCallBack;

    public Player(Position position, String name, Resource health, int attack, int defense){
        super(playerTile, position, name, health, attack, defense);
        this.experience=0;
        this.level=1;
    }

    public void setInputQuery(InputQuery inputProvider)
    {
        this.inputProvider = inputProvider;
    }


    /// Abstract
    public abstract void onGameTick();

    public abstract void gainSpecialAbility();



    protected void addExperience(int experienceGained) {
           this.experience += experienceGained;
        int nextLevelReq = levelUpRequirement();

        while (experience >= nextLevelReq) {
            levelUp();
            experience -= nextLevelReq;
            nextLevelReq = levelUpRequirement();
        }
    }

    public void levelUp(){
        level++;
        int healthGained = gainHealth();
        int attackGained = gainAttack();
        int defenseGained = gainDefense();
        health.AddCapacity(healthGained);
        health.restore();
        attack+=attackGained;
        defense+=defenseGained;
        gainSpecialAbility();
        messageCallBack.send(String.format("%s reached level %d: +%d Health, +%d Attack, +%d Defense",getName(),getLevel(),healthGained,attackGained,defenseGained));
    }

    protected int gainHealth(){
        return level*HEALTH_BONUS;
    }
    protected int gainAttack(){
        return level*ATTACK_BONUS;
    }
    protected int gainDefense(){
        return level*DEFENSE_BONUS;
    }
    protected int levelUpRequirement(){
        return level*REQ_EXP;
    }


    public Position performAction() {
        Action action = inputProvider.getInput();
        Position newPosition = position;

        if (action == Action.SPECIAL_ABILITY)
            castAbility();
        else {
            if (action == Action.RIGHT)
                newPosition = newPosition.add(1, 0);
            else if (action == Action.LEFT)
                newPosition = newPosition.add(-1, 0);
            else if (action == Action.UP)
                newPosition = newPosition.add(0, -1);
            else if (action == Action.DOWN)
                newPosition = newPosition.add(0, 1);
            onGameTick();
        }

        return newPosition;
    }

    /// Visitor Pattern
    public void visit(Enemy enemy){
        battle(enemy);
        if(!enemy.alive()){
            swapPosition(enemy);
            enemy.onDeath();
            addExperience(enemy.getExperienceValue());
            messageCallBack.send(String.format("%s died. %s gained %d experience.", enemy.getName(), getName(), enemy.getExperienceValue()));
        }
    }
    public void visit(Empty tile){
        swapPosition(tile);
    }
    public void visit(Wall wall){}
    public void visit(Player player){}
    public void accept(Visitor v){v.visit(this);}


    public void setEnemiesInRangeCallBack(EnemiesInRangeCallBack enemiesInRangeCallBack) {
        this.enemiesInRangeCallBack = enemiesInRangeCallBack;
    }

    @Override
    public String toString()
    {
        return alive() ? super.toString() : "X";
    }
}
