package com.family.rhymes;

public final class GameData {
  private GameData() {}
  public static final class CardDef {
    public final String word; public final int pairId;
    public CardDef(String word, int pairId) { this.word=word; this.pairId=pairId; }
  }
  public static final String[][] PAIR_SPEECH = new String[][] {
    new String[] {"конь — огонь", "рубашка — ромашка", "корова — подкова", "щенок — чеснок"},
    new String[] {"компот — бегемот", "самокат — акробат", "ель — карамель", "клубника — черника"},
    new String[] {"пипетка — салфетка", "глаз — алмаз", "рак — мак", "лягушка — подушка"},
    new String[] {"машина — корзина", "сапог — пирог", "пилот — живот", "халат — салат"},
    new String[] {"календарь — фонарь", "пшеница — синица", "букет — пакет", "водолаз — дикобраз"},
    new String[] {"кот — рот", "кукушка — погремушка", "картина — витрина", "барсук — сундук"},
    new String[] {"бык — язык", "канат — гранат", "олень — ремень", "удав — рукав"},
    new String[] {"жилет — билет", "самолёт — вертолёт", "дом — сом", "булка — шкатулка"},
    new String[] {"пружина — паутина", "петух — пастух", "зуб — дуб", "нос — пылесос"},
    new String[] {"пень — тюлень", "шлем — крем", "рулет — табурет", "мёд — лёд"},
    new String[] {"жук — лук", "свечка — печка", "скелет — браслет", "телефон — микрофон"},
    new String[] {"кит — щит", "кокос — матрос", "костюм — изюм", "пробка — коробка"},
    new String[] {"белка — тарелка", "парашют — салют", "ёж — нож", "лейка — наклейка"},
    new String[] {"малина — балерина", "слон — балкон", "жираф — шкаф", "глобус — автобус"},
    new String[] {"кость — трость", "вилка — копилка", "губка — юбка", "карандаш — шалаш"},
    new String[] {"мяч — врач", "чайка — гайка", "ракета — конфета", "лимон — вагон"},
    new String[] {"мост — хвост", "топор — мухомор", "соль — фасоль", "холодильник — будильник"},
    new String[] {"клетка — таблетка", "меч — печь", "шар — комар", "палка — галка"},
    new String[] {"монета — котлета", "дракон — флакон", "медведь — лебедь", "забор — помидор"},
    new String[] {"педаль — медаль", "дневник — грузовик", "сетка — рулетка", "кирпич — кулич"},
    new String[] {"банан — стакан", "пушка — кружка", "газета — карета", "воробей — клей"},
    new String[] {"крот — плот", "ковёр — бобёр", "трамвай — попугай", "телёнок — цыплёнок"},
    new String[] {"копейка — линейка", "ключ — луч", "кран — баран", "шишка — крышка"},
    new String[] {"корона — ворона", "котёнок — утёнок", "енот — блокнот", "сарай — каравай"},
    new String[] {"муравей — ручей", "жеребёнок — поросёнок", "индейка — канарейка", "ананас — матрас"},
  };
  public static final CardDef[][] LEVELS = new CardDef[][] {
    new CardDef[] {new CardDef("ромашка", 1), new CardDef("огонь", 0), new CardDef("конь", 0), new CardDef("рубашка", 1), new CardDef("чеснок", 3), new CardDef("подкова", 2), new CardDef("корова", 2), new CardDef("щенок", 3)},
    new CardDef[] {new CardDef("акробат", 1), new CardDef("черника", 3), new CardDef("клубника", 3), new CardDef("бегемот", 0), new CardDef("компот", 0), new CardDef("карамель", 2), new CardDef("ель", 2), new CardDef("самокат", 1)},
    new CardDef[] {new CardDef("мак", 2), new CardDef("глаз", 1), new CardDef("лягушка", 3), new CardDef("салфетка", 0), new CardDef("пипетка", 0), new CardDef("рак", 2), new CardDef("алмаз", 1), new CardDef("подушка", 3)},
    new CardDef[] {new CardDef("пилот", 2), new CardDef("сапог", 1), new CardDef("корзина", 0), new CardDef("халат", 3), new CardDef("пирог", 1), new CardDef("машина", 0), new CardDef("салат", 3), new CardDef("живот", 2)},
    new CardDef[] {new CardDef("пшеница", 1), new CardDef("календарь", 0), new CardDef("букет", 2), new CardDef("дикобраз", 3), new CardDef("водолаз", 3), new CardDef("пакет", 2), new CardDef("фонарь", 0), new CardDef("синица", 1)},
    new CardDef[] {new CardDef("кукушка", 1), new CardDef("сундук", 3), new CardDef("кот", 0), new CardDef("витрина", 2), new CardDef("картина", 2), new CardDef("погремушка", 1), new CardDef("барсук", 3), new CardDef("рот", 0)},
    new CardDef[] {new CardDef("язык", 0), new CardDef("удав", 3), new CardDef("гранат", 1), new CardDef("бык", 0), new CardDef("ремень", 2), new CardDef("канат", 1), new CardDef("рукав", 3), new CardDef("олень", 2)},
    new CardDef[] {new CardDef("вертолёт", 1), new CardDef("билет", 0), new CardDef("шкатулка", 3), new CardDef("сом", 2), new CardDef("дом", 2), new CardDef("самолёт", 1), new CardDef("жилет", 0), new CardDef("булка", 3)},
    new CardDef[] {new CardDef("зуб", 2), new CardDef("паутина", 0), new CardDef("пружина", 0), new CardDef("дуб", 2), new CardDef("пылесос", 3), new CardDef("петух", 1), new CardDef("пастух", 1), new CardDef("нос", 3)},
    new CardDef[] {new CardDef("табурет", 2), new CardDef("крем", 1), new CardDef("лёд", 3), new CardDef("тюлень", 0), new CardDef("пень", 0), new CardDef("рулет", 2), new CardDef("шлем", 1), new CardDef("мёд", 3)},
    new CardDef[] {new CardDef("браслет", 2), new CardDef("свечка", 1), new CardDef("телефон", 3), new CardDef("скелет", 2), new CardDef("печка", 1), new CardDef("жук", 0), new CardDef("лук", 0), new CardDef("микрофон", 3)},
    new CardDef[] {new CardDef("матрос", 1), new CardDef("пробка", 3), new CardDef("костюм", 2), new CardDef("кит", 0), new CardDef("щит", 0), new CardDef("кокос", 1), new CardDef("коробка", 3), new CardDef("изюм", 2)},
    new CardDef[] {new CardDef("лейка", 3), new CardDef("парашют", 1), new CardDef("нож", 2), new CardDef("тарелка", 0), new CardDef("белка", 0), new CardDef("ёж", 2), new CardDef("салют", 1), new CardDef("наклейка", 3)},
    new CardDef[] {new CardDef("жираф", 2), new CardDef("глобус", 3), new CardDef("балкон", 1), new CardDef("балерина", 0), new CardDef("автобус", 3), new CardDef("шкаф", 2), new CardDef("малина", 0), new CardDef("слон", 1)},
    new CardDef[] {new CardDef("копилка", 1), new CardDef("кость", 0), new CardDef("карандаш", 3), new CardDef("вилка", 1), new CardDef("трость", 0), new CardDef("юбка", 2), new CardDef("губка", 2), new CardDef("шалаш", 3)},
    new CardDef[] {new CardDef("мяч", 0), new CardDef("ракета", 2), new CardDef("вагон", 3), new CardDef("чайка", 1), new CardDef("конфета", 2), new CardDef("врач", 0), new CardDef("гайка", 1), new CardDef("лимон", 3)},
    new CardDef[] {new CardDef("мухомор", 1), new CardDef("будильник", 3), new CardDef("соль", 2), new CardDef("мост", 0), new CardDef("хвост", 0), new CardDef("фасоль", 2), new CardDef("холодильник", 3), new CardDef("топор", 1)},
    new CardDef[] {new CardDef("клетка", 0), new CardDef("палка", 3), new CardDef("комар", 2), new CardDef("таблетка", 0), new CardDef("печь", 1), new CardDef("шар", 2), new CardDef("галка", 3), new CardDef("меч", 1)},
    new CardDef[] {new CardDef("котлета", 0), new CardDef("лебедь", 2), new CardDef("медведь", 2), new CardDef("помидор", 3), new CardDef("дракон", 1), new CardDef("монета", 0), new CardDef("забор", 3), new CardDef("флакон", 1)},
    new CardDef[] {new CardDef("рулетка", 2), new CardDef("медаль", 0), new CardDef("дневник", 1), new CardDef("кирпич", 3), new CardDef("педаль", 0), new CardDef("грузовик", 1), new CardDef("кулич", 3), new CardDef("сетка", 2)},
    new CardDef[] {new CardDef("карета", 2), new CardDef("стакан", 0), new CardDef("пушка", 1), new CardDef("воробей", 3), new CardDef("банан", 0), new CardDef("газета", 2), new CardDef("клей", 3), new CardDef("кружка", 1)},
    new CardDef[] {new CardDef("плот", 0), new CardDef("бобёр", 1), new CardDef("ковёр", 1), new CardDef("трамвай", 2), new CardDef("телёнок", 3), new CardDef("крот", 0), new CardDef("попугай", 2), new CardDef("цыплёнок", 3)},
    new CardDef[] {new CardDef("баран", 2), new CardDef("крышка", 3), new CardDef("луч", 1), new CardDef("копейка", 0), new CardDef("линейка", 0), new CardDef("кран", 2), new CardDef("шишка", 3), new CardDef("ключ", 1)},
    new CardDef[] {new CardDef("корона", 0), new CardDef("котёнок", 1), new CardDef("утёнок", 1), new CardDef("ворона", 0), new CardDef("енот", 2), new CardDef("сарай", 3), new CardDef("каравай", 3), new CardDef("блокнот", 2)},
    new CardDef[] {new CardDef("индейка", 2), new CardDef("муравей", 0), new CardDef("жеребёнок", 1), new CardDef("канарейка", 2), new CardDef("ручей", 0), new CardDef("ананас", 3), new CardDef("матрас", 3), new CardDef("поросёнок", 1)},
  };
}
