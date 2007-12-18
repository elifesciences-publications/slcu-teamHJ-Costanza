package costanza;

public enum DataId {

    CENTERS(DataGroup.CELL),
    BOAS(DataGroup.CELL),
    NEIGHBORS(DataGroup.CELL),
    INTENSITIES(DataGroup.CELL),
    BACKGROUND(DataGroup.STACK);
    private final DataGroup group;

    DataId(DataGroup g) {
        group = g;
    }

    public DataGroup getGroup() {
        return group;
    }
}
