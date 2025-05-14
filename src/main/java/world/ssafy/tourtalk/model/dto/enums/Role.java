package world.ssafy.tourtalk.model.dto.enums;

public enum Role {
    USER("일반회원"),
    CURATOR("학예사"),
    ADMIN("관리자");

    private final String desc;

    Role(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}