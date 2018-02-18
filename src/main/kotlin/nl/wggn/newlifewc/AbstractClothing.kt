package nl.wggn.newlifewc

import javafx.application.Application
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.stage.Stage
import nl.wggn.newlifewc.Colour.*
import nl.wggn.newlifewc.Flag.*
import nl.wggn.newlifewc.Length.*
import nl.wggn.newlifewc.OutfitType.*
import nl.wggn.newlifewc.Style.*
import nl.wggn.newlifewc.TopType.*
import nl.wggn.newlifewc.WhereWorn.*
import nl.wggn.newlifewc.WhereWorn.ANY
import java.util.concurrent.ThreadLocalRandom

class Test : Application() {

    var selected: AbstractClothing? = null
    val shortDesc = TextField()
    val colourChoiceBox = ChoiceBox(FXCollections.observableArrayList(*enumValues<Colour>()))
    val detailDesc = TextArea()

    override fun start(stage: Stage) {
        val grid = GridPane()
        grid.alignment = Pos.CENTER
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(25.0, 25.0, 25.0, 25.0)
        val scene = Scene(grid, 800.0, 600.0)
        stage.scene = scene

        val clothingList: ObservableList<AbstractClothing> = FXCollections.observableArrayList(createWorkTrousers(), createYogaPants(PROVOCATIVE), createYogaPants(CUTE))
        val listView = ListView<AbstractClothing>()

        val pane = FlowPane()
        val styleChoiceBox = ChoiceBox(FXCollections.observableArrayList(*enumValues<Style>()))
        styleChoiceBox.selectionModel.selectFirst()
        pane.children.add(styleChoiceBox)

        val wwChoiceBox = ChoiceBox(FXCollections.observableArrayList(*enumValues<WhereWorn>()))
        wwChoiceBox.selectionModel.selectFirst()
        pane.children.add(wwChoiceBox)

        val outfitTypeChoiceBox = ChoiceBox(FXCollections.observableArrayList(*enumValues<OutfitType>()))
        outfitTypeChoiceBox.selectionModel.selectFirst()
        pane.children.add(outfitTypeChoiceBox)

        val addButton = Button("Add")
        val createButton = Button("Create")
        val removeButton = Button("Remove")

        pane.children.add(createButton)
        pane.children.add(addButton)
        pane.children.add(removeButton)

        grid.add(pane, 0, 0, 3, 1)

        addButton.setOnAction {
            if (selected != null) {
                clothingList.add(selected)
                listView.selectionModel.select(selected)
            }
        }
        removeButton.setOnAction {
            if (selected != null) {
                clothingList.remove(selected)
                selectClothing(listView.selectionModel.selectedItem)
            }
        }


        val flowPane = FlowPane()

        val categories = ChoiceBox<Category>(FXCollections.observableArrayList(*Category.values()))
        categories.selectionModel.selectFirst()
        categories.minWidth = 130.0
        val subCategories = ChoiceBox<SubCategory>(FXCollections.observableArrayList(*SubCategory.values()))
        subCategories.selectionModel.selectFirst()
        subCategories.minWidth = 130.0
        val variants = ChoiceBox<Variant>(FXCollections.observableArrayList(*Variant.values().sortedArrayWith(compareBy { it.name })))
        variants.minWidth = 130.0
        val topTypes = ChoiceBox<TopType>(FXCollections.observableArrayList(*TopType.values()))
        topTypes.selectionModel.selectFirst()
        topTypes.minWidth = 130.0
        val lengths = ChoiceBox<Length>(FXCollections.observableArrayList(*Length.values()))
        lengths.selectionModel.selectFirst()
        lengths.minWidth = 130.0
        colourChoiceBox.selectionModel.selectFirst()
        colourChoiceBox.minWidth = 130.0

        val flagBoxes = mutableMapOf<Flag, CheckBox>()

        wwChoiceBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val selectedItem = categories.value
            val newList = if (newValue == null || newValue == ANY) {
                Category.values().sliceArray(1 until Category.values().size).asList().sortedWith(compareBy { it.name })
            } else {
                Category.values().filter { it.whereWorn.contains(newValue) }.sortedWith(compareBy { it.name })
            }
            categories.items.setAll((if (newList.size > 1) listOf(Category.ANY) else listOf()) + newList)

            if (selectedItem != null && newList.contains(selectedItem)) {
                categories.value = selectedItem
            } else if (newList.isNotEmpty()) {
                categories.selectionModel.selectFirst()
            }
        }

        categories.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val selectedItem: SubCategory? = subCategories.value
            val newList: List<SubCategory?> = if (newValue == null || newValue == Category.ANY) {
                SubCategory.values().sliceArray(1 until SubCategory.values().size).asList().sortedWith(compareBy { it.name })
            } else {
                SubCategory.values().filter { it.category == newValue }.sortedWith(compareBy { it.name })
            }
            subCategories.items.setAll((if (newList.size > 1) listOf(SubCategory.ANY) else listOf()) + newList)

