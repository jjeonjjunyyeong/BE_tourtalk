package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommentStatus {
	ACTIVE("공개"), INACTIVE("비공개"), DELETED("삭제");
	
	private final String desc;

	CommentStatus(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return desc;
    }
    
    @JsonCreator
    public static CommentStatus from(String value) {
        return CommentStatus.valueOf(value.toUpperCase());
    }
}
