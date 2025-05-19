package world.ssafy.tourtalk.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.service.BoardService;

@SpringBootTest
@Transactional
public class BoardControllerTest {

	@Autowired
	private BoardService boardService;

	@Test
	void 게시글_검색_제목으로_정상작동() {
		SearchConditionRequest cond = SearchConditionRequest.builder().pageNumber(1).pageSize(10).keyword("여행")
				.keywordType("title").orderBy("created_at").orderDirection("DESC").build();

		PageResponse<BoardResponse> response = boardService.searchWithConditions(cond);

		assertThat(response).isNotNull();
		assertThat(response.getContent()).isNotEmpty();
		System.out.println("검색 결과 수: " + response.getTotalElements());
	}

}
