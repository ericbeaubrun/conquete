package configuration;

public class DevConfig {
    /**
     * By default, the game uses the "FileInputStream()" method to read resources of the game, this constant
     * allows to change to read with "getResourceAsStream()" (it's needed to generate the jar file).
     * Note that if you leave this constant as true, you need to make sure that the "res" folder containing
     * the game's resource files is properly linked.
     */
    public static final boolean READ_RESOURCE_AS_STREAM = false;
}
