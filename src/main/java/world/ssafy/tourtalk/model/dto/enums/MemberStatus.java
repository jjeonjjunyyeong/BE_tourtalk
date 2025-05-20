package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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
    
    @JsonCreator
    public static MemberStatus from(String value) {
        return MemberStatus.valueOf(value.toUpperCase());
    }
}