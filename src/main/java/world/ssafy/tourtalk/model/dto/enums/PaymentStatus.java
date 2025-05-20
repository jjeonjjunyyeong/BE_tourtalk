package world.ssafy.tourtalk.model.dto.enums;

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
}