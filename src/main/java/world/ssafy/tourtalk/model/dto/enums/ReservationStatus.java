package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReservationStatus {
	WAITING_FOR_PAYMENT("결제대기"),
    RESERVED("정상예약"),
    CANCELLED("취소됨"),
    COMPLETED("투어 완료");
	
    private final String desc;

    ReservationStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    @JsonCreator
    public static ReservationStatus from(String value) {
        return ReservationStatus.valueOf(value.toUpperCase());
    }
}