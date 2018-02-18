package nl.wggn.newlifewc

import java.util.concurrent.ThreadLocalRandom

class Dress(type: List<OutfitType>, colour: Colour, val length: Length, val topType: TopType, attractive: Int, cute: Int,
            elegant: Int, shortDesc: String, override val basicDesc: String, shortType: String, private val detailComment: String, basePrice: Int) :
        AbstractClothing(type, topType.undoDiff, colour, attractive, cute, elegant, listOf(WhereWorn.TOP, WhereWorn.BOTTOM), shortDesc, basePrice) {
    override val detailDesc: String
        get() = "A " + when (length) {
            Length.THIGH -> "short "
            Length.ANKLES -> "long "
            else -> ""
        } + (if (hasFlag(Flag.CLINGY)) "clingy " else "") +
                (if (hasFlag(Flag.SEE_THROUGH)) "nearly transparent " else "") +
                (if (hasFlag(Flag.LOW_CUT)) "low-cut " else "") + colourDesc + when (topType) {
            TopType.HALTERTOP -> " halter-neck"
            TopType.STRAPLESS -> " strapless"
            TopType.STRAPPY -> " strappy"
            else -> ""
        } + " " + basicDesc + when (topType) {
            TopType.BUTTONS -> " that buttons down the front. "
            TopType.ZIP -> " that zips down the back. "
            else -> ". "
        } +
                (if (hasFlag(Flag.THIN)) "The material is particularly thin. " else "") + when (length) {
            Length.THIGH -> "It's very short, leaving your legs bare up to the upper thigh. "
            Length.ANKLES -> "It's a long dress, covering your legs all the way down to your ankles. Classy! "
            else -> "It comes down to just above your knees. "
        } +
                (if (topType == TopType.HALTERTOP || topType == TopType.STRAPLESS) "\n" +
                        "Fashion dictates that you can't wear a bra with this kind of " + basicDesc + ". " else "") +
                "\n" + detailComment
}

fun createFlamencoDress(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?, topType: TopType?, flagsParam: Map<Flag, Boolean>): Dress {
    var attractive = 10
    var cute = 0
    var elegant = 8
    val flags = mutableListOf(Flag.SINGULAR, Flag.DANCE)
    val shortType = "traje de flamenco"
    val detailDesc = "A long dress worn by flamenco dancers. It was a prize for coming second in the dance tournament" +
            ", and wearing it reminds you of your success and fills you with confidence. It might also impress the" +
            " judges in any future competitions. "
    val colour = colourParam ?: when (rnd10()) {
        0, 1 -> Colour.WHITE
        2 -> Colour.BLACK
        3, 4, 5 -> Colour.RED
        6, 7 -> Colour.YELLOW
        8 -> Colour.PURPLE
        else -> Colour.GREEN
    }
    val random = ThreadLocalRandom.current()
    if (flagsParam.getOrDefault(Flag.LOW_CUT, random.nextInt(3) == 0)) {
        flags.add(Flag.LOW_CUT)
        attractive += 1
        cute -= 2
        elegant -= 2
    }
    if (flagsParam.getOrDefault(Flag.THIN, random.nextBoolean())) {
        flags.add(Flag.THIN)
        attractive += 1
        cute -= 1
    }
    if (flagsParam.getOrDefault(Flag.CLINGY, random.nextBoolean())) {
        flags.add(Flag.CLINGY)
        attractive += 1
        cute -= 2
    }
    val shortDesc = (if (flags.contains(Flag.THIN)) "thin " else "") + colour.desc + " " + shortType
    val dress = Dress(listOf(OutfitType.GOING_OUT, OutfitType.FORMAL), colour, Length.ANKLES, TopType.ZIP, attractive, cute,
            elegant, shortDesc, "flamenco dress", shortType, detailDesc, 50)
    flags.forEach(dress::addFlag)
    return dress
}

fun createBabydoll(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?, topType: TopType?, flagsParam: Map<Flag, Boolean>): Dress =
        when (variantParam ?: if (rnd10() < when (style) {
                    Style.CUTE -> 10
                    Style.PROVOCATIVE -> 0
                    Style.ELEGANT -> 2
                    Style.WHOLESOME -> 7
                    Style.CHEERFUL -> 5
                    else -> 4
                }) Variant.CUTE_BABYDOLL else Variant.BABYDOLL) {
            Variant.CUTE_BABYDOLL -> createCuteBabydoll(colourParam, topType)
            else -> createBabydoll(style, colourParam, topType, flagsParam)
        }

