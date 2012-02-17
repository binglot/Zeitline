package org.Zeitline.OpenFileFilters;

final class ZeitlineProject extends AbstractFileFilter {
    private static final String NAME = "Zeitline Project";
    private static final String FILE_EXTENSION = ".ztl";

    public ZeitlineProject() {
        super(NAME, FILE_EXTENSION);
    }

}