            if (selectedItem != null && newList.contains(selectedItem)) {
                subCategories.value = selectedItem
            } else if (newList.isNotEmpty()) {
                subCategories.selectionModel.selectFirst()
            }
        }

        subCategories.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val selectedItem: Variant? = variants.value
            val newList: List<Variant?> = if (newValue == null || newValue == SubCategory.ANY) {
                if (variants.value == Variant.ANY) createButton.isDisable = true
                Variant.values().sliceArray(1 until Variant.values().size)
                        .filter { subCategories.items.contains(it.subCategory) }.sortedWith(compareBy { it.name })
            } else {
                createButton.isDisable = false
                Variant.values().filter { it.subCategory == newValue }.sortedWith(compareBy { it.name })
            }
            variants.items.setAll((if (newList.size > 1) listOf(Variant.ANY) else listOf()) + newList)

            if (selectedItem != null && newList.contains(selectedItem)) {
                variants.value = selectedItem
            } else if (newList.isNotEmpty()) {
                variants.selectionModel.selectFirst()
            }
        }

        variants.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null && newValue != Variant.ANY) {
                createButton.isDisable = false
                val topTypes1 = newValue.topTypes
                topTypes.isDisable = topTypes1.size < 2
                topTypes.items.setAll((if (topTypes1.size > 1) setOf(TopType.ANY) else setOf()) + topTypes1.sorted())

                val lengths1 = newValue.lengths
                lengths.isDisable = lengths1.size < 2
                lengths.items.setAll((if (lengths1.size > 1) setOf(Length.ANY) else setOf()) + lengths1.sorted())

                colourChoiceBox.isDisable = newValue.colours.size < 2
                colourChoiceBox.items.setAll((if (newValue.colours.size > 1) setOf(Colour.ANY) else setOf()) +
                        newValue.colours.sortedWith(compareBy { it.desc }))

                flagBoxes.values.forEach {
                    it.isDisable = true
                    it.isIndeterminate = false
                    it.isSelected = false
                }
                newValue.mandatoryFlags.forEach {
                    flagBoxes[it]?.isSelected = true
                }
                newValue.optionalFlags.forEach {
                    flagBoxes[it]?.isDisable = false
                    flagBoxes[it]?.isIndeterminate = true
                }

            } else {
                if (subCategories.value == SubCategory.ANY) {
                    createButton.isDisable = true
                    flagBoxes.values.forEach {
                        it.isDisable = false
                        it.isIndeterminate = true
                        it.isSelected = false
                    }
                } else if (variants.value == null || variants.value == Variant.ANY) {
                    val selectedVariants = Variant.values().filter { it.subCategory == subCategories.value }
                    val mandatoryFlags = selectedVariants.flatMap { it.mandatoryFlags }.distinct()
                    val optionalFlags = selectedVariants.flatMap { it.optionalFlags }.distinct()

                    flagBoxes.values.forEach {
                        it.isDisable = true
                        it.isIndeterminate = false
                        it.isSelected = false
                    }
                    mandatoryFlags.forEach {
                        flagBoxes[it]?.isSelected = true
                    }
                    optionalFlags.forEach {
                        flagBoxes[it]?.isSelected = false
                        flagBoxes[it]?.isDisable = false
                        flagBoxes[it]?.isIndeterminate = true
                    }
                }
                topTypes.items.setAll(TopType.ANY, *TopType.values().sliceArray(1 until TopType.values().size).sortedArray())
                topTypes.isDisable = false
                val newLengths = Length.values().filter { length -> variants.items.any { variant -> variant.lengths.contains(length) } }
                lengths.items.setAll((if (newLengths.size > 1) listOf(Length.ANY) else listOf()) + newLengths)
                lengths.isDisable = lengths.items.size < 2
                colourChoiceBox.items.setAll(*Colour.values())
                colourChoiceBox.isDisable = false
            }
            if (!topTypes.items.contains(topTypes.value) && topTypes.items.isNotEmpty()) {
                topTypes.selectionModel.selectFirst()
            }
            if (!lengths.items.contains(lengths.value) && lengths.items.isNotEmpty()) {
                lengths.selectionModel.selectFirst()
            }
            if (!colourChoiceBox.items.contains(colourChoiceBox.value) && colourChoiceBox.items.isNotEmpty()) {
                colourChoiceBox.selectionModel.selectFirst()
            }
        }

        variants.selectionModel.select(ThreadLocalRandom.current().nextInt(Variant.values().size))

        createButton.setOnAction {
            val colour = colourChoiceBox.value
            val length = lengths.value
            val variant = variants.value
            val subCategory = subCategories.value
            val topType = topTypes.value
            val flags = flagBoxes.filter { !it.value.isIndeterminate }.map { Pair(it.key, it.value.isSelected) }.toMap()
            selectClothing(createClothing(if (variant != Variant.ANY) variant else null,
                    if (subCategory != SubCategory.ANY) subCategory else null,
                    styleChoiceBox.value,
                    if (colour != Colour.ANY) colour else null,
                    if (length != Length.ANY) length else null,
                    if (topType != TopType.ANY) topType else null,
                    flags))
            addButton.isDisable = false
        }


        flowPane.children += categories
        flowPane.children += subCategories
        flowPane.children += variants
        flowPane.children += topTypes
        flowPane.children += lengths
        flowPane.children += colourChoiceBox


        grid.add(flowPane, 0, 1, 3, 1)


        detailDesc.isWrapText = true
        listView.items = clothingList
        listView.selectionModel.selectedItemProperty().addListener({ _, _, newValue ->
            selectClothing(newValue)
            if (newValue != null) {
                addButton.isDisable = true
            }
        })
        grid.add(listView, 0, 2, 1, 5)

        grid.add(Text("Short Desc"), 1, 2)
        shortDesc.isEditable = false
        grid.add(shortDesc, 2, 2)

        detailDesc.isEditable = false
        grid.add(detailDesc, 1, 4, 2, 1)


        val flagsPane = FlowPane()
        flagsPane.orientation = Orientation.VERTICAL

        Flag.values().sortedBy { it.name }.forEach {
            val checkBox = CheckBox(readable(it.name))
            flagBoxes += Pair(it, checkBox)
            checkBox.isAllowIndeterminate = true
            flagsPane.children += checkBox
        }

        grid.add(flagsPane, 3, 2, 1, 3)


        stage.show()
    }

    private fun selectClothing(clothing: AbstractClothing?) {
        selected = clothing
        shortDesc.text = clothing?.shortDesc
        detailDesc.text = clothing?.fullDesc()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Test::class.java)
        }
    }
}

fun createClothing(variant: Variant?, subCategory: SubCategory?, style: Style, colour: Colour?, length: Length?, topType: TopType?, flags: Map<Flag, Boolean>): AbstractClothing =
        if (variant != null && variant != ANY) {
            variant.subCategory.create.invoke(style, variant, colour, length, topType, flags)
        } else {
            subCategory?.create?.invoke(style, null, colour, length, topType, flags)!!
        }

interface Clothing {
    val type: List<OutfitType>
    val colourDesc: String
    val colour: Colour

    fun hasFlag(flag: Flag): Boolean
    val shortDesc: String
    fun fullDesc(): String
    val basicDesc: String
    val attractive: Int
    val cute: Int
    val elegant: Int
//    val zip: Boolean
}

abstract class AbstractClothing(override val type: List<OutfitType>, val removeDiff: Int, colour: Colour,
                                override val attractive: Int, override val cute: Int, override val elegant: Int,
                                val whereWorn: List<WhereWorn>, override val shortDesc: String, val basePrice: Int) : Clothing {
    val price = (basePrice..basePrice * 2).random()
    private val flags = HashSet<Flag>()

    abstract val detailDesc: String

    val colourProperty: ObjectProperty<Colour> = SimpleObjectProperty(colour)
    override var colour: Colour
        get() = colourProperty.get()
        set(value) = colourProperty.set(value)

    override val colourDesc
        get() = colour.desc

    override fun hasFlag(flag: Flag) = flags.contains(flag)
    fun addFlag(flag: Flag) {
        flags.add(flag)
    }

    override fun fullDesc(): String =
            detailDesc + "\n" +
                    "Attractiveness: $attractive\n" +
                    (if (cute < 0) "Naughtiness: ${-cute}" else "Cuteness: $cute") + "\n" +
                    (if (elegant < 0) "Casualness: ${-elegant}" else "Elegance: $elegant") + "\n" +
                    "Suitable for outfits: " + type.joinToString(", ") { it.name.toLowerCase() }

    override fun toString() = shortDesc
}

interface Bottom : Clothing {
    val length: Length
    val skirt: Boolean
}

val athOrCas = listOf(ATHLETIC, CASUAL)

enum class Flag {
    BOOST_ATHLETIC_ELEGANCE,
    GOOD_WITH_LOWCUT,
    PULL_DOWN,
    ILL_FITTING,
    LOW_CUT,
    THIN,
    LACY,
    SEE_THROUGH,
    CLINGY,
    DANCE,
    SILK,
    LOW_RISE,
    SHOWS_TUMMY,
    THICK_STRAPS,
    SINGULAR
}

fun readable(str: String) = str.toLowerCase().replace('_', ' ')

enum class Length {
    ANY, THIGH, KNEES, ANKLES;

    override fun toString() = readable(name)
}

