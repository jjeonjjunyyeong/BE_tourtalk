package world.ssafy.tourtalk.model.dto.enums;

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
}