fun createBabydoll(style: Style, colourParam: Colour?, topTypeParam: TopType?, flagsParam: Map<Flag, Boolean>): Dress {
    val flags = mutableListOf(Flag.SINGULAR, Flag.THIN)
    var attractive = 8
    var cute = -3
    var elegant = 0
    val topType = topTypeParam ?: if (rnd10() < 7) TopType.STRAPPY else TopType.HALTERTOP
    val colour = colourParam ?: when (style) {
        Style.PROVOCATIVE -> when (rnd10()) {
            in 0..4 -> Colour.BLACK
            else -> Colour.RED
        }
        Style.CUTE -> when (rnd10()) {
            in 0..3 -> Colour.WHITE
            in 4..7 -> Colour.PINK
            8 -> Colour.BLACK
            else -> Colour.BLUE
        }
        Style.CHEERFUL -> when (rnd10()) {
            0, 1, 2 -> Colour.YELLOW
            3 -> Colour.PINK
            in 4..7 -> Colour.RED
            else -> Colour.BLUE
        }
        else -> when (rnd10()) {
            0, 1 -> Colour.WHITE
            2, 3 -> Colour.PINK
            in 4..7 -> Colour.BLACK
            8 -> Colour.RED
            else -> Colour.BLUE
        }
    }

    when (colour) {
        Colour.WHITE -> cute += 2
        Colour.PINK -> cute += 3
        Colour.RED -> cute -= 1
        Colour.YELLOW -> elegant -= 4
    }

    if (flagsParam.getOrDefault(Flag.LOW_CUT, rnd10() < when (style) {
                Style.PROVOCATIVE -> 8
                Style.WHOLESOME, Style.CUTE -> 2
                else -> 5
            })) {
        flags.add(Flag.LOW_CUT)
        attractive += 1
        cute -= 2
    }

    if (flagsParam.getOrDefault(Flag.SEE_THROUGH, rnd10() < when (style) {
                Style.PROVOCATIVE -> 7
                Style.WHOLESOME -> 0
                Style.CUTE -> 1
                else -> 5
            })) {
        flags.add(Flag.SEE_THROUGH)
        attractive += 2
        cute -= 4
    }

    val detailComment = "A babydoll is a short loose-fitting negligee intended to be worn as titillating bedroom attire. "
    val shortType = "babydoll"
    val shortDesc = (if (flags.contains(Flag.SEE_THROUGH)) "transparent " else "") +
            (if (flags.contains(Flag.LOW_CUT)) "lowcut " else "") + colour.desc + " " + shortType

    val dress = Dress(listOf(OutfitType.SEXY_NIGHTWEAR), colour, Length.THIGH, topType, attractive, cute, elegant, shortDesc, shortType, shortType, detailComment, 30)
    flags.forEach(dress::addFlag)
    return dress
}

fun createCuteBabydoll(colourParam: Colour?, topTypeParam: TopType?): Dress {
    val flags = mutableListOf(Flag.SINGULAR, Flag.THIN)
    val topType = topTypeParam ?: if (rnd10() < 7) TopType.STRAPPY else TopType.HALTERTOP
    val attractive = 8
    var cute = 5
    val colour = colourParam ?: when (rnd10()) {
        in 0..3 -> Colour.WHITE
        in 4..7 -> {
            cute += 1
            Colour.PINK
        }
        8 -> Colour.YELLOW
        else -> Colour.PURPLE
    }
    val shortType = "babydoll"
    val shortDesc = "cute " + (if (flags.contains(Flag.SEE_THROUGH)) "transparent " else "") +
            (if (flags.contains(Flag.LOW_CUT)) "lowcut " else "") + colour.desc + " " + shortType
    val detailComment = "A more 'cute' version of the babydoll, this one is decorated with girly touches like bows " +
            "and ribbons. It's still very revealing. "
    val dress = Dress(listOf(OutfitType.SEXY_NIGHTWEAR), colour, Length.THIGH, topType, attractive, cute, 0, shortDesc, shortType, shortType, detailComment, 30)
    flags.forEach(dress::addFlag)
    return dress
}

