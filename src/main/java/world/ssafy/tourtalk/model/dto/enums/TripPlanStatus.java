package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TripPlanStatus {
    DRAFT("임시저장"),
    COMPLETED("완료"),
    DELETED("삭제");

    private final String desc;

    TripPlanStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    @JsonCreator
    public static TripPlanStatus from(String value) {
        return TripPlanStatus.valueOf(value.toUpperCase());
    }
}