package dnaqr.evoluringCore.settings;

public class GameSettings {

    public BoardSettings getBoardSettings() {
        return new BoardSettings();
    }

    public EnergyCostSettings getCostSetting() {
        return new EnergyCostSettings();
    }

    public FoodAddingSettings getFoodAddingSettings() {
        return new FoodAddingSettings();
    }

    public CellGenerationSettings getCellGenerationSettings() {
        return new CellGenerationSettings();
    }
    public RenderSettings getRenderSettings() { return new RenderSettings(); }
}
