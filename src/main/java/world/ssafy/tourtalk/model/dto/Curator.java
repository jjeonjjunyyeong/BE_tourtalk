package world.ssafy.tourtalk.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curator {
	private int mno;
	private String curatorNo;
	private String curatorImg;
	private String adGrade;
	private LocalDateTime approvedAt;
}
