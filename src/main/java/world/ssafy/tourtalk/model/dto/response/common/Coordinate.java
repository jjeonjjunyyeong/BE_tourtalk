package world.ssafy.tourtalk.model.dto.response.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String name;
	
	public BigDecimal getX() {
		return longitude;
	}
	
	public BigDecimal getY() {
		return latitude;
	}
	
	public void setX(BigDecimal x) {
		this.longitude = x;
	}
	
	public void setY(BigDecimal y) {
		this.latitude = y;
	}
	
	public boolean isValid() {
		if (latitude == null || longitude == null) {
			return false;
		}
		
		if(latitude.compareTo(BigDecimal.valueOf(-90)) < 0 
				|| latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
			return false;
		}
		
		if(longitude.compareTo(BigDecimal.valueOf(-180)) < 0 
				|| longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
			return false;
		}
		
		return true;
	}
	public double distanceTo(Coordinate other) {
        if (other == null || !this.isValid() || !other.isValid()) {
            return 0.0;
        }
        
        final double EARTH_RADIUS = 6371.0; // 지구 반지름 (km)
        
        double lat1Rad = Math.toRadians(this.latitude.doubleValue());
        double lat2Rad = Math.toRadians(other.latitude.doubleValue());
        double deltaLatRad = Math.toRadians(other.latitude.subtract(this.latitude).doubleValue());
        double deltaLonRad = Math.toRadians(other.longitude.subtract(this.longitude).doubleValue());
        
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    public String toCoordinateString() {
        if (!isValid()) {
            return "0,0";
        }
        return latitude.setScale(6, RoundingMode.HALF_UP) + "," + 
               longitude.setScale(6, RoundingMode.HALF_UP);
    }
    
    public String toKakaoMapUrl() {
        if (!isValid()) {
            return "https://map.kakao.com";
        }
        
        String placeName = (name != null && !name.trim().isEmpty()) ? name : "위치";
        return String.format("https://map.kakao.com/link/map/%s,%s,%s", 
                           placeName, latitude, longitude);
    }
    
    public static Coordinate of(double latitude, double longitude) {
        return Coordinate.builder()
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .build();
    }
    
    public static Coordinate of(double latitude, double longitude, String name) {
        return Coordinate.builder()
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .name(name)
                .build();
    }
    
    public static Coordinate fromString(String coordinateString) {
        if (coordinateString == null || coordinateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            String[] parts = coordinateString.split(",");
            if (parts.length >= 2) {
                return Coordinate.of(
                    Double.parseDouble(parts[0].trim()),
                    Double.parseDouble(parts[1].trim())
                );
            }
        } catch (NumberFormatException e) {
            // 파싱 실패 시 null 반환
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("Coordinate{name='%s', lat=%s, lng=%s}", 
                           name, latitude, longitude);
    }
}
