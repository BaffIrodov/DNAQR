package dnaqr.evoluringCore.settings;

public class FoodAddingSettings {
    public int freeFoodAddingRate = 4*65; // в тик на карте появляются столько квадратов free-еды
    public int closeFoodAddingRate = 4*65; // в тик на карте появляются столько квадратов close-еды
    public int freeEatAddingByEveryTick = 55; // количество добавляемой free-энергии за тик
    public int closeEatAddingByEveryTick = 55; // количество добавляемой close-энергии за тик
    public int freeEatAddingByDeath = 100; // количество free-энергии от "трупа" клетки
}
