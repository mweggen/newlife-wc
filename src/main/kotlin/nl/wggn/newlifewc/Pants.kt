package nl.wggn.newlifewc

class Pants(type: List<OutfitType>, removeDiff: Int, colour: Colour, attractive: Int, cute: Int, elegant: Int,
            shortDesc: String, basePrice: Int, override val basicDesc: String, override val length: Length, override val detailDesc: String) :
        Bottom, AbstractClothing(type, removeDiff, colour, attractive, cute, elegant, listOf(WhereWorn.BOTTOM), shortDesc, basePrice) {
    override val skirt = false
}

fun createWorkTrousers(): Pants {
    val colour = when (rnd10()) {
        in 0..4 -> Colour.BLACK
        5 -> Colour.BROWN
        6, 7 -> Colour.GREY
        else -> Colour.WHITE
    }
    val basicDesc = "work trousers"
    val shortDesc = colour.desc + ' ' + basicDesc
    val length = Length.ANKLES
    return Pants(listOf(OutfitType.BUSINESS), 0, colour, 0, 0, 1, shortDesc,
            35, basicDesc, length, getPantsDetail(shortDesc, length, listOf()))
}

fun createYogaPants(style: Style): Pants {
    val length = Length.ANKLES
    val flags = listOf(Flag.CLINGY, Flag.PULL_DOWN, Flag.BOOST_ATHLETIC_ELEGANCE)
    val st = "yoga pants"
    val colour = getAthleticLongPantsColour(style)
    val cute = if (colour == Colour.PINK) 1 else 0
    val shortDesc = colour.desc + " " + st
    val detailDesc = getPantsDetail(shortDesc, length, flags) + "\nFlexible tight-fitting trousers designed for the practice of yoga, but that can also be worn casually. "
    val pants = Pants(athOrCas, 0, colour, 4, cute, -5, shortDesc, 20, st, Length.ANKLES, detailDesc)
    flags.forEach(pants::addFlag)
    return pants
}

fun createTracksuitBottoms(style: Style): Pants {
    val flags = listOf(Flag.PULL_DOWN)
    val length = Length.ANKLES
    val colour = getAthleticLongPantsColour(style)
    val st = "tracksuit bottoms"
    val cute = if (colour == Colour.PINK) 1 else 0
    val shortDesc = colour.desc + " " + st
    val detailDesc = getPantsDetail(shortDesc, length, flags) + "\n" +
            "Also known as sweatpants in some countries. These are soft, cheap, rather unflattering and very casual trousers that can be worn to work out or just casually. "
    val pants = Pants(athOrCas, 0, colour, -2, cute, -8, shortDesc, 8, st, length, detailDesc)
    flags.forEach(pants::addFlag)
    return pants
}

fun createBikeShorts(style: Style): Pants {
    val length = Length.THIGH
    val flags = listOf(Flag.CLINGY, Flag.PULL_DOWN)
    val basicDesc = "bike shorts"
    val colour: Colour = getPantsColour(style)
    val shortDesc = colour.desc + " " + basicDesc
    val detailDesc = getPantsDetail(shortDesc, length, flags) + "\nSkin-tight shorts used for exercise. "
    val pants = Pants(listOf(OutfitType.ATHLETIC), 0, colour, 2, -3, -3, shortDesc, 30, basicDesc, length, detailDesc)
    flags.forEach(pants::addFlag)
    return pants
}

fun getPantsColour(style: Style): Colour =
        when (style) {
            Style.CUTE -> when (rnd10()) {
                0, 1 -> Colour.BLUE
                2 -> Colour.BLACK
                3 -> Colour.WHITE
                in 4..8 -> Colour.PINK
                else -> Colour.GREY
            }
            Style.SCRUFFY -> when (rnd10()) {
                in 0..5 -> Colour.BLUE
                6, 7 -> Colour.BLACK
                else -> Colour.GREY
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                in 0..3 -> Colour.BLUE
                4, 5, 6 -> Colour.BLACK
                7 -> Colour.WHITE
                8 -> Colour.RED
                else -> Colour.GREY
            }
            else -> when (rnd10()) {
                in 0..3 -> Colour.BLUE
                4, 5, 6 -> Colour.BLACK
                7 -> Colour.WHITE
                8 -> Colour.PINK
                else -> Colour.GREY
            }
        }

fun getAthleticLongPantsColour(style: Style): Colour =
        when (style) {
            Style.CHEERFUL -> when (rnd10()) {
                0, 1, 2 -> Colour.RED
                3 -> Colour.GREEN
                4, 5 -> Colour.ORANGE
                6 -> Colour.BLUE
                else -> Colour.YELLOW
            }
            Style.BUSINESSLIKE -> when (rnd10()) {
                in 0..4 -> Colour.BLACK
                5 -> Colour.BROWN
                6 -> Colour.GREY
                7, 8 -> Colour.BLUE
                else -> Colour.WHITE
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                in 0..5 -> Colour.BLACK
                6, 7, 8 -> Colour.RED
                else -> Colour.BLUE
            }
            else -> when (rnd10()) {
                in 0..4 -> Colour.BLACK
                5 -> Colour.BROWN
                6 -> Colour.GREY
                7 -> Colour.BLUE
                8 -> Colour.PINK
                else -> Colour.WHITE
            }
        }

fun getPantsDetail(shortDesc: String, length: Length, flags: List<Flag>): String =
        "A pair of $shortDesc" +
                (if (flags.contains(Flag.CLINGY)) " that cling tightly to your bottom" else "") + ".\n" +
                when (length) {
                    Length.THIGH -> "They're short, leaving your legs bare up to the upper thigh"
                    Length.ANKLES -> "They cover your legs all the way down to your ankles"
                    else -> "They cover your legs down to about halfway"
                } + ".\n" +
                (if (flags.contains(Flag.PULL_DOWN))
                    "They can be removed by simply pulling them down, no need to fiddle with a zip first"
                else "They fasten with a zip at the front") + ". "