enum class OutfitType {
    ANY, CASUAL, GOING_OUT, BUSINESS, ATHLETIC,
    NIGHTWEAR, SEXY_NIGHTWEAR, FORMAL, WEDDING;

    override fun toString() = readable(name)
}

enum class Colour(val desc: String) {
    ANY("any"),
    WHITE("white"), BLACK("black"), RED("red"), BLUE("blue"), GREEN("green"),
    YELLOW("yellow"), PINK("pink"), GREY("grey"), BROWN("brown"), CREAM("cream"),
    PURPLE("purple"), ORANGE("orange"), RED_CHECK("red check"), TARTAN("tartan"),
    GREEN_CHECK("green check"), BLACK_AND_WHITE_CHECK("black-and-white check");

    override fun toString() = desc
}

enum class WhereWorn(private val desc: String) {
    ANY("Any"), TOP("Upper Body"), BOTTOM("Lower Body"),
    PANTIES("Panties"), BRA("Bra"), LEGS("Legwear");

    override fun toString() = desc
}

enum class Style {
    NONE, CUTE, PROVOCATIVE, ELEGANT,
    CHEERFUL, SCRUFFY, WHOLESOME, BUSINESSLIKE;

    override fun toString() = readable(name)
}

enum class Category(val whereWorn: Set<WhereWorn>) {
    ANY(setOf(WhereWorn.ANY)),
    DRESS(setOf(WhereWorn.TOP, BOTTOM)),
    NIGHTGOWN(setOf(WhereWorn.TOP, BOTTOM)),
    NIGHTIE(setOf(WhereWorn.TOP, BOTTOM)),
    PANTS(setOf(BOTTOM)),
    SKIRT(setOf(BOTTOM)),
    TOP(setOf(WhereWorn.TOP)),
    BRA(setOf(WhereWorn.BRA)),
    LEGWEAR(setOf(LEGS)),
    PANTIES(setOf(WhereWorn.PANTIES));

    override fun toString() = readable(name)
}

enum class SubCategory(val category: Category, val create: (s: Style, v: Variant?, c: Colour?, l: Length?, tt: TopType?, flags: Map<Flag, Boolean>) -> AbstractClothing = ::createSportsSkirt) {

    ANY(Category.ANY),

    FLAMENCO(Category.DRESS, ::createFlamencoDress),
    DRESS(Category.DRESS),
    BABYDOLL(Category.DRESS, ::createBabydoll),
    SLIP(Category.DRESS, ::createSlip),
    SUMMER_DRESS(Category.DRESS, ::createSummerDress),
    //TODO santa dresses

    WORK_SKIRT(Category.SKIRT),
    SPORTS_SKIRT(Category.SKIRT, ::createSportsSkirt),
    DENIM_SKIRT(Category.SKIRT),
    SKIRT(Category.SKIRT, ::createSkirt),

    WORK_TROUSERS(Category.PANTS),
    YOGA_PANTS(Category.PANTS),
    TRACKSUIT_BOTTOMS(Category.PANTS),
    PYJAMA_BOTTOMS(Category.PANTS),
    HOTPANTS(Category.PANTS),
    BIKE_SHORTS(Category.PANTS),
    SHORTS(Category.PANTS),
    JEANS(Category.PANTS),

    NIGHTGOWN(Category.NIGHTGOWN),
    NIGHTIE(Category.NIGHTIE),

    BRA(Category.BRA),
    SPORTS_BRA(Category.BRA),

    PANTIES(Category.PANTIES),

    SOCKS(Category.LEGWEAR),
    STOCKINGS(Category.LEGWEAR),

    TANK_TOP(Category.TOP),
    CAMISOLE(Category.TOP),
    PYJAMA_TOP(Category.TOP),
    BLOUSE(Category.TOP),
    SHEER_BLOUSE(Category.TOP),
    TOP(Category.TOP);

    override fun toString() = readable(name)
}

enum class Variant(val subCategory: SubCategory, val topTypes: Collection<TopType>, val lengths: Collection<Length>,
                   val mandatoryFlags: Collection<Flag>, val optionalFlags: Collection<Flag>, val outfitTypes: Collection<OutfitType>,
                   val colours: Collection<Colour>) {
    ANY(SubCategory.ANY, setOf(), setOf(), setOf(), setOf(), setOf(), setOf()),


    FLAMENCO(SubCategory.FLAMENCO, setOf(ZIP), setOf(ANKLES), setOf(SINGULAR, DANCE),
            setOf(LOW_CUT, THIN, CLINGY), setOf(GOING_OUT, FORMAL), setOf(WHITE, BLACK, RED, YELLOW, PURPLE, GREEN)),

    BODYCON_DRESS(SubCategory.DRESS, allTopTypes, setOf(THIGH), setOf(SINGULAR, CLINGY), setOf(LOW_CUT, THIN, SEE_THROUGH),
            setOf(GOING_OUT), outerColours),
    SLIP_DRESS(SubCategory.DRESS, setOf(STRAPPY), setOf(THIGH, KNEES), setOf(SINGULAR), setOf(LOW_CUT, THIN, SEE_THROUGH),
            setOf(GOING_OUT), outerColours),
    SKATER_DRESS(SubCategory.DRESS, allTopTypes, setOf(THIGH, KNEES), setOf(SINGULAR), setOf(LOW_CUT, THIN), setOf(GOING_OUT),
            outerColours),
    PINAFORE_DRESS(SubCategory.DRESS, setOf(ZIP), setOf(THIGH, KNEES), setOf(SINGULAR), setOf(THIN), setOf(GOING_OUT, CASUAL),
            outerColours),
    SHIFT_DRESS(SubCategory.DRESS, setOf(ZIP), setOf(KNEES), setOf(SINGULAR), setOf(THIN, LOW_CUT), setOf(BUSINESS, GOING_OUT),
            setOf(BLACK, GREY, WHITE, BROWN, BLUE, GREEN)),
    SHEATH_DRESS(SubCategory.DRESS, allTopTypes, setOf(KNEES), setOf(SINGULAR), setOf(THIN, LOW_CUT, SEE_THROUGH, CLINGY),
            setOf(GOING_OUT), outerColours),
    MAXI_DRESS(SubCategory.DRESS, allTopTypes, setOf(ANKLES), setOf(SINGULAR), setOf(THIN, LOW_CUT, SEE_THROUGH), setOf(GOING_OUT),
            outerColours),

    CUTE_BABYDOLL(SubCategory.BABYDOLL, setOf(STRAPPY, HALTERTOP), setOf(THIGH), setOf(SINGULAR, THIN), setOf(LOW_CUT),
            setOf(SEXY_NIGHTWEAR), setOf(WHITE, PINK, YELLOW, PURPLE)),
    BABYDOLL(SubCategory.BABYDOLL, setOf(STRAPPY, HALTERTOP), setOf(THIGH), setOf(SINGULAR, THIN), setOf(LOW_CUT, SEE_THROUGH),
            setOf(SEXY_NIGHTWEAR), setOf(BLACK, RED, WHITE, PINK, BLUE, YELLOW)),
    SLIP(SubCategory.SLIP, setOf(STRAPPY, HALTERTOP), setOf(THIGH), setOf(SINGULAR, THIN), setOf(LOW_CUT), setOf(SEXY_NIGHTWEAR),
            setOf(CREAM, BLACK, RED, PURPLE, BLUE, WHITE, PINK, YELLOW, GREEN)),

    SUMMER_DRESS(SubCategory.SUMMER_DRESS, setOf(STRAPPY, ZIP, BUTTONS, HALTERTOP), setOf(THIGH, ANKLES, KNEES), setOf(SINGULAR, THIN),
            setOf(LOW_CUT), setOf(CASUAL), setOf(WHITE, PINK, YELLOW, RED, GREEN, PURPLE, ORANGE, BLUE, CREAM, BLACK, BROWN, GREY)),


    TENNIS_STYLE(SubCategory.SPORTS_SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(), setOf(ATHLETIC), setOf(RED, BLUE, YELLOW, WHITE, BLACK, GREY)),
    SPORTS_SKIRT(SubCategory.SPORTS_SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(CLINGY), setOf(ATHLETIC), setOf(RED, BLUE, YELLOW, WHITE, BLACK, GREY, PINK, GREEN, ORANGE)),

    MINISKIRT(SubCategory.SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),
    NANOSKIRT(SubCategory.SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),
    SKATER_SKIRT(SubCategory.SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),

    HIGH_LOW_SKIRT(SubCategory.SKIRT, setOf(), setOf(THIGH, KNEES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),
    WRAP_SKIRT(SubCategory.SKIRT, setOf(), setOf(KNEES, ANKLES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),
    PLEATED_SKIRT(SubCategory.SKIRT, setOf(), setOf(THIGH, KNEES, ANKLES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),

    PENCIL_SKIRT(SubCategory.SKIRT, setOf(), setOf(KNEES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),
    A_LINE_SKIRT(SubCategory.SKIRT, setOf(), setOf(KNEES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),

    MAXI_SKIRT(SubCategory.SKIRT, setOf(), setOf(ANKLES), setOf(SINGULAR), setOf(CLINGY), setOf(GOING_OUT, CASUAL), outerColours),

    WORK_SKIRT(SubCategory.WORK_SKIRT, setOf(), setOf(THIGH), setOf(SINGULAR), setOf(CLINGY), setOf(BUSINESS), setOf(BLACK, BLUE, PURPLE, GREEN, RED, YELLOW, GREY, BROWN, WHITE)),
    PENCIL_SUIT_SKIRT(SubCategory.WORK_SKIRT, setOf(), setOf(KNEES, ANKLES), setOf(SINGULAR), setOf(CLINGY), setOf(BUSINESS), setOf(BLACK, BLUE, PURPLE, GREEN, RED, YELLOW, GREY, BROWN, WHITE)),

    DENIM_SKIRT(SubCategory.DENIM_SKIRT, setOf(), setOf(THIGH, KNEES), setOf(SINGULAR), setOf(), setOf(CASUAL), setOf(BLUE, BLACK, WHITE, PINK, GREY, RED, YELLOW)),


    FULL_CUP_BRA(SubCategory.BRA, setOf(), setOf(), setOf(), setOf(LACY), normalOutfitTypes, underColours),
    DEMI_CUP_BRA(SubCategory.BRA, setOf(), setOf(), setOf(), setOf(LACY), normalOutfitTypes, underColours),
    BALCONY_BRA(SubCategory.BRA, setOf(), setOf(), setOf(), setOf(LACY), normalOutfitTypes, underColours),
    PLUNGE_BRA(SubCategory.BRA, setOf(), setOf(), setOf(), setOf(LACY), normalOutfitTypes, underColours),
    BRALETTE(SubCategory.BRA, setOf(), setOf(), setOf(), setOf(LACY), normalOutfitTypes, underColours),

    SPORTS_BRA(SubCategory.SPORTS_BRA, setOf(), setOf(), setOf(), setOf(), setOf(ATHLETIC), setOf(WHITE, PINK, YELLOW, RED, GREEN, BLUE, BLACK, GREY, PURPLE)),


    NIGHTIE(SubCategory.NIGHTIE, setOf(BUTTONS, ZIP, STRAPPY), setOf(THIGH, KNEES), setOf(SINGULAR), setOf(LOW_CUT, THIN), setOf(NIGHTWEAR), setOf(WHITE, PINK, BLACK, YELLOW, BLUE)),

    NIGHTGOWN(SubCategory.NIGHTGOWN, setOf(BUTTONS, ZIP, STRAPPY), setOf(KNEES, ANKLES), setOf(SINGULAR), setOf(LOW_CUT, THIN), setOf(NIGHTWEAR), setOf(WHITE, BLACK, BLUE, RED, YELLOW, CREAM)),


    WORK_TROUSERS(SubCategory.WORK_TROUSERS, setOf(), setOf(ANKLES), setOf(), setOf(), setOf(BUSINESS), setOf(BLACK, BROWN, GREY, WHITE)),

    YOGA_PANTS(SubCategory.YOGA_PANTS, setOf(), setOf(ANKLES), setOf(CLINGY, PULL_DOWN, BOOST_ATHLETIC_ELEGANCE), setOf(), setOf(ATHLETIC, CASUAL), athleticLongPantsColours),

    TRACKSUIT_BOTTOMS(SubCategory.TRACKSUIT_BOTTOMS, setOf(), setOf(ANKLES), setOf(PULL_DOWN), setOf(), setOf(ATHLETIC, CASUAL), athleticLongPantsColours),

    PYJAMA_BOTTOMS(SubCategory.PYJAMA_BOTTOMS, setOf(), setOf(THIGH, ANKLES), setOf(PULL_DOWN), setOf(THIN), setOf(NIGHTWEAR), pyjamaColours),
    SILK_PYJAMA_BOTTOMS(SubCategory.PYJAMA_BOTTOMS, setOf(), setOf(THIGH, ANKLES), setOf(SILK, PULL_DOWN), setOf(THIN), setOf(NIGHTWEAR), silkPyjamaColours),

    HOTPANTS(SubCategory.HOTPANTS, setOf(), setOf(THIGH), setOf(CLINGY), setOf(LOW_RISE), setOf(GOING_OUT, CASUAL), jeansColours),

    BIKE_SHORTS(SubCategory.BIKE_SHORTS, setOf(), setOf(THIGH), setOf(CLINGY, PULL_DOWN), setOf(), setOf(ATHLETIC), jeansColours),

    DENIM_SHORTS(SubCategory.SHORTS, setOf(), setOf(THIGH), setOf(), setOf(CLINGY, LOW_RISE), setOf(CASUAL), jeansColours),
    CHINO_SHORTS(SubCategory.SHORTS, setOf(), setOf(THIGH), setOf(), setOf(CLINGY, LOW_RISE), setOf(CASUAL), outerColours),
    SHORTS(SubCategory.SHORTS, setOf(), setOf(KNEES, THIGH), setOf(), setOf(CLINGY, LOW_RISE), setOf(CASUAL), outerColours),

    SKINNY_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(CLINGY), setOf(LOW_RISE), setOf(CASUAL), jeansColours),
    DESIGNER_SKINNY_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(CLINGY), setOf(LOW_RISE), setOf(CASUAL, GOING_OUT), jeansColours),
    STRAIGHT_LEG_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(CLINGY), setOf(LOW_RISE), setOf(CASUAL), jeansColours),
    DESIGNER_STRAIGHT_LEG_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(CLINGY), setOf(LOW_RISE), setOf(CASUAL, GOING_OUT), jeansColours),
    BOOTCUT_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(), setOf(LOW_RISE), setOf(CASUAL), jeansColours),
    DESIGNER_BOOTCUT_JEANS(SubCategory.JEANS, setOf(), setOf(ANKLES), setOf(), setOf(LOW_RISE), setOf(CASUAL, GOING_OUT), jeansColours),


    THONG(SubCategory.PANTIES, setOf(), setOf(), setOf(SINGULAR), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR), underColours),
    G_STRING(SubCategory.PANTIES, setOf(), setOf(), setOf(SINGULAR), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR), underColours),
    HIGH_LEG_PANTIES(SubCategory.PANTIES, setOf(), setOf(), setOf(), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR, ATHLETIC), underColours),
    BOYSHORTS(SubCategory.PANTIES, setOf(), setOf(), setOf(), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR, ATHLETIC), underColours),
    HIPSTER_PANTIES(SubCategory.PANTIES, setOf(), setOf(), setOf(), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR, ATHLETIC), underColours),
    CLASSIC_BRIEFS(SubCategory.PANTIES, setOf(), setOf(), setOf(), setOf(THIN, LACY), setOf(CASUAL, GOING_OUT, NIGHTWEAR, BUSINESS, SEXY_NIGHTWEAR, ATHLETIC), underColours),


    VEST(SubCategory.TANK_TOP, setOf(STRAPPY), setOf(), setOf(), setOf(CLINGY, LOW_CUT, THIN, SHOWS_TUMMY, THICK_STRAPS), setOf(ATHLETIC, CASUAL), outerColours),
    TANK_TOP(SubCategory.TANK_TOP, setOf(STRAPPY), setOf(), setOf(THICK_STRAPS), setOf(CLINGY, LOW_CUT, THIN, SHOWS_TUMMY), setOf(ATHLETIC, CASUAL), outerColours),

    CAMISOLE(SubCategory.CAMISOLE, setOf(STRAPPY), setOf(), setOf(THIN), setOf(LOW_CUT, SEE_THROUGH), setOf(SEXY_NIGHTWEAR), setOf(BLACK, RED, WHITE, PINK, BLUE, YELLOW)),

    PYJAMA_TOP(SubCategory.PYJAMA_TOP, setOf(BUTTONS), setOf(), setOf(), setOf(THIN), setOf(NIGHTWEAR), pyjamaColours),
    SILK_PYJAMA_TOP(SubCategory.PYJAMA_TOP, setOf(BUTTONS), setOf(), setOf(SILK), setOf(THIN), setOf(NIGHTWEAR), silkPyjamaColours),

    BLOUSE(SubCategory.BLOUSE, setOf(BUTTONS), setOf(), setOf(), setOf(LOW_CUT, THIN), setOf(BUSINESS), blouseColours),

    SHEER_BLOUSE(SubCategory.SHEER_BLOUSE, setOf(BUTTONS), setOf(), setOf(THIN, SEE_THROUGH), setOf(LOW_CUT, SHOWS_TUMMY), setOf(GOING_OUT), outerColours),

    TOP(SubCategory.TOP, setOf(STRAPPY, ZIP, BUTTONS), setOf(), setOf(), setOf(LOW_CUT, THIN, CLINGY, SEE_THROUGH, SHOWS_TUMMY), setOf(GOING_OUT, CASUAL), outerColours),
    STRAPLESS_TOP(SubCategory.TOP, setOf(HALTERTOP, TopType.STRAPLESS), setOf(), setOf(), setOf(THIN, CLINGY, SEE_THROUGH, SHOWS_TUMMY), setOf(GOING_OUT, CASUAL), outerColours)
    ;

    override fun toString() = readable(name)
}

val normalOutfitTypes = setOf(CASUAL, FORMAL, GOING_OUT, BUSINESS)
val weddingOutfitTypes = setOf(CASUAL, FORMAL, GOING_OUT, BUSINESS, WEDDING)

val allTopTypes = setOf(STRAPPY, ZIP, BUTTONS, HALTERTOP, STRAPLESS)

val outerColours = setOf(WHITE, PINK, YELLOW, RED, PURPLE, GREEN, BLUE, CREAM, BLACK, GREY, BROWN)
val underColours = setOf(WHITE, PINK, YELLOW, RED, PURPLE, GREEN, BLUE, CREAM, BLACK, GREY)
val athleticLongPantsColours = setOf(BLACK, BROWN, GREY, BLUE, PINK, WHITE, RED, GREEN, ORANGE, YELLOW)
val silkPyjamaColours = setOf(WHITE, CREAM, PINK, YELLOW, RED, BLACK)
val pyjamaColours = setOf(WHITE, PINK, BLACK, GREEN, BLUE, YELLOW, RED, ORANGE, GREY)
val jeansColours = setOf(BLUE, BLACK, WHITE, PINK, GREY, RED)
val blouseColours = setOf(WHITE, PINK, YELLOW, RED, ORANGE, GREEN, BLUE, BLACK, GREY)


enum class TopType(val undoDiff: Int = 0) {
    ANY, STRAPPY, HALTERTOP(-10), STRAPLESS(-10), SLEEVED(20), ZIP(5), BUTTONS(10);

    override fun toString() = readable(name)
}

fun ClosedRange<Int>.random() = ThreadLocalRandom.current().nextInt(endInclusive - start) + start

fun isSilk(style: Style) =
        rnd10() < when (style) {
            BUSINESSLIKE -> 4
            ELEGANT -> 10
            PROVOCATIVE -> 5
            WHOLESOME, CUTE -> 2
            else -> 0
        }

fun isThin(style: Style, silk: Boolean): Boolean =
        rnd10() < (if (silk) 5 else 2) +
                when (style) {
                    WHOLESOME -> -2
                    CUTE -> -3
                    PROVOCATIVE -> 5
                    else -> 0
                }

fun getColour(style: Style): Colour =
        when (style) {
            Style.CUTE -> when (rnd10()) {
                in 0..3 -> WHITE
                in 4..8 -> PINK
                else -> YELLOW
            }
            CHEERFUL -> when (rnd10()) {
                0, 1, 2 -> RED
                3 -> PURPLE
                4, 5, 6 -> YELLOW
                7, 8 -> GREEN
                else -> BLUE
            }
            ELEGANT -> when (rnd10()) {
                0, 1, 2 -> WHITE
                3 -> CREAM
                4, 5, 6 -> BLACK
                7 -> GREEN
                8 -> GREY
                else -> BLUE
            }
            PROVOCATIVE -> when (rnd10()) {
                in 0..4 -> BLACK
                else -> RED
            }
            WHOLESOME -> when (rnd10()) {
                0, 1, 2 -> WHITE
                3 -> CREAM
                4 -> PINK
                5 -> BLACK
                6 -> BROWN
                7, 8 -> PURPLE
                else -> BLUE
            }
            BUSINESSLIKE -> when (rnd10()) {
                0 -> WHITE
                in 1..5 -> BLACK
                6 -> BROWN
                7, 8 -> GREY
                else -> BLUE
            }
            else -> when (rnd10()) {
                0, 1 -> WHITE
                2 -> PINK
                3, 4, 5 -> BLACK
                6 -> RED
                else -> when (rnd10()) {
                    0, 1 -> BLUE
                    2, 3 -> GREEN
                    4, 5 -> GREY
                    6 -> BROWN
                    7, 8 -> PURPLE
                    else -> YELLOW
                }
            }
        }

