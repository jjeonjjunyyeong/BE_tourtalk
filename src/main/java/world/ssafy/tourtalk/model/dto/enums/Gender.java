package world.ssafy.tourtalk.model.dto.enums;

public enum Gender {
    UNKNOWN("비공개"),
    MAN("남자"),
    WOMAN("여자");

    private final String desc;

    Gender(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}