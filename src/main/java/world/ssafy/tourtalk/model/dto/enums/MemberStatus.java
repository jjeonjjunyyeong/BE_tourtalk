package world.ssafy.tourtalk.model.dto.enums;

public enum MemberStatus {
    ACTIVE("정상"),
    SUSPENDED("정지"),
    PENDING("대기"),
    DELETED("탈퇴");

    private final String desc;

    MemberStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}