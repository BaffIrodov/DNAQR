package dnaqr.evoluringCore.settings;

public class CellGenerationSettings {
    public boolean generateByFood = true; // рождение новых клеток по количеству еды
    public boolean generateByGene = false; // рождение новых клеток по гену
    public int costOfGenerationByFood = 300; // стоимость рождения по еде в её количестве
}
