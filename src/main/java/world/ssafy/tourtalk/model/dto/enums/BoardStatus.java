package world.ssafy.tourtalk.model.dto.enums;

public enum BoardStatus {
	ACTIVE("공개"),
	INACTIVE("비공개"),
	DELETED("삭제");
	
	private final String desc;
	
	BoardStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
