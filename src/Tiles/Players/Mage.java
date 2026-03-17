package Tiles.Players;

import Utils.Resource;
import Tiles.Enemies.Enemy;

import java.util.List;
import java.util.Random;

public class Mage extends Player {
    private static final int MAGE_MANA_CAPACITY_BONUS=25;
    private static final int MAGE_MANA_AMOUNT_BONUS=4;
    private static final int MAGE_SPELL_POWER_BONUS=10;



    private Resource mana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;



    public Mage(String name, int healthCapacity, int attack, int defense, int manaCapacity, int manaCost, int spellPower, int hitsCount , int  abilityRange) {
        super(null, name, new Resource(healthCapacity,healthCapacity), attack, defense);
        this.mana = new Resource(manaCapacity/4,manaCapacity);
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;
        specialAbilityName = "Blizzard";
    }

    @Override
    public void levelUp(){
        super.levelUp();
        messageCallBack.send(String.format("\t\t+%d Maximum Mana, +%d Spell Power",MAGE_MANA_CAPACITY_BONUS*level, MAGE_SPELL_POWER_BONUS*level));
    }


    @Override
    public void onGameTick(){
        this.mana.AddAmount(level);
    }

    @Override
    public void gainSpecialAbility() {
        mana.AddCapacity(MAGE_MANA_CAPACITY_BONUS*level);
        mana.AddAmount(mana.getCapacity()/MAGE_MANA_AMOUNT_BONUS);
        spellPower += MAGE_SPELL_POWER_BONUS*level;
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public void castAbility(){
        if(manaCost<=this.mana.getAmount()) {
            messageCallBack.send(String.format("%s cast %s.", getName(), specialAbilityName));
            this.mana.ReduceAmount(manaCost);
            int hits = 0;
            List<Enemy> possibleEnemies = enemiesInRangeCallBack.getEnemies(abilityRange);
            while (hits < hitsCount && !possibleEnemies.isEmpty()) {
                Random randomEnemy = new Random();
                int enemyIndex = randomEnemy.nextInt(possibleEnemies.size());
                Enemy selectedEnemy = possibleEnemies.get(enemyIndex);
                int attackPoints = this.spellPower;
                int defensePoints = selectedEnemy.defense();
                int actualAttack = Math.max(0,attackPoints -defensePoints);
                selectedEnemy.getHealth().ReduceAmount(actualAttack);
                messageCallBack.send(String.format("%s hit %s for %d ability damage.",getName(), selectedEnemy.getName(), actualAttack));

                if(!selectedEnemy.alive()){
                    possibleEnemies.remove(selectedEnemy);
                    selectedEnemy.onDeath();
                    messageCallBack.send(String.format("%s died. %s gained %d experience.", selectedEnemy.getName(), getName(), selectedEnemy.getExperienceValue()));
                    addExperience(selectedEnemy.getExperienceValue());
                }
                hits++;
            }
        }
        else{
            onGameTick();
            messageCallBack.send(String.format("%s tried to cast %s, but there was not enough mana: %d/%d",getName(),specialAbilityName, getMana().getAmount(),getManaCost()));
        }

    }

    public Resource getMana() {
        return mana;
    }

    public int getSpellPower() {
        return spellPower;
    }

    public String describe(){
        return String.format("%s\t\tHealth: %s\t\tAttack: %d\t\tDefense: %d\t\tLevel: %d\t\tExperience: %d/%d\t\tMana: %d/%d\t\tSpell Power: %d", getName(), getHealth(), getAttack(), getDefense(), getLevel(), getExperience(),getLevel()*REQ_EXP, getMana().getAmount(), getMana().getCapacity(),getSpellPower());
    }

}
