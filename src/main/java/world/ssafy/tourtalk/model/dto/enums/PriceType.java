package world.ssafy.tourtalk.model.dto.enums;

public enum PriceType {
    TOTAL("총합"),
    PER_PERSON("1인 비용");

    private final String desc;

    PriceType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
