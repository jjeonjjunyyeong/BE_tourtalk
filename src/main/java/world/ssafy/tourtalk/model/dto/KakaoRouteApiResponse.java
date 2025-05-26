package world.ssafy.tourtalk.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KakaoRouteApiResponse {
    @JsonProperty("trans_id")
    private String transId;
    
    private List<Route> routes;
    
    @Data
    public static class Route {
        @JsonProperty("result_code")
        private Integer resultCode;
        
        @JsonProperty("result_msg")
        private String resultMsg;
        
        private Summary summary;
        private List<Section> sections;
        
        @Data
        public static class Summary {
            private Origin origin;
            private Destination destination;
            private List<Waypoint> waypoints;
            private Integer priority;
            private Bound bound;
            private Fare fare;
            private Integer distance;
            private Integer duration;
        }
        
        @Data
        public static class Section {
            private Integer distance;
            private Integer duration;
            private Bound bound;
            private List<Road> roads;
        }
        
        @Data
        public static class Road {
            private String name;
            private Integer distance;
            private Integer duration;
            @JsonProperty("traffic_speed")
            private Double trafficSpeed;
            @JsonProperty("traffic_state")
            private Integer trafficState;
            private List<List<Double>> vertexes; // [x, y] 좌표 배열
        }
        
        @Data
        public static class Origin {
            private String name;
            private Double x;
            private Double y;
        }
        
        @Data
        public static class Destination {
            private String name;
            private Double x;
            private Double y;
        }
        
        @Data
        public static class Waypoint {
            private String name;
            private Double x;
            private Double y;
        }
        
        @Data
        public static class Bound {
            @JsonProperty("min_x")
            private Double minX;
            @JsonProperty("min_y")
            private Double minY;
            @JsonProperty("max_x")
            private Double maxX;
            @JsonProperty("max_y")
            private Double maxY;
        }
        
        @Data
        public static class Fare {
            private Integer taxi;
            private Integer toll;
        }
    }
}
