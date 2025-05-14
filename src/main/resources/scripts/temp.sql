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
  PRIMARY KEY (`no`),
  FOREIGN KEY (`content_type_id`) REFERENCES `contenttypes` (`content_type_id`),
  FOREIGN KEY (`area_code`) REFERENCES `sidos` (`no`),
  FOREIGN KEY (`si_gun_gu_code`) REFERENCES `guguns` (`key`)
);