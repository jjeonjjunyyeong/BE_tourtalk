package world.ssafy.tourtalk.model.service;

import world.ssafy.tourtalk.model.dto.request.route.RouteRequestDto;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;

public interface RouteService {
	/**
     * 경로 검색
     * @param requestDto 경로 요청 정보
     * @return 경로 응답 정보
     * @throws Exception 경로 검색 실패 시
     */
	RouteResponseDto getRoute(RouteRequestDto requestDto) throws Exception;

}
