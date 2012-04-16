package org.Zeitline.GUI.Graphics.Coloring;

public class FormatDataEntry {
    private String sourceType;
    private String source;
    private String shortDesc;
    private String desc;
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    // The fields that will be evaluated while coloring
    private static enum FIELDS {
        SourceType(0),
        Source (1),
        Short (3),
        Desc (7);

        private final int code;

        FIELDS(int i) {
            code = i;
        }

        public int getCode(){
            return code;
        }

        public static final int size = 13;

    }
    public FormatDataEntry(String description) {
        String fields[] = description.split("\n");

        if (fields.length != FIELDS.size)
            return;

        this.sourceType = fields[FIELDS.SourceType.getCode()];
        this.source = fields[FIELDS.Source.getCode()];
        this.shortDesc = fields[FIELDS.Short.getCode()];
        this.desc = fields[FIELDS.Desc.getCode()];

        valid = true;
    }

    public String getSourceType() {
        return sourceType.split(": ")[1];
    }

    public String getSource() {
        return source.split(": ")[1];
    }

    public String getShortDesc() {
        return shortDesc.split(": ")[1];
    }

    public String getDesc() {
        return desc.split(": ")[1];
    }
}