fun createSummerDress(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?, topTypeParam: TopType?, flagsParam: Map<Flag, Boolean>): Dress {
    val flags = mutableListOf(Flag.SINGULAR, Flag.THIN)
    var attractive = 6
    var cute = 2
    var elegant = 3
    val length = lengthParam ?: getDressLength(style)
    val st = "summer dress"
    val detailDesc = "A lightweight loose dress that you could wear out in the day"
    val topType = topTypeParam ?: when (style) {
        Style.CUTE -> when (rnd10()) {
            in 0..5 -> TopType.STRAPPY
            6 -> TopType.ZIP
            else -> TopType.BUTTONS
        }
        Style.PROVOCATIVE -> when (rnd10()) {
            in 0..5 -> TopType.STRAPPY
            6 -> TopType.ZIP
            else -> TopType.HALTERTOP
        }
        Style.ELEGANT -> when (rnd10()) {
            in 0..5 -> TopType.STRAPPY
            else -> TopType.ZIP
        }
        else -> when (rnd10()) {
            in 0..5 -> TopType.STRAPPY
            6 -> TopType.ZIP
            7, 8 -> TopType.BUTTONS
            else -> TopType.HALTERTOP
        }
    }

    if (topType == TopType.ZIP) elegant += 2
    else if (topType == TopType.BUTTONS) {
        cute += 2
        elegant -= 1
    } else if (topType == TopType.HALTERTOP) cute -= 1

    val colour = colourParam ?: when (style) {
        Style.CUTE -> when (rnd10()) {
            in 0..3 -> Colour.WHITE
            in 5..8 -> Colour.PINK
            else -> Colour.YELLOW
        }
        Style.CHEERFUL -> when (rnd10()) {
            0, 1 -> Colour.RED
            2 -> Colour.GREEN
            3 -> Colour.PURPLE
            4, 5, 6 -> Colour.YELLOW
            7, 8 -> Colour.ORANGE
            else -> Colour.BLUE
        }
        Style.ELEGANT -> when (rnd10()) {
            0, 1, 2 -> Colour.WHITE
            3 -> Colour.CREAM
            4 -> Colour.BLACK
            5 -> Colour.GREEN
            6 -> Colour.YELLOW
            7 -> Colour.PURPLE
            else -> Colour.BLUE
        }
        Style.PROVOCATIVE -> when (rnd10()) {
            0 -> Colour.CREAM
            1, 2, 3 -> Colour.RED
            4, 5 -> Colour.BLUE
            6, 7 -> Colour.GREEN
            else -> Colour.YELLOW
        }
        Style.BUSINESSLIKE -> when (rnd10()) {
            0, 1 -> Colour.WHITE
            2 -> Colour.BLACK
            3 -> Colour.BROWN
            4 -> Colour.GREY
            5, 6 -> Colour.GREEN
            7 -> Colour.PURPLE
            else -> Colour.BLUE
        }
        else -> when (rnd10()) {
            0, 1, 2 -> Colour.WHITE
            3 -> Colour.PURPLE
            4 -> Colour.PINK
            5 -> Colour.RED
            6 -> Colour.BLUE
            7 -> Colour.GREEN
            else -> Colour.YELLOW
        }
    }

    if (colour == Colour.WHITE) cute += 1
    else if (colour == Colour.PINK) cute += 3

    if (flagsParam.getOrDefault(Flag.LOW_CUT, style != Style.CUTE && style != Style.WHOLESOME &&
                    topType != TopType.HALTERTOP && topType != TopType.STRAPLESS &&
                    rnd10() < when (style) {
                Style.PROVOCATIVE -> 9
                Style.BUSINESSLIKE -> 4
                else -> 5
            })) {
        flags += Flag.LOW_CUT
        attractive += 1
        cute -= 3
        elegant -= 3
    }

    val shortDesc = colour.desc + " " + st

    val price = 35 + (if (Flag.CLINGY in flags) 5 else 0) + if (length == Length.ANKLES) 5 else 0

    val dress = Dress(listOf(OutfitType.CASUAL), colour, length, topType, attractive, cute, elegant, shortDesc, "dress", st, detailDesc, price)
    flags.forEach(dress::addFlag)
    return dress
}

fun getDressLength(style: Style): Length =
        when (style) {
            Style.ELEGANT -> when (rnd10()) {
                0 -> Length.THIGH
                in 1..4 -> Length.KNEES
                else -> Length.ANKLES
            }
            Style.PROVOCATIVE -> Length.THIGH
            Style.WHOLESOME -> when (rnd10()) {
                in 0..5 -> Length.KNEES
                else -> Length.ANKLES
            }
            Style.BUSINESSLIKE -> when (rnd10()) {
                0, 1 -> Length.THIGH
                in 2..7 -> Length.KNEES
                else -> Length.ANKLES
            }
            else -> when (rnd10()) {
                in 0..3 -> Length.THIGH
                in 4..7 -> Length.KNEES
                else -> Length.ANKLES
            }
        }

