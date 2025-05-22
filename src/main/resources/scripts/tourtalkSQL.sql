drop database tourtalktest;
create database tourtalktest;
use tourtalktest;

-- 회원 관련 테이블
-- 1. 회원 기본 정보
CREATE TABLE `member` (
  `mno` INT NOT NULL AUTO_INCREMENT,
  `id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(1000) NOT NULL,
  `nickname` VARCHAR(50) NULL,
  -- 사용자, 큐레이터, 관리자
  `role` ENUM('USER', 'CURATOR', 'ADMIN') NOT NULL DEFAULT 'USER',
  -- 정상 활동, 일시 정지, 승인 대기, 탈퇴 처리
  `status` ENUM('ACTIVE', 'SUSPENDED', 'PENDING', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  `points` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`mno`),
  UNIQUE KEY (`id`)
);

-- 2. 회원 상세 정보
CREATE TABLE `member_details` (
  `mno` INT NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(13) NOT NULL,
  -- 비공개, 남자, 여자
  `gender` ENUM('UNKNOWN', 'MAN', 'WOMAN') NOT NULL DEFAULT 'UNKNOWN',
  `address` VARCHAR(200) NULL,
  `postal_code` VARCHAR(20) NULL,
  `birth_date` DATE NULL,
  `profile_img_path` VARCHAR(255) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` DATETIME NULL,
  PRIMARY KEY (`mno`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 3. 큐레이터 정보 (테이블명 수정: Untitled -> curator)
CREATE TABLE `curator` (
  `mno` INT NOT NULL,
  `curator_no` VARCHAR(50) NULL,
  `curator_img` VARCHAR(255) NOT NULL,
  `ad_grade` VARCHAR(255) NULL,
  `approved_at` DATETIME NULL,
  PRIMARY KEY (`mno`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 게시판 관련 테이블
-- 4. 게시판 카테고리 삭제

-- 5. 게시글
CREATE TABLE `board` (
  `post_id` INT NOT NULL AUTO_INCREMENT,
  -- 공지사항, 자유게시판, QnA, 문의, 리뷰
  `category` ENUM('NOTICE', 'FREE', 'QNA', 'INQUIRY', 'REVIEW') NOT NULL,
  `writer_id` INT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  -- 공개, 비공개, 삭제됨
  `status` ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  `view_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`post_id`),
  FOREIGN KEY (`writer_id`) REFERENCES `member` (`mno`)
);

-- 6. 게시글 상세 정보
CREATE TABLE `board_details` (
  `post_id` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  `deleted_at` DATETIME NULL,
  `file_path` VARCHAR(255) NULL,
  PRIMARY KEY (`post_id`),
  FOREIGN KEY (`post_id`) REFERENCES `board` (`post_id`)
);

-- 7. 댓글
CREATE TABLE `comments` (
  `comment_id` INT NOT NULL AUTO_INCREMENT,
  `post_id` INT NOT NULL,
  `writer_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  -- 공개, 비밀댓글, 삭제됨
  `status` ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`comment_id`),
  FOREIGN KEY (`post_id`) REFERENCES `board` (`post_id`),
  FOREIGN KEY (`writer_id`) REFERENCES `member` (`mno`)
);


-- 8. 게시글 좋아요
CREATE TABLE `post_likes` (
  `post_id` INT NOT NULL,
  `mno` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`, `mno`),
  FOREIGN KEY (`post_id`) REFERENCES `board` (`post_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 9. 평점
CREATE TABLE `rating` (
  `rating_id` INT NOT NULL AUTO_INCREMENT,
  `post_id` INT NOT NULL,
  `mno` INT NOT NULL,
  -- 상품, 큐레이터, 관광지
  `target_type` ENUM('PRODUCT', 'CURATOR', 'ATTRACTION') NOT NULL,
  `target_id` INT NOT NULL,
  `rating_value` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  -- 공개, 배공개, 삭제됨
  `status` ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`rating_id`),
  FOREIGN KEY (`post_id`) REFERENCES `board` (`post_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 채팅 관련 테이블
-- 10. 채팅방
CREATE TABLE `chat_rooms` (
  `room_id` INT NOT NULL AUTO_INCREMENT,
  `room_name` VARCHAR(30) NOT NULL,
  -- 비공개, 그룹, 공개
  `room_type` ENUM('PRIVATE', 'GROUP', 'PUBLIC') NOT NULL,
  `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mno` INT NOT NULL,
  PRIMARY KEY (`room_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 11. 사용자 채팅방
CREATE TABLE `user_rooms` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `room_id` INT NOT NULL,
  `mno` INT NOT NULL,
  `joined_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `role` ENUM('MEMBER', 'MANAGER') NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`room_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

-- 12. 메시지
-- message_type은 좀 고려해보기
CREATE TABLE `messages` (
  `message_id` INT NOT NULL AUTO_INCREMENT,
  `mno` INT NOT NULL,
  `room_id` INT NOT NULL,
  `content` VARCHAR(300) NOT NULL,
  `message_type` VARCHAR(20) NULL,
  `created_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`),
  FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`room_id`)
);

-- 여행 그룹 관련 테이블
-- 13. 여행 그룹
CREATE TABLE `travel_group` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `info` VARCHAR(255) NULL,
  `leader_id` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_public` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`group_id`),
  FOREIGN KEY (`leader_id`) REFERENCES `member` (`mno`)
);

-- 14. 그룹 멤버
CREATE TABLE `group_member` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `member_id` INT NOT NULL,
  `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `role` ENUM('MEMBER', 'MANAGER') NOT NULL DEFAULT 'MEMBER',
  PRIMARY KEY (`group_id`, `member_id`),
  FOREIGN KEY (`group_id`) REFERENCES `travel_group` (`group_id`),
  FOREIGN KEY (`member_id`) REFERENCES `member` (`mno`)
);

-- 15. 그룹 여행 계획
CREATE TABLE `group_trip_plan` (
  `plan_id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `title` VARCHAR(50) NOT NULL,
  `start_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  `info` VARCHAR(255) NULL,
  PRIMARY KEY (`plan_id`),
  FOREIGN KEY (`group_id`) REFERENCES `travel_group` (`group_id`)
);

-- 관광지 관련 테이블
-- 16. 시도
CREATE TABLE `sidos` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `sido_code` INT NULL,
  `sido_name` VARCHAR(20) NULL,
  PRIMARY KEY (`no`)
);

-- 17. 구군
CREATE TABLE `guguns` (
  `key` INT NOT NULL AUTO_INCREMENT,
  `sido_code` VARCHAR(255) NULL,
  `gugun_code` INT NULL,
  `gugun_name` VARCHAR(20) NULL,
  PRIMARY KEY (`key`)
);

-- 18. 콘텐츠 타입
CREATE TABLE `contenttypes` (
  `content_type_id` INT NOT NULL,
  `content_type_name` VARCHAR(45) NULL,
  PRIMARY KEY (`content_type_id`)
);

-- 19. 관광지
CREATE TABLE `attractions` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `content_id` INT NULL,
  `title` VARCHAR(500) NULL,
  `content_type_id` INT NOT NULL,
  `area_code` INT NOT NULL,
  `si_gun_gu_code` INT NOT NULL,
  `first_image1` VARCHAR(100) NULL,
  `first_image2` VARCHAR(100) NULL,
  `map_level` INT NULL,
  `latitude` DECIMAL(20,17) NULL,
  `longitude` DECIMAL(20,17) NULL,
  `tel` VARCHAR(20) NULL,
  `addr1` VARCHAR(100) NULL,
  `addr2` VARCHAR(100) NULL,
  `homepage` VARCHAR(1000) NULL,
  `overview` VARCHAR(10000) NULL,
  `view_cnt` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`no`),
  FOREIGN KEY (`content_type_id`) REFERENCES `contenttypes` (`content_type_id`),
  FOREIGN KEY (`area_code`) REFERENCES `sidos` (`no`),
  FOREIGN KEY (`si_gun_gu_code`) REFERENCES `guguns` (`key`)
);

