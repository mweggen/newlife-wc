package nl.wggn.newlifewc

class Skirt(type: List<OutfitType>, colour: Colour, override val length: Length, attractive: Int, cute: Int, elegant: Int,
            shortDesc: String, private val detailAppend: String, price: Int) :
        AbstractClothing(type, 0, colour, attractive, cute, elegant, listOf(WhereWorn.BOTTOM), shortDesc, price), Bottom {
    override val skirt: Boolean
        get() = true
    override val basicDesc: String
        get() = "skirt"
    override val detailDesc: String
        get() = "A " + basicDesc + (if (hasFlag(Flag.CLINGY)) " that clings to your bottom" else "") + ". " +
                when (length) {
                    Length.THIGH -> "It's very short, leaving your legs bare up to the upper thigh. "
                    Length.ANKLES -> "It's a long skirt, covering your legs all the way down to your ankles. Classy! "
                    else -> "It comes down to just above your knees. "
                } + "\n" + detailAppend
}

fun createSportsSkirt(style: Style, variant: Variant?, colourParam: Colour?, lengthParam: Length?, topType: TopType?, flagsParam: Map<Flag, Boolean>): Skirt {
    val flags = mutableListOf(Flag.SINGULAR)
    val length = Length.THIGH
    var attractive = 3
    var cute = 0
    var elegant = 2
    val tennis = when (variant) {
        Variant.TENNIS_STYLE -> true
        Variant.SPORTS_SKIRT -> false
        else -> when (style) {
            Style.ELEGANT -> true
            Style.SCRUFFY, Style.CHEERFUL -> false
            Style.BUSINESSLIKE -> rnd10() < 4
            else -> rnd10() < 2
        }
    }
    val shortDesc: String
    val detail: String
    val colour = if (tennis) {
        shortDesc = "tennis style"
        detail = "A classic pleated tennis style. There don't seem to be many tennis courts around here, but you can also wear it for other types of exercise. "
        elegant += 2
        colourParam ?: when (rnd10()) {
            0, 1 -> Colour.BLACK
            2 -> Colour.GREY
            3 -> Colour.BLUE
            4 -> Colour.RED
            else -> Colour.WHITE
        }
    } else {
        shortDesc = "sports skirt"
        detail = "This is a light style that you can wear while running or working out. "
        val clingyChance = when (style) {
            Style.PROVOCATIVE -> 7
            Style.WHOLESOME -> 1
            Style.CUTE -> 2
            else -> 4
        }
        if (flagsParam.getOrDefault(Flag.CLINGY, rnd10() < clingyChance)) {
            flags += Flag.CLINGY
            attractive += 1
            cute -= 2
        }

        colourParam ?: when (style) {
            Style.CUTE -> when (rnd10()) {
                0, 1 -> Colour.BLACK
                2 -> Colour.GREY
                3 -> Colour.BLUE
                4, 5, 6 -> Colour.PINK
                else -> Colour.WHITE
            }
            Style.CHEERFUL -> when (rnd10()) {
                0, 1, 2 -> Colour.RED
                3 -> Colour.GREEN
                4, 5, 6 -> Colour.ORANGE
                7 -> Colour.BLUE
                else -> Colour.YELLOW
            }
            Style.BUSINESSLIKE -> when (rnd10()) {
                in 0..4 -> Colour.BLACK
                5, 6 -> Colour.GREY
                7 -> Colour.BLUE
                else -> Colour.WHITE
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                0, 1, 2 -> Colour.BLACK
                3 -> Colour.GREY
                4 -> Colour.BLUE
                5, 6 -> Colour.RED
                else -> Colour.WHITE
            }
            else -> when (rnd10()) {
                0, 1, 2 -> Colour.BLACK
                3 -> Colour.GREY
                4 -> Colour.BLUE
                5 -> Colour.PINK
                6 -> Colour.RED
                else -> Colour.WHITE
            }
        }
    }

    if (colour == Colour.PINK) cute += 2
    else if (colour == Colour.ORANGE) elegant -= 1

    val shortDesc1 = colour.desc + " " + shortDesc
    val price = if (tennis) 35 else 25
    val skirt = Skirt(listOf(OutfitType.ATHLETIC), colour, length, attractive, cute, elegant, shortDesc1, detail, price)
    flags.forEach(skirt::addFlag)
    return skirt
}

