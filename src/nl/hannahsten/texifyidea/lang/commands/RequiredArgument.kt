package nl.hannahsten.texifyidea.lang.commands

/**
 * @author Sten Wessel
 */
@Suppress("ConvertSecondaryConstructorToPrimary")
open class RequiredArgument : Argument {

    @JvmOverloads
    internal constructor(name: String, type: Type = Type.NORMAL) : super(name, type)

    override fun toString() = "{$name}"
}