package dnaqr.evoluringCore;

import dnaqr.evoluringCore.settings.BoardSettings;
import dnaqr.evoluringCore.settings.EnergyCostSettings;
import dnaqr.evoluringCore.settings.GameSettings;
import javafx.scene.paint.Color;

import java.util.*;

public class Cell {
    public String name;
    public Integer generationNumber;
    public Integer energy;
    public Integer energyCost;
    public DNA dna;
    public Integer attack;
    public Integer defence;
    public Coordinates coordinates;
    public Cell parentCell;
    public Cell childCell;
    public Color color;
    public Map<String, CellActions.CellActionsNames> actionMap = new HashMap<>();

    GameSettings gameSettings = new GameSettings();
    BoardSettings boardSettings = gameSettings.getBoardSettings();
    EnergyCostSettings energyCostSettings = gameSettings.getCostSetting();

    public Cell(String name, Integer generationNumber, Integer energy, Coordinates coordinates, DNA dna, Color color) {
        this.name = name;
        this.generationNumber = generationNumber;
        this.energy = energy;
        this.coordinates = coordinates;
        this.dna = dna;
        this.color = color;
        getCountDnaGenesByTypeAndEnergyCost();
    }

    public CellActions.CellActionsNames getNextAction() {
        char[] dnaCommands = dna.dnaCode.toCharArray();

        int commandOverflow = dnaCommands.length;
        if (dna.dnaCursor + 1 == commandOverflow) {
            dna.dnaCursor = -1;
        }
        char nextActionInDNA = dnaCommands[dna.dnaCursor + 1];
        dna.dnaCursor++;
        return Main.actionMap.get(String.valueOf(nextActionInDNA));
    }

    public Cell generateChild(int squareSize) {
        int rangeX = 1;
        int rangeY = 1;
        this.energy -= this.energy/2;
        Cell newCell = new Cell(this.name, this.generationNumber++, this.energy/2, new Coordinates(this.coordinates.x + rangeX * squareSize, this.coordinates.y + rangeY * squareSize), new DNA(this.dnaGeneration(this.dna.dnaCode), 0), this.color);
        newCell.parentCell = this;
        this.childCell = newCell;
        return newCell;
    }

    public boolean checkIfDeath() {
        return this.energy < 0;
    }

    public String dnaGeneration(String dnaCode) {
        int countOfGenesToDeleting = 1;
        List<String> geneList = Main.actionMap.keySet().stream().toList();
        String genesToAdding = "";
        Random rand = new Random();
        int countOfGenesToAdding = rand.nextInt(1, 5);
        if (countOfGenesToAdding > 1) {
            countOfGenesToDeleting = rand.nextInt(1, countOfGenesToAdding);
        }
        for (int i = 0; i < countOfGenesToAdding; i++) {
            int nextGene = rand.nextInt(0, geneList.size());
            genesToAdding += geneList.get(nextGene);
        }
        String genesAfterAdding = dnaCode + genesToAdding;
        String result = "";
        for (int j = 0; j < countOfGenesToDeleting; j++) {
            result = "";
            int index = rand.nextInt(genesAfterAdding.length() - 1);
            char[] genesAfterAddingArray = genesAfterAdding.toCharArray();
            for (int i = 0; i < genesAfterAddingArray.length; i++) {
                if (i != index) {
                    result += genesAfterAddingArray[i];
                }
            }
            genesAfterAdding = result;
        }
        /*
        for (int i = 0; i < countOfGenesToAdding; i++) {
            int nextGene = rand.nextInt(0, geneList.size());
            genesToAdding += geneList.get(nextGene);
        }
        String genesAfterAdding = dnaCode + genesToAdding;
        String result = "";
        Map<Integer, Boolean> deletedGenesInIndexMap = new HashMap<>();
        for (int j = 0; j < countOfGenesToDeleting; j++) {
            Integer index = rand.nextInt(genesAfterAdding.length() - 1);
            deletedGenesInIndexMap.put(index, true);
        }
        result = "";
        char[] genesAfterAddingArray = genesAfterAdding.toCharArray();
        for(int i = 0; i < genesAfterAdding.length()-1; i++) {
            if(deletedGenesInIndexMap.get(i) == null) {
                result += genesAfterAddingArray[i];
            }
        }
        */
        if(result.length() > 30) {
            result = result.substring(0, 30);
        }
        return result;
    }

    public void getCountDnaGenesByTypeAndEnergyCost() { //плата за сложность днк
        char[] chars = this.dna.dnaCode.toCharArray();
        Map<String, Integer> dnaCodeCountByName = new HashMap<>();
        for (char aChar : chars) {
            dnaCodeCountByName.merge(String.valueOf(aChar), 1, Integer::sum);
        }
        this.attack = Optional.ofNullable(dnaCodeCountByName.get("f")).orElse(0);
        this.defence = Optional.ofNullable(dnaCodeCountByName.get("g")).orElse(0);
        int doNothingLength = Optional.ofNullable(dnaCodeCountByName.get("o")).orElse(0);
        int moveLeftLength = Optional.ofNullable(dnaCodeCountByName.get("a")).orElse(0);
        int moveRightLength = Optional.ofNullable(dnaCodeCountByName.get("b")).orElse(0);
        int moveUpLength = Optional.ofNullable(dnaCodeCountByName.get("c")).orElse(0);
        int moveDownLength = Optional.ofNullable(dnaCodeCountByName.get("d")).orElse(0);
        int eatCloseLength = Optional.ofNullable(dnaCodeCountByName.get("e")).orElse(0);
        this.energyCost = this.attack * energyCostSettings.attackPassiveCost
                + this.defence * energyCostSettings.defencePassiveCost
                + doNothingLength * energyCostSettings.doNothingPassiveCost
                + moveLeftLength * energyCostSettings.moveLeftPassive
                + moveRightLength * energyCostSettings.moveRightPassive
                + moveUpLength * energyCostSettings.moveUpPassive
                + moveDownLength * energyCostSettings.moveDownPassive
                + eatCloseLength * energyCostSettings.eatCloseFoodPassive;
    }

    public Square getDimensionSquare(String dimension){
        int coordinateX = 0;
        int coordinateY = 0;
        Square result = null;
        switch (dimension){
            case "right" -> {
                coordinateX = this.coordinates.x + boardSettings.getSquareSize();
                coordinateY = this.coordinates.y;
                coordinateX = validateX(coordinateX)/boardSettings.getSquareSize();
                coordinateY = validateY(coordinateY)/boardSettings.getSquareSize();
                result = Main.squares.get(Main.mapSquareCoordinatesToIndex.get((String.valueOf(coordinateX)) + "|" + (String.valueOf(coordinateY))));
            }
        }
        return result;
    }

    public int validateX(int x){
        int width = boardSettings.getWidth();
        int squareSize = boardSettings.getSquareSize();
        if(x < 0){
            x = (width-1)*(squareSize);
        }
        if(x >= width*squareSize){
            x = 0;
        }
        return x;
    }

    public int validateY(int y){
        int height = boardSettings.getHeight();
        int squareSize = boardSettings.getSquareSize();
        if(y < 0){
            y = (height-1)*(squareSize);
        }
        if(y >= height*squareSize){
            y = 0;
        }
        return y;
    }
}
