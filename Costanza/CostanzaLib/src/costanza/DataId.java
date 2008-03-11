package costanza;

public enum DataId {

    CENTERS(DataGroup.CELL),
    INTENSITIES(DataGroup.CELL),
//    BOAS(DataGroup.CELL),
    NEIGHBORS(DataGroup.CELL),
//    BACKGROUND(DataGroup.STACK),
    PIXEL_FLAG(DataGroup.STACK);
    private final DataGroup group;

    DataId(DataGroup g) {
        group = g;
    }

    public DataGroup getGroup() {
        return group;
    }
}