fun createSlip(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?, topTypeParam: TopType?, flagsParam: Map<Flag, Boolean>): Dress {
    val flags = mutableListOf(Flag.SINGULAR, Flag.THIN)
    val topType = topTypeParam ?: if (rnd10() < 8) TopType.STRAPPY else TopType.HALTERTOP
    var attractive = 8
    var cute = -1
    var elegant = 5
    val colour = colourParam ?: when (style) {
        Style.PROVOCATIVE -> when (rnd10()) {
            0 -> Colour.CREAM
            1, 2, 3 -> Colour.BLACK
            4, 5, 6 -> Colour.RED
            7, 8 -> Colour.PURPLE
            else -> Colour.BLUE
        }
        Style.CUTE -> when (rnd10()) {
            0, 1, 2 -> Colour.WHITE
            in 3..6 -> Colour.PINK
            7 -> Colour.YELLOW //todo PURPLE?
            else -> Colour.BLUE
        }
        Style.CHEERFUL -> when (rnd10()) {
            0, 1, 2 -> Colour.YELLOW
            3 -> Colour.PINK
            4, 5, 6 -> Colour.RED
            7 -> Colour.PURPLE
            8 -> Colour.GREEN
            else -> Colour.BLUE
        }
        else -> when (rnd10()) {
            0 -> Colour.WHITE
            1 -> Colour.CREAM
            2 -> Colour.PINK
            3, 4 -> Colour.BLACK
            5 -> Colour.RED
            6 -> Colour.GREEN
            7 -> Colour.PURPLE
            8 -> Colour.YELLOW
            else -> Colour.BLUE
        }
    }

    when (colour) {
        Colour.PINK -> cute += 3
        Colour.WHITE -> cute += 1
        Colour.RED, Colour.BLACK -> cute -= 1
        Colour.CREAM -> elegant += 1
        Colour.YELLOW -> elegant -= 1
    }

    val basicDesc = "slip"
    val shortType = "satin " + basicDesc
    val shortDesc = (if (flagsParam.getOrDefault(Flag.LOW_CUT, topType != TopType.HALTERTOP && topType != TopType.STRAPLESS &&
                    rnd10() < when (style) {
                Style.PROVOCATIVE -> 8
                Style.WHOLESOME, Style.CUTE -> 2
                else -> 5
            })) {
        flags += Flag.LOW_CUT
        attractive += 1
        cute -= 2
        "lowcut "
    } else "") + colour.desc + " " + shortType

    val detailAppend = "Made of smooth satin with titillating lace trim, this is an alluring and sensual piece of clothing. "

    val dress = Dress(listOf(OutfitType.SEXY_NIGHTWEAR), colour, Length.THIGH, topType, attractive, cute, elegant,
            shortDesc, basicDesc, shortType, detailAppend, 40)
    flags.forEach(dress::addFlag)
    return dress
}

fun createDress(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?,
                topTypeParam: TopType?, flagsParam: Map<Flag, Boolean>): Dress {
    val flags = mutableListOf(Flag.SINGULAR)
    var attractive = 6
    var cute = 1
    var elegant = 2
    val length = lengthParam ?: getDressLength(style)
    if (length == Length.THIGH) {
        attractive += 2
        cute -= 3
        elegant -= 2
    } else if (length == Length.ANKLES) {
        elegant += 4
    }
    val variant = variantParam ?: when (length) {
        Length.THIGH -> when (style) {
            Style.CUTE -> when (rnd10()) {
                in 0..5 -> Variant.SKATER_DRESS
                6, 7, 8 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.WHOLESOME -> when (rnd10()) {
                in 0..3 -> Variant.SKATER_DRESS
                4, 5, 6 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                in 0..6 -> Variant.BODYCON_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.BUSINESSLIKE, Style.ELEGANT -> when (rnd10()) {
                in 0..4 -> Variant.SKATER_DRESS
                5, 6, 7 -> Variant.BODYCON_DRESS
                else -> Variant.SLIP_DRESS
            }
            else -> when (rnd10()) {
                0, 1, 2 -> Variant.SKATER_DRESS
                3, 4, 5 -> Variant.BODYCON_DRESS
                6, 7 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
        }
        Length.KNEES -> when (style) {
            Style.BUSINESSLIKE -> when (rnd10()) {
                in 0..5 -> Variant.SHIFT_DRESS
                6, 7 -> Variant.SHEATH_DRESS
                8 -> Variant.SKATER_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.ELEGANT -> when (rnd10()) {
                0, 1, 2 -> Variant.SHIFT_DRESS
                3, 4, 5 -> Variant.SHEATH_DRESS
                6 -> Variant.SKATER_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.SCRUFFY -> when (rnd10()) {
                0, 1, 2 -> Variant.SKATER_DRESS
                3, 4 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.CHEERFUL -> when (rnd10()) {
                in 0..4 -> Variant.SKATER_DRESS
                5, 6, 7 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.CUTE -> when (rnd10()) {
                0 -> Variant.SHIFT_DRESS
                1 -> Variant.SHEATH_DRESS
                2, 3 -> Variant.PINAFORE_DRESS
                in 4..7 -> Variant.SKATER_DRESS
                else -> Variant.SLIP_DRESS
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                0, 1, 2 -> Variant.SHIFT_DRESS
                in 3..7 -> Variant.SHEATH_DRESS
                else -> Variant.SLIP_DRESS
            }
            else -> when (rnd10()) {
                0, 1 -> Variant.SHIFT_DRESS
                2, 3 -> Variant.SHEATH_DRESS
                4, 5 -> Variant.SKATER_DRESS
                6, 7 -> Variant.PINAFORE_DRESS
                else -> Variant.SLIP_DRESS
            }
        }
        else -> Variant.MAXI_DRESS
    }
    val st = when (variant) {
        Variant.SKATER_DRESS -> "skater dress"
        Variant.BODYCON_DRESS -> "bodycon dress"
        Variant.PINAFORE_DRESS -> "pinafore dress"
        Variant.SLIP_DRESS -> "slip dress"
        Variant.SHIFT_DRESS -> "shift dress"
        Variant.SHEATH_DRESS -> "sheath dress"
        else /*Variant.MAXI_DRESS*/ -> "maxi dress"
    }
    val detailDesc = when (variant) {
        Variant.SKATER_DRESS -> "Skater dresses are a cute and feminine style. This one is only really suitable for going out in. "
        Variant.BODYCON_DRESS -> "Bodycon, short for \"body-conscious\" is a dress that clings tightly to your body. This one is only really suitable for going out in. "
        Variant.PINAFORE_DRESS -> "A sleeveless, collarless dress similar in appearance to pinny-style aprons and apparently called a \"jumper\" in American English. It's a very cute type of dress that you could wear out or just in day-to-day activities. "
        Variant.SLIP_DRESS -> "This dress is a bit too 'nice' to be casual clothing, but you could wear it to a club or on a date. "
        Variant.SHIFT_DRESS -> "A classic style of dress with straight lines that doesn't hug the waist. You could wear it on a night out, but it's also suitable for business wear. "
        Variant.SHEATH_DRESS -> "A simple style of figure-hugging dress. This one is only really suited for nights out. "
        else /*Variant.MAXI_DRESS*/ -> "A long dress, this one isn't really suitable for casual occasions - it's the sort of thing you'd wear to a club or on a date. "
    }
    val topType = when (variant) {
        Variant.PINAFORE_DRESS, Variant.SHIFT_DRESS -> TopType.ZIP
        Variant.SLIP_DRESS -> TopType.STRAPPY
        else -> topTypeParam
                ?: if (variant == Variant.SHEATH_DRESS && ThreadLocalRandom.current().nextBoolean()) TopType.ZIP
                else getTopType(style)
    }
    when (topType) {
        TopType.ZIP -> elegant += 2
        TopType.BUTTONS -> {
            cute += 2; elegant -= 1
        }
        TopType.HALTERTOP -> {
            cute -= 1; elegant += 1
        }
        TopType.STRAPLESS -> cute -= 2
    }
    val colour = colourParam ?: if (variant == Variant.SHIFT_DRESS) when (rnd10()) {
        in 0..3 -> Colour.BLACK
        4, 5 -> Colour.GREY
        6 -> Colour.WHITE
        7 -> Colour.BROWN
        8 -> Colour.BLUE
        else -> Colour.GREEN
    } else getColour(style)

    when (colour) {
        Colour.WHITE -> cute += 1
        Colour.PINK -> cute += 3
        Colour.BLACK, Colour.RED -> cute -= 1
        Colour.ORANGE -> elegant -= 1
    }

    val outfitTypes = when (variant) {
        Variant.PINAFORE_DRESS -> listOf(OutfitType.GOING_OUT, OutfitType.CASUAL)
        Variant.SHIFT_DRESS -> listOf(OutfitType.BUSINESS, OutfitType.GOING_OUT)
        else -> listOf(OutfitType.GOING_OUT)
    }
    if (variant != Variant.PINAFORE_DRESS && topType != TopType.STRAPLESS && topType != TopType.HALTERTOP &&
            flagsParam.getOrDefault(Flag.LOW_CUT, rnd10() < when (style) {
                Style.WHOLESOME, Style.CUTE -> 0
                Style.PROVOCATIVE -> 9
                Style.BUSINESSLIKE -> 4
                else -> 5
            })) {
        flags += Flag.LOW_CUT
        attractive += 1
        cute -= 3
        elegant -= 2
    }
    if (flagsParam.getOrDefault(Flag.THIN, rnd10() < when (style) {
                Style.WHOLESOME -> 0
                Style.PROVOCATIVE -> 8
                Style.CUTE -> 4
                else -> 5
            })) {
        flags += Flag.THIN
        attractive += 2
        cute -= 2
    }
    if (variant != Variant.SKATER_DRESS && variant != Variant.PINAFORE_DRESS && variant != Variant.SHIFT_DRESS &&
            flagsParam.getOrDefault(Flag.SEE_THROUGH, rnd10() < when (style) {
                Style.WHOLESOME, Style.CUTE, Style.ELEGANT, Style.BUSINESSLIKE -> 0
                Style.PROVOCATIVE -> 4
                else -> 1
            })) {
        flags += Flag.SEE_THROUGH
        attractive += 2
        cute -= 6
        elegant -= 6
    }
    if (variant == Variant.BODYCON_DRESS || (variant == Variant.SHEATH_DRESS &&
                    flagsParam.getOrDefault(Flag.CLINGY, style != Style.WHOLESOME &&
                            ThreadLocalRandom.current().nextInt(8) == 0))) {
        flags += Flag.CLINGY
        attractive += 1
        cute -= 4
    }
    when (variant) {
        Variant.SKATER_DRESS -> cute += 4
        Variant.PINAFORE_DRESS -> {
            cute += 6; elegant -= 3
        }
        Variant.SHIFT_DRESS, Variant.SHEATH_DRESS -> elegant += 1
    }
    if (length == Length.THIGH && (variant == Variant.SKATER_DRESS || variant == Variant.PINAFORE_DRESS)) cute += 2

    val shortDesc = (if (variant == Variant.PINAFORE_DRESS && length == Length.THIGH) "short " else "") +
            (if (Flag.THIN in flags) "thin " else "") + colour.desc + " " + st

    val price = 35 + (if (Flag.CLINGY in flags) 5 else 0) + (if (length == Length.ANKLES) 5 else 0)

    val dress = Dress(outfitTypes, colour, length, topType, attractive, cute, elegant, shortDesc, "dress", st,
            detailDesc, price)
    flags.forEach(dress::addFlag)
    return dress
}

fun getTopType(style: Style): TopType {
    return when (style) {
        Style.CUTE -> when (rnd10()) {
            in 0..3 -> TopType.STRAPPY
            4, 5 -> TopType.ZIP
            6, 7 -> TopType.BUTTONS
            8 -> TopType.HALTERTOP
            else -> TopType.STRAPLESS
        }
        Style.WHOLESOME -> when (rnd10()) {
            in 0..3 -> TopType.STRAPPY
            4, 5, 6 -> TopType.ZIP
            7, 8 -> TopType.BUTTONS
            else -> if (ThreadLocalRandom.current().nextBoolean()) TopType.HALTERTOP else TopType.STRAPLESS
        }
        Style.PROVOCATIVE -> when (rnd10()) {
            in 0..3 -> TopType.STRAPPY
            4, 5 -> TopType.HALTERTOP
            else -> TopType.STRAPLESS
        }
        Style.ELEGANT -> when (rnd10()) {
            in 0..3 -> TopType.STRAPPY
            in 4..7 -> TopType.ZIP
            8 -> TopType.HALTERTOP
            else -> TopType.STRAPLESS
        }
        else -> when (rnd10()) {
            in 0..4 -> TopType.STRAPPY
            5, 6 -> TopType.ZIP
            7 -> TopType.BUTTONS
            8 -> TopType.HALTERTOP
            else -> TopType.STRAPLESS
        }
    }
}