fun createSkirt(style: Style, variantParam: Variant?, colourParam: Colour?, lengthParam: Length?, topType: TopType?, flagsParam: Map<Flag, Boolean>): Skirt {
    val flags = mutableListOf(Flag.SINGULAR)
    var attractive = 3
    var cute = 1
    var elegant = 2
    var detail = ""

    val maxLength = variantParam?.lengths?.max() ?: Length.ANKLES
    val minLength = variantParam?.lengths?.min() ?: Length.THIGH
    val medLength = if (variantParam?.lengths?.contains(Length.KNEES) == true) Length.KNEES else variantParam?.lengths?.first()
            ?: Length.KNEES

    val length = lengthParam ?: when (style) {
        Style.ELEGANT -> if (rnd10() < 5) medLength else maxLength
        Style.PROVOCATIVE -> minLength
        Style.WHOLESOME -> if (rnd10() < 6) medLength else maxLength
        else -> when (rnd10()) {
            in 0..3 -> minLength
            in 4..7 -> medLength
            else -> maxLength
        }
    }
    if (length == Length.THIGH) {
        attractive += 2
        cute -= 4
        elegant -= 2
    } else if (length == Length.ANKLES) elegant += 4

    val colour = colourParam ?: getColour(style)
    if (colour == Colour.WHITE) cute += 1
    else if (colour == Colour.PINK) cute += 2
    else if (colour == Colour.RED) {
        cute -= 1
        elegant -= 1
    }

    if (flagsParam.getOrDefault(Flag.CLINGY, rnd10() < when (style) {
                Style.PROVOCATIVE -> 7
                Style.WHOLESOME -> 1
                Style.CUTE -> 2
                else -> 4
            })) {
        flags += Flag.CLINGY
        attractive += 1
        cute -= 2
    }

    var prependLength = false
    val basicType: String
    if (length == Length.THIGH) {
        when (variantParam ?: when (style) {
            Style.CUTE, Style.WHOLESOME -> when (rnd10()) {
                in 0..5 -> Variant.SKATER_SKIRT
                in 6..8 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                0, 1, 2 -> Variant.NANOSKIRT
                in 3..7 -> Variant.MINISKIRT
                else -> Variant.PLEATED_SKIRT
            }
            Style.ELEGANT -> when (rnd10()) {
                in 0..3 -> Variant.MINISKIRT
                4, 5 -> Variant.SKATER_SKIRT
                6 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            Style.CHEERFUL, Style.SCRUFFY -> when (rnd10()) {
                0 -> Variant.NANOSKIRT
                1, 2, 3 -> Variant.MINISKIRT
                4, 5, 6 -> Variant.SKATER_SKIRT
                else -> Variant.PLEATED_SKIRT
            }
            else -> when (rnd10()) {
                0 -> Variant.NANOSKIRT
                1, 2, 3 -> Variant.MINISKIRT
                4, 5, 6 -> Variant.SKATER_SKIRT
                7, 8 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
        }) {
            Variant.MINISKIRT -> basicType = "miniskirt"
            Variant.NANOSKIRT -> {
                basicType = "nanoskirt"
                cute -= 2
                elegant -= 1
                detail += "Even shorter than a miniskirt, this skirt is so short it could almost be mistaken for an extra-wide belt. "
            }
            Variant.SKATER_SKIRT -> {
                basicType = "skater skirt"
                prependLength = true
                cute += 4
            }
            Variant.PLEATED_SKIRT -> {
                basicType = "pleated miniskirt"
                cute += 3
            }
            else -> {
                basicType = "high-low skirt"
                attractive -= 1
                elegant += 2
                cute += 4
                prependLength = true
                detail += "A high-low skirt is a type of asymmetrical skirt with a high hemline in front and a lower one behind. This one is thigh-length in front, but the longer hem at the back makes it look a bit more cute and classy than most other short skirts. "
            }
        }
    } else if (length == Length.KNEES) {
        prependLength = true
        when (variantParam ?: when (style) {
            Style.CUTE, Style.WHOLESOME -> when (rnd10()) {
                0, 1 -> Variant.PENCIL_SKIRT
                2 -> Variant.A_LINE_SKIRT
                3 -> Variant.WRAP_SKIRT
                in 4..8 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            Style.PROVOCATIVE -> when (rnd10()) {
                in 0..3 -> Variant.PENCIL_SKIRT
                4, 5, 6 -> Variant.A_LINE_SKIRT
                7, 8 -> Variant.WRAP_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            Style.ELEGANT -> when (rnd10()) {
                0, 1 -> Variant.PENCIL_SKIRT
                2, 3, 4 -> Variant.A_LINE_SKIRT
                5 -> Variant.WRAP_SKIRT
                6 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            Style.BUSINESSLIKE -> when (rnd10()) {
                in 0..4 -> Variant.PENCIL_SKIRT
                5, 6, 7 -> Variant.A_LINE_SKIRT
                8 -> Variant.WRAP_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
            else -> when (rnd10()) {
                0, 1, 2 -> Variant.PENCIL_SKIRT
                3, 4, 5 -> Variant.A_LINE_SKIRT
                6 -> Variant.WRAP_SKIRT
                7, 8 -> Variant.PLEATED_SKIRT
                else -> Variant.HIGH_LOW_SKIRT
            }
        }) {
            Variant.PENCIL_SKIRT -> basicType = "pencil skirt"
            Variant.A_LINE_SKIRT -> basicType = "a-line skirt"
            Variant.WRAP_SKIRT -> basicType = "wrap skirt"
            Variant.PLEATED_SKIRT -> {
                basicType = "pleated skirt"
                cute += 2
            }
            else -> {
                basicType = "high-low skirt"
                elegant += 1
                detail += "A high-low skirt is a type of asymmetrical skirt with a high hemline in front and a lower one behind. This one is knee-length in front but looks like a maxi skirt from behind, giving you a more elegant silhouette. "
            }
        }
    } else {
        when (variantParam ?: when (rnd10()) {
            in 0..4 -> Variant.MAXI_SKIRT
            5, 6, 7 -> Variant.WRAP_SKIRT
            else -> Variant.PLEATED_SKIRT
        }) {
            Variant.MAXI_SKIRT -> basicType = "maxi-skirt"
            Variant.WRAP_SKIRT -> {
                prependLength = true
                basicType = "wrap skirt"
            }
            else -> basicType = "pleated maxi-skirt"
        }
    }

    val shortDesc = (if (prependLength) {
        when (length) {
            Length.THIGH -> "short "
            Length.KNEES -> "knee-length "
            Length.ANKLES -> "long "
            else -> ""
        }
    } else "") + colour.desc + " " + basicType

    if (detail.isEmpty()) detail = "This is an ordinary $shortDesc that you could wear on a night out or just in your day-to-day life. "

    val price = 20 + (if (flags.contains(Flag.CLINGY)) 5 else 0) +
            (if (length == Length.ANKLES) 5 else 0)

    val skirt = Skirt(listOf(OutfitType.GOING_OUT, OutfitType.CASUAL), colour, length, attractive, cute, elegant, shortDesc, detail, price)
    flags.forEach(skirt::addFlag)
    return skirt
}
