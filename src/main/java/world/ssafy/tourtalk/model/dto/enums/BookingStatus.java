package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookingStatus {
	PENDING_PAYMENT("결제 대기"), RESERVED("정상 예약"), CANCELLED("취소됨"), COMPLETED("투어 완료");

	private final String desc;

	BookingStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	@JsonCreator
	public static BookingStatus from(String value) {
		return BookingStatus.valueOf(value.toUpperCase());
	}
}
