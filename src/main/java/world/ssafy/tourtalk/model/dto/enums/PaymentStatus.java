package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentStatus {
    UNPAID("미결제"),
    PAID("결제완료"),
    REFUNDED("환불");
    private final String desc;

    PaymentStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    @JsonCreator
    public static PaymentStatus from(String value) {
        return PaymentStatus.valueOf(value.toUpperCase());
    }
}