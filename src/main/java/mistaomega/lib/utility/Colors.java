package mistaomega.lib.utility;

/**
 * enum for different colors for text or colored objects :D
 *
 * @see <a href="https://bukkit.org/threads/colored-motd-codes.274484/">Colored MOTD Codes</a>
 */
public enum Colors
{
    BLACK("\u00a70"),
    WHITE("\u00a7f"),
    GRAY("\u00a77"),
    LIGHTRED("\u00a7C"),
    DARKRED("\u00a74"),
    BLUE("\u00a79"),
    GREEN("\u00a7A"),
    YELLOW("\u00a7E"),
    TURQUOISE("\u00a7B"),
    ;

    public final String color;

    Colors(String colorCode)
    {
        color = colorCode;
    }

    @Override
    public String toString()
    {
        return color;
    }
}
