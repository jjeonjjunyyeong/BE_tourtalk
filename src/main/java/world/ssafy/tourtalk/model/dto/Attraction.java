package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Attraction {
	private int no;
	private String contentId;
	private String title;
	private String contentTypeName;	// 관광지 
	private String sido;	// 서울
	private String gugun;	// 종로구
	private String firstImage1;
	private String firstImage2;
	private int mapLevel;
	private float latitude;
	private float longitude;
	private String tel;
	private String addr;
	private String homepage;
	private String overview;
	private int viewCnt;
	
	public Attraction(int no, String title, String sido, int viewCnt) {
		super();
		this.no = no;
		this.title = title;
		this.sido = sido;
		this.viewCnt = viewCnt;
	}
}