fun getPajamaColour(style: Style, silk: Boolean): Colour =
        if (silk) {
            when (style) {
                CUTE -> when (rnd10()) {
                    0, 1, 2 -> WHITE
                    3, 4 -> WHITE
                    5, 6, 7, 8 -> PINK
                    else -> YELLOW
                }
                CHEERFUL -> when (rnd10()) {
                    0 -> WHITE
                    1, 2, 3 -> RED
                    4, 5 -> PINK
                    else -> YELLOW
                }
                PROVOCATIVE -> when (rnd10()) {
                    0 -> WHITE
                    1, 2 -> CREAM
                    3, 4, 5, 6 -> BLACK
                    else -> RED
                }
                else -> when (rnd10()) {
                    0, 1 -> WHITE
                    2, 3, 4 -> CREAM
                    5, 6 -> BLACK
                    7 -> RED
                    8 -> PINK
                    else -> YELLOW
                }
            }
        } else {
            when (style) {
                CUTE -> when (rnd10()) {
                    0 -> WHITE
                    1, 2, 3, 4, 5 -> PINK
                    6 -> BLACK
                    7 -> GREEN
                    8 -> BLUE
                    else -> YELLOW
                }
                CHEERFUL -> when (rnd10()) {
                    0 -> WHITE
                    1 -> PINK
                    2, 3 -> RED
                    4 -> GREEN
                    5 -> BLUE
                    6, 7 -> ORANGE
                    else -> YELLOW
                }
            //todo PROVOCATIVE
                else -> when (rnd10()) {
                    0 -> WHITE
                    1, 2 -> PINK
                    3 -> BLACK
                    4 -> RED
                    5 -> GREEN
                    6, 7 -> BLUE
                    8 -> GREY
                    else -> YELLOW
                }
            }
        }

fun rnd10() = ThreadLocalRandom.current().nextInt(10)
