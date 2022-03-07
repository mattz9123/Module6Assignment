package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;

public class DiceGame {
    private final List<Player> players;
    private final List<Die> dice;
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame (int countPlayers, int countDice, int maxRolls){
        players = new ArrayList<>();
        for (int i = 0; i < countPlayers; i++) {
            players.add(new Player());
        }

        dice = new ArrayList<>();
        for (int i = 0; i < countDice; i++) {
            dice.add(new Die(6));
        }

        if (countPlayers < 2) {
            throw new IllegalArgumentException();
        }

        this.maxRolls = maxRolls;
    }

    private boolean allDiceHeld () {
        return dice.stream().allMatch(Die::isBeingHeld);
        // dice.stream().allMatch(die -> die.isBeingHeld())
    }

    public boolean autoHold(int faceValue) {
        return dice.stream().anyMatch(die -> die.getFaceValue() == faceValue && !die.isBeingHeld());
    }

    public boolean currentPlayerCanRoll() {
        return maxRolls - currentPlayer.getRollsUsed() > 0 && !allDiceHeld();
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
        return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return dice.stream().map(Die::toString).collect(Collectors.joining());
    }

    public String getFinalWinner() {
        return Collections.max(players, Comparator.comparingInt(Player::getWins)).toString();
    }

    public String getGameResults() {
        players.sort(Comparator.comparingInt(Player::getScore).reversed());
//        players.forEach(Player::addWin);
        int i = 0;
        for (Player player : players) {
            if (i == 0) {
                player.addWin();
            }
            else {
                player.addLoss();
            }
            i++;
        }
        return players.stream().map(Player::toString).collect(Collectors.joining());
    }

    private boolean isHoldingDie(int faceValue){
        return dice.stream().anyMatch(die -> die.getFaceValue() == faceValue && die.isBeingHeld());
    }

    public boolean nextPlayer() {
        for (Player player : players) {
            if (player.getPlayerNumber() == currentPlayer.getPlayerNumber() + 1) {
                return true;
            }
        }
        return false;
    }

    public void playerHold(char dieNum){
        Optional<Die> optionalDie =  dice.stream().filter(die -> die.getDieNum() == dieNum).findFirst();
        optionalDie.ifPresent(Die::holdDie);

    }

    public void resetDice() {
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers() {
        players.forEach(Player::resetPlayer);
    }

    public void rollDice() {
        System.out.println(getDiceResults());
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer() {
        List<Integer> values = dice.stream().map(Die::getFaceValue).collect(Collectors.toList());

        if (values.contains(6) && values.contains(5) && values.contains(4)){
            int score = -15;
            for (int value : values) {
                score += value;
            }
            currentPlayer.setScore(score + getCurrentPlayerScore());
        }
    }

    public void startNewGame() {
        currentPlayer = players.get(0);
        resetPlayers();
    }
}

/* Five six-sided die
*  Each player rolls three die, highest score goes first
*  Player has up to three rolls
*  Must get a 6 (ship), 5 (captain), and 4 (crew) in that order or simultaneously
*  Remaining two die will be added to final score (cargo)
*  Remaining two die can be rerolled if there is a roll left (have to roll both)
*   */
