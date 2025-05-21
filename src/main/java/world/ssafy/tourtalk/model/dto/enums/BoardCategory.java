package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BoardCategory {
	ACTIVE("공개"),
	INACTIVE("비공개"),
	DELETED("삭제");
	
	private final String desc;
	
	BoardCategory(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    @JsonCreator
    public static BoardCategory from(String value) {
        return BoardCategory.valueOf(value.toUpperCase());
    }
}
