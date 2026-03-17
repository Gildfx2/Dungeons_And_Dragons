package Tiles.Players;

import Utils.Resource;
import Tiles.Enemies.Enemy;

import java.util.List;
import java.util.Random;

public class Warrior extends Player {
    private int abilityCoolDown;
    private int remainingCoolDown;
    private static final int WARRIOR_ATTACK_BONUS=2;
    private static final int WARRIOR_DEFENSE_BONUS=1;
    private static final int WARRIOR_HEALTH_BONUS=5;
    private static final int WARRIOR_ABILITY_BONUS=10;
    private static final int WARRIOR_ABILITY_DAMAGE_PERCENTAGE=10;
    private static final int WARRIOR_SPECIAL_ABILITY_RANGE=3;


    public Warrior(String name, int healthCapacity, int attack, int defense, int abilityCoolDown ) {
        super(null, name, new Resource(healthCapacity,healthCapacity), attack, defense);
        this.abilityCoolDown = abilityCoolDown;
        remainingCoolDown = 0;
        specialAbilityName = "Avenger's Shield";
    }


    @Override
    public void castAbility() {
        if(remainingCoolDown == 0){
            int healthBonus = gainAbilityHealth();
            messageCallBack.send(String.format("%s used %s, healing for: %d.", getName(), specialAbilityName, healthBonus));
            this.health.AddAmount(healthBonus);
            List<Enemy> possibleEnemies = enemiesInRangeCallBack.getEnemies(WARRIOR_SPECIAL_ABILITY_RANGE);
            if(!possibleEnemies.isEmpty()){
                Random randomEnemy = new Random();
                int enemyIndex = randomEnemy.nextInt(possibleEnemies.size());
                Enemy selectedEnemy = possibleEnemies.get(enemyIndex);
                int attackPoints = getHealth().getCapacity()/WARRIOR_ABILITY_DAMAGE_PERCENTAGE;
                int defensePoints = selectedEnemy.defense();
                int actualAttack = Math.max(0, attackPoints - defensePoints);
                messageCallBack.send(String.format("%s hit %s for %d ability damage.",getName(), selectedEnemy.getName(), actualAttack));
                selectedEnemy.getHealth().ReduceAmount(actualAttack);
                if(!selectedEnemy.alive()){
                    selectedEnemy.onDeath();
                    messageCallBack.send(String.format("%s died. %s gained %d experience.", selectedEnemy.getName(), getName(), selectedEnemy.getExperienceValue()));
                    addExperience(selectedEnemy.getExperienceValue());
                }
            }
            remainingCoolDown = abilityCoolDown;
        }
        else{
            onGameTick();
            messageCallBack.send(String.format("%s tried to cast %s, but there is a cooldown: %d", getName(), specialAbilityName, getRemainingCoolDown()));
        }
    }


    @Override
    public void onGameTick(){
        this.remainingCoolDown=Math.max(this.remainingCoolDown-1,0);
    }

    protected int gainHealth(){
        return level*(WARRIOR_HEALTH_BONUS+HEALTH_BONUS);
    }
    protected int gainAttack(){
        return level*(WARRIOR_ATTACK_BONUS+ATTACK_BONUS) ;
    }
    protected int gainDefense(){
        return level*(WARRIOR_DEFENSE_BONUS+DEFENSE_BONUS);
    }

    @Override
    public void gainSpecialAbility(){
        remainingCoolDown=0;
    }

    protected int gainAbilityHealth(){
        return defense*WARRIOR_ABILITY_BONUS;
    }

    public int getRemainingCoolDown() {
        return remainingCoolDown;
    }

    public int getAbilityCoolDown() {
        return abilityCoolDown;
    }

    @Override
    public String describe(){
        return String.format("%s\t\tHealth: %s\t\tAttack: %d\t\tDefense: %d\t\tLevel: %d\t\tExperience: %d/%d\t\tCooldown: %d/%d", getName(), getHealth(), getAttack(),
                getDefense(), getLevel(), getExperience(),getLevel()*REQ_EXP, getRemainingCoolDown(), getAbilityCoolDown());
    }


}