-- 투어 상품 관련 테이블
-- 20. 투어 상품
CREATE TABLE `tour_product` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `mno` INT NOT NULL,
  `location_name` INT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` TEXT NULL,
  `max_participants` INT NOT NULL,
  `min_participants` INT NOT NULL DEFAULT 1,
  -- 전체 비용, 1인당 비용
  `price_type` ENUM('TOTAL', 'PER_PERSON') NOT NULL,
  `price` INT NOT NULL,
  `start_date` DATE NOT NULL,
  -- 비공개, 모집 중, 모집 완료, 취소됨
  `status` ENUM('DRAFT', 'OPEN', 'CLOSED', 'CANCELLED') NULL DEFAULT 'DRAFT',
  `thumbnail_img` VARCHAR(255) NULL,
  `tags` VARCHAR(255) NULL,
  `meeting_place` VARCHAR(255) NULL,
  `meeting_time` TIME NOT NULL,
  `duration` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`product_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`),
  FOREIGN KEY (`location_name`) REFERENCES `attractions` (`no`)
);

-- 21. 투어 예약
CREATE TABLE `tour_booking` (
  `booking_id` INT NOT NULL AUTO_INCREMENT,
  `mno` INT NOT NULL,
  `product_id` INT NOT NULL,
  `reserved_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `participant_count` INT NOT NULL,
  `total_price` INT NOT NULL,
  `payment_method` VARCHAR(50) NOT NULL,
  -- 미결제, 결제완료, 환불
  `payment_status` ENUM('UNPAID', 'PAID', 'REFUNDED') NULL DEFAULT 'UNPAID',
  -- 정상 예약, 취소됨, 투어 완료
  `status` ENUM('RESERVED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'RESERVED',
  `cancelled_at` DATETIME NULL,
  PRIMARY KEY (`booking_id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`),
  FOREIGN KEY (`product_id`) REFERENCES `tour_product` (`product_id`)
);

-- 여행 계획 테이블
CREATE TABLE `trip_plans` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mno` INT NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `description` TEXT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `total_distance` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `total_duration` INT NOT NULL DEFAULT 0,
  `status` ENUM('DRAFT', 'COMPLETED', 'DELETED') NOT NULL DEFAULT 'DRAFT',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`mno`) REFERENCES `member` (`mno`) ON DELETE CASCADE,
  INDEX `idx_mno_status` (`mno`, `status`),
  INDEX `idx_created_at` (`created_at`)
);

-- 여행 계획-관광지 연결 테이블
CREATE TABLE `trip_plan_attractions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `trip_plan_id` BIGINT NOT NULL,
  `attraction_no` INT NOT NULL,
  `visit_order` INT NOT NULL,
  `attraction_title` VARCHAR(500) NULL,
  `latitude` DECIMAL(20,17) NULL,
  `longitude` DECIMAL(20,17) NULL,
  `sido` VARCHAR(50) NULL,
  `gugun` VARCHAR(50) NULL,
  `addr` VARCHAR(200) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`trip_plan_id`) REFERENCES `trip_plans` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`) ON DELETE CASCADE,
  UNIQUE KEY `uk_trip_plan_attraction` (`trip_plan_id`, `attraction_no`),
  INDEX `idx_trip_plan_order` (`trip_plan_id`, `visit_order`)
);