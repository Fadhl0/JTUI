package utils;

public enum Colors {

    /* Primary colors */
    White("#ffffff"),
    Black("#000000"),
    
    Gray100("#e9eaeb"),
    Gray200("#d5d7da"),
    Gray300("#a4a7ae"),
    Gray400("#717680"),
    Gray500("#535862"),
    Gray600("#414651"),

    Error100("#fecdc9"),
    Error200("#fda19b"),
    Error300("#f97066"),
    Error400("#f04438"),
    Error500("#d92d20"),
    Error600("#b32318"),

    Warning100("#fedf89"),
    Warning200("#fec84b"),
    Warning300("#fdb022"),
    Warning400("#f79009"),
    Warning500("#dc6803"),
    Warning600("#b54708"),

    Success100("#a9efc5"),
    Success200("#75e0a7"),
    Success300("#47cd89"),
    Success400("#17b26a"),
    Success500("#079455"),
    Success600("#067647"),

    /* Secondary colors */
    Yellow100("#feee95"),
    Yellow200("#fde172"),
    Yellow300("#fac415"),
    Yellow400("#eaaa08"),
    Yellow500("#ca8504"),
    Yellow600("#a15c07"),

    Orange100("#f9dbaf"),
    Orange200("#f7b27a"),
    Orange300("#f38744"),
    Orange400("#ef6820"),
    Orange500("#e04f16"),
    Orange600("#b93815"),

    Orangedark100("#ffd6ae"),
    Orangedark200("#ff9c66"),
    Orangedark300("#ff692e"),
    Orangedark400("#ff4405"),
    Orangedark500("#e62e05"),
    Orangedark600("#bc1b06"),

    Rose100("#feccd6"),
    Rose200("#fea3b4"),
    Rose300("#fd6f8e"),
    Rose400("#f63d68"),
    Rose500("#e31b53"),
    Rose600("#c01048"),

    Pink100("#fcceee"),
    Pink200("#faa7e0"),
    Pink300("#f670c7"),
    Pink400("#ee46bc"),
    Pink500("#dd2590"),
    Pink600("#c01574"),

    Fuchsia100("#f6d0fe"),
    Fuchsia200("#eeaafd"),
    Fuchsia300("#e478fa"),
    Fuchsia400("#d444f1"),
    Fuchsia500("#ba24d5"),
    Fuchsia600("#9f1ab1"),

    Purple100("#d9d6fe"),
    Purple200("#bdb4fe"),
    Purple300("#9b8afb"),
    Purple400("#7a5af8"),
    Purple500("#6938ef"),
    Purple600("#5925dc"),

    Violet100("#ddd6fe"),
    Violet200("#c3b4fd"),
    Violet300("#a48afb"),
    Violet400("#875bf7"),
    Violet500("#7839ee"),
    Violet600("#6927da"),

    Indigo100("#c6d7fe"),
    Indigo200("#a4bcfd"),
    Indigo300("#8098f9"),
    Indigo400("#6172f3"),
    Indigo500("#444ce7"),
    Indigo600("#3538cd"),

    Bluedark100("#b2ccff"),
    Bluedark200("#84adff"),
    Bluedark300("#528bff"),
    Bluedark400("#2970ff"),
    Bluedark500("#155eef"),
    Bluedark600("#004eeb"),

    Blue100("#b2ddff"),
    Blue200("#84caff"),
    Blue300("#53b1fd"),
    Blue400("#2e90fa"),
    Blue500("#1570ef"),
    Blue600("#175cd3"),

    Bluelight100("#b9e6fe"),
    Bluelight200("#7cd4fd"),
    Bluelight300("#36bffa"),
    Bluelight400("#0ba5ec"),
    Bluelight500("#0086c9"),
    Bluelight600("#026aa2"),

    Cyan100("#a5f0fc"),
    Cyan200("#67e3f9"),
    Cyan300("#22ccee"),
    Cyan400("#06aed4"),
    Cyan500("#088ab2"),
    Cyan600("#0e6f90"),

    Teal100("#99f6e0"),
    Teal200("#5fe9d0"),
    Teal300("#2ed3b7"),
    Teal400("#15b79e"),
    Teal500("#0e9384"),
    Teal600("#107569"),

    Green100("#aaf0c4"),
    Green200("#73e2a3"),
    Green300("#3ccb7f"),
    Green400("#16b364"),
    Green500("#099250"),
    Green600("#087443"),

    Greenlight100("#d0f8aa"),
    Greenlight200("#a6ef67"),
    Greenlight300("#85e139"),
    Greenlight400("#66c61c"),
    Greenlight500("#4ba30d"),
    Greenlight600("#3b7c0f"),

    Moss100("#ceeab0"),
    Moss200("#acdb79"),
    Moss300("#86cb3c"),
    Moss400("#669f2a"),
    Moss500("#4f7a21"),
    Moss600("#3f621a"),

    Graywarm100("#e7e5e4"),
    Graywarm200("#d7d3d0"),
    Graywarm300("#a8a29d"),
    Graywarm400("#79716b"),
    Graywarm500("#57534e"),
    Graywarm600("#44403c"),

    Graymodern100("#e3e8ef"),
    Graymodern200("#cdd5df"),
    Graymodern300("#9aa3b2"),
    Graymodern400("#697586"),
    Graymodern500("#4b5565"),
    Graymodern600("#364152"),

    Grayblue100("#d5d9eb"),
    Grayblue200("#b3b8db"),
    Grayblue300("#717bbc"),
    Grayblue400("#4e5ba6"),
    Grayblue500("#3e4784"),
    Grayblue600("#363f72");


    private String color;
    private Colors (String color) {
        this.color = color;
    }

    // public String getColor() {
    //   return this.color;
    // }

    public static String setColor(String hexadesimal) {
        return hexadesimal;
    }

    public String getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.color;
    }
}