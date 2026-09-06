package com.family.rhymes;

public final class GameData {
    private GameData() {}
    public static final class Pair {
        public final String left; public final String right; public final String leftAsset; public final String rightAsset;
        public Pair(String left, String right, String leftAsset, String rightAsset) { this.left=left; this.right=right; this.leftAsset=leftAsset; this.rightAsset=rightAsset; }
    }
    public static final Pair[][] LEVELS = new Pair[][] {
        new Pair[] {
            new Pair("конь", "огонь", "cards/card_018.jpg", "cards/card_004.jpg"),
            new Pair("рубашка", "ромашка", "cards/card_006.jpg", "cards/card_012.jpg"),
            new Pair("корова", "подкова", "cards/card_000.jpg", "cards/card_016.jpg"),
            new Pair("щенок", "чеснок", "cards/card_008.jpg", "cards/card_011.jpg"),
        },
        new Pair[] {
            new Pair("компот", "бегемот", "cards/card_019.jpg", "cards/card_015.jpg"),
            new Pair("самокат", "акробат", "cards/card_002.jpg", "cards/card_010.jpg"),
            new Pair("ель", "карамель", "cards/card_013.jpg", "cards/card_009.jpg"),
            new Pair("клубника", "черника", "cards/card_003.jpg", "cards/card_014.jpg"),
        },
        new Pair[] {
            new Pair("пипетка", "салфетка", "cards/card_017.jpg", "cards/card_005.jpg"),
            new Pair("глаз", "алмаз", "cards/card_001.jpg", "cards/card_007.jpg"),
            new Pair("рак", "мак", "cards/card_021.jpg", "cards/card_031.jpg"),
            new Pair("лягушка", "подушка", "cards/card_035.jpg", "cards/card_025.jpg"),
        },
        new Pair[] {
            new Pair("машина", "корзина", "cards/card_020.jpg", "cards/card_032.jpg"),
            new Pair("сапог", "пирог", "cards/card_030.jpg", "cards/card_039.jpg"),
            new Pair("пилот", "живот", "cards/card_028.jpg", "cards/card_033.jpg"),
            new Pair("халат", "салат", "cards/card_037.jpg", "cards/card_023.jpg"),
        },
        new Pair[] {
            new Pair("календарь", "фонарь", "cards/card_022.jpg", "cards/card_024.jpg"),
            new Pair("пшеница", "синица", "cards/card_029.jpg", "cards/card_038.jpg"),
            new Pair("букет", "пакет", "cards/card_034.jpg", "cards/card_027.jpg"),
            new Pair("водолаз", "дикобраз", "cards/card_026.jpg", "cards/card_036.jpg"),
        },
        new Pair[] {
            new Pair("кот", "рот", "cards/card_041.jpg", "cards/card_049.jpg"),
            new Pair("кукушка", "погремушка", "cards/card_052.jpg", "cards/card_047.jpg"),
            new Pair("картина", "витрина", "cards/card_040.jpg", "cards/card_054.jpg"),
            new Pair("барсук", "сундук", "cards/card_048.jpg", "cards/card_045.jpg"),
        },
        new Pair[] {
            new Pair("бык", "язык", "cards/card_055.jpg", "cards/card_059.jpg"),
            new Pair("канат", "гранат", "cards/card_058.jpg", "cards/card_042.jpg"),
            new Pair("олень", "ремень", "cards/card_056.jpg", "cards/card_050.jpg"),
            new Pair("удав", "рукав", "cards/card_046.jpg", "cards/card_057.jpg"),
        },
        new Pair[] {
            new Pair("жилет", "билет", "cards/card_043.jpg", "cards/card_053.jpg"),
            new Pair("самолёт", "вертолёт", "cards/card_051.jpg", "cards/card_044.jpg"),
            new Pair("дом", "сом", "cards/card_073.jpg", "cards/card_061.jpg"),
            new Pair("булка", "шкатулка", "cards/card_074.jpg", "cards/card_070.jpg"),
        },
        new Pair[] {
            new Pair("пружина", "паутина", "cards/card_062.jpg", "cards/card_065.jpg"),
            new Pair("петух", "пастух", "cards/card_077.jpg", "cards/card_067.jpg"),
            new Pair("зуб", "дуб", "cards/card_060.jpg", "cards/card_078.jpg"),
            new Pair("нос", "пылесос", "cards/card_079.jpg", "cards/card_068.jpg"),
        },
        new Pair[] {
            new Pair("пень", "тюлень", "cards/card_066.jpg", "cards/card_076.jpg"),
            new Pair("шлем", "крем", "cards/card_072.jpg", "cards/card_075.jpg"),
            new Pair("рулет", "табурет", "cards/card_069.jpg", "cards/card_063.jpg"),
            new Pair("мёд", "лёд", "cards/card_064.jpg", "cards/card_071.jpg"),
        },
        new Pair[] {
            new Pair("жук", "лук", "cards/card_082.jpg", "cards/card_089.jpg"),
            new Pair("свечка", "печка", "cards/card_093.jpg", "cards/card_090.jpg"),
            new Pair("скелет", "браслет", "cards/card_094.jpg", "cards/card_083.jpg"),
            new Pair("телефон", "микрофон", "cards/card_091.jpg", "cards/card_080.jpg"),
        },
        new Pair[] {
            new Pair("кит", "щит", "cards/card_085.jpg", "cards/card_099.jpg"),
            new Pair("кокос", "матрос", "cards/card_096.jpg", "cards/card_088.jpg"),
            new Pair("костюм", "изюм", "cards/card_095.jpg", "cards/card_081.jpg"),
            new Pair("пробка", "коробка", "cards/card_097.jpg", "cards/card_087.jpg"),
        },
        new Pair[] {
            new Pair("белка", "тарелка", "cards/card_086.jpg", "cards/card_098.jpg"),
            new Pair("парашют", "салют", "cards/card_092.jpg", "cards/card_084.jpg"),
            new Pair("ёж", "нож", "cards/card_115.jpg", "cards/card_105.jpg"),
            new Pair("лейка", "наклейка", "cards/card_111.jpg", "cards/card_117.jpg"),
        },
        new Pair[] {
            new Pair("малина", "балерина", "cards/card_104.jpg", "cards/card_108.jpg"),
            new Pair("слон", "балкон", "cards/card_100.jpg", "cards/card_109.jpg"),
            new Pair("жираф", "шкаф", "cards/card_106.jpg", "cards/card_112.jpg"),
            new Pair("глобус", "автобус", "cards/card_118.jpg", "cards/card_103.jpg"),
        },
        new Pair[] {
            new Pair("кость", "трость", "cards/card_102.jpg", "cards/card_110.jpg"),
            new Pair("вилка", "копилка", "cards/card_116.jpg", "cards/card_107.jpg"),
            new Pair("губка", "юбка", "cards/card_101.jpg", "cards/card_114.jpg"),
            new Pair("карандаш", "шалаш", "cards/card_113.jpg", "cards/card_119.jpg"),
        },
        new Pair[] {
            new Pair("мяч", "врач", "cards/card_131.jpg", "cards/card_127.jpg"),
            new Pair("чайка", "гайка", "cards/card_123.jpg", "cards/card_128.jpg"),
            new Pair("ракета", "конфета", "cards/card_137.jpg", "cards/card_120.jpg"),
            new Pair("лимон", "вагон", "cards/card_122.jpg", "cards/card_134.jpg"),
        },
        new Pair[] {
            new Pair("мост", "хвост", "cards/card_124.jpg", "cards/card_133.jpg"),
            new Pair("топор", "мухомор", "cards/card_132.jpg", "cards/card_138.jpg"),
            new Pair("соль", "фасоль", "cards/card_136.jpg", "cards/card_126.jpg"),
            new Pair("холодильник", "будильник", "cards/card_125.jpg", "cards/card_135.jpg"),
        },
        new Pair[] {
            new Pair("клетка", "таблетка", "cards/card_139.jpg", "cards/card_130.jpg"),
            new Pair("меч", "печь", "cards/card_121.jpg", "cards/card_129.jpg"),
            new Pair("шар", "комар", "cards/card_148.jpg", "cards/card_140.jpg"),
            new Pair("палка", "галка", "cards/card_156.jpg", "cards/card_149.jpg"),
        },
        new Pair[] {
            new Pair("монета", "котлета", "cards/card_152.jpg", "cards/card_142.jpg"),
            new Pair("дракон", "флакон", "cards/card_155.jpg", "cards/card_145.jpg"),
            new Pair("медведь", "лебедь", "cards/card_144.jpg", "cards/card_153.jpg"),
            new Pair("забор", "помидор", "cards/card_158.jpg", "cards/card_146.jpg"),
        },
        new Pair[] {
            new Pair("педаль", "медаль", "cards/card_159.jpg", "cards/card_143.jpg"),
            new Pair("дневник", "грузовик", "cards/card_151.jpg", "cards/card_157.jpg"),
            new Pair("сетка", "рулетка", "cards/card_147.jpg", "cards/card_150.jpg"),
            new Pair("кирпич", "кулич", "cards/card_141.jpg", "cards/card_154.jpg"),
        },
        new Pair[] {
            new Pair("банан", "стакан", "cards/card_160.jpg", "cards/card_175.jpg"),
            new Pair("пушка", "кружка", "cards/card_171.jpg", "cards/card_165.jpg"),
            new Pair("газета", "карета", "cards/card_172.jpg", "cards/card_163.jpg"),
            new Pair("воробей", "клей", "cards/card_179.jpg", "cards/card_174.jpg"),
        },
        new Pair[] {
            new Pair("крот", "плот", "cards/card_164.jpg", "cards/card_173.jpg"),
            new Pair("ковёр", "бобёр", "cards/card_177.jpg", "cards/card_168.jpg"),
            new Pair("трамвай", "попугай", "cards/card_162.jpg", "cards/card_169.jpg"),
            new Pair("телёнок", "цыплёнок", "cards/card_178.jpg", "cards/card_166.jpg"),
        },
        new Pair[] {
            new Pair("копейка", "линейка", "cards/card_176.jpg", "cards/card_167.jpg"),
            new Pair("ключ", "луч", "cards/card_170.jpg", "cards/card_161.jpg"),
            new Pair("кран", "баран", "cards/card_182.jpg", "cards/card_189.jpg"),
            new Pair("шишка", "крышка", "cards/card_191.jpg", "cards/card_198.jpg"),
        },
        new Pair[] {
            new Pair("корона", "ворона", "cards/card_192.jpg", "cards/card_180.jpg"),
            new Pair("котёнок", "утёнок", "cards/card_184.jpg", "cards/card_188.jpg"),
            new Pair("енот", "блокнот", "cards/card_196.jpg", "cards/card_199.jpg"),
            new Pair("сарай", "каравай", "cards/card_195.jpg", "cards/card_185.jpg"),
        },
        new Pair[] {
            new Pair("муравей", "ручей", "cards/card_183.jpg", "cards/card_190.jpg"),
            new Pair("жеребёнок", "поросёнок", "cards/card_197.jpg", "cards/card_194.jpg"),
            new Pair("индейка", "канарейка", "cards/card_186.jpg", "cards/card_193.jpg"),
            new Pair("ананас", "матрас", "cards/card_187.jpg", "cards/card_181.jpg"),
        },
    };
}
