-- Migration: Thêm cột vehicle_code vào bảng car
-- Date: 2024
-- Description: Bổ sung mã xe tự sinh theo format XE0001, XE0002, v.v.

-- Thêm cột vehicle_code
ALTER TABLE `car`
ADD COLUMN `vehicle_code` VARCHAR(50) COLLATE utf8mb4_unicode_ci UNIQUE NULL COMMENT 'Mã xe tự sinh (XE0001, XE0002, ...)';

-- Tạo index cho vehicle_code để tìm kiếm nhanh
CREATE INDEX idx_vehicle_code ON `car` (`vehicle_code`);

-- Tạo bảng car_model nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS `car_model` (
  `id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `brand` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `created_date` datetime(6) NOT NULL,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_car_model_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục mẫu xe dùng cho các nghiệp vụ';

-- Thêm một số dữ liệu mẫu cho car_model
INSERT IGNORE INTO `car_model` (`id`, `name`, `brand`, `description`, `is_active`, `created_date`, `created_by`)
VALUES
('model-001', 'Honda Wave Alpha', 'Honda', 'Xe số phổ thông', 1, NOW(), 'system'),
('model-002', 'Honda Vision', 'Honda', 'Xe ga phổ thông', 1, NOW(), 'system'),
('model-003', 'Yamaha Sirius', 'Yamaha', 'Xe số thể thao', 1, NOW(), 'system'),
('model-004', 'Yamaha PG-1', 'Yamaha', 'Xe ga cao cấp', 1, NOW(), 'system'),
('model-005', 'Suzuki Raider', 'Suzuki', 'Xe tay côn', 1, NOW(), 'system');
