package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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
    
    @JsonCreator
    public static BoardStatus from(String value) {
        return BoardStatus.valueOf(value.toUpperCase());
    }
}
