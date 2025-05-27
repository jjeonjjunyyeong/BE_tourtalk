package world.ssafy.tourtalk.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BoardCategory {
	NOTICE("공지사항"),
	FREE("자유게시판"),
	QNA("QnA"),
	INQUIRY("문의 게시판"),
	REVIEW("리뷰");
	
	private final String desc;
	
	BoardCategory(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    @JsonValue  // 직렬화 시 사용할 값
    public String getValue() {
        return this.name();
    }
    
    @JsonCreator
    public static BoardCategory from(String value) {
        return BoardCategory.valueOf(value.toUpperCase());
    }
}
