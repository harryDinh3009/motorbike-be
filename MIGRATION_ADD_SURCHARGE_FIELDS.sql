-- Migration: Thêm các trường surcharge_type_id, quantity, unit_price vào bảng surcharge
-- Date: 2024
-- Description: Bổ sung thông tin chi tiết về phụ thu để lưu số lượng, đơn giá và ID loại phụ thu
-- Lưu ý: Không sử dụng foreign key constraint, chỉ lưu ID để tham chiếu

-- Thêm cột surcharge_type_id (ID loại phụ thu - lưu ID để tham chiếu, không có foreign key)
ALTER TABLE `surcharge` 
ADD COLUMN `surcharge_type_id` VARCHAR(36) COLLATE utf8mb4_unicode_ci NULL AFTER `contract_id`;

-- Thêm cột quantity (Số lượng)
ALTER TABLE `surcharge` 
ADD COLUMN `quantity` DECIMAL(10,2) NULL AFTER `description`;

-- Thêm cột unit_price (Đơn giá)
ALTER TABLE `surcharge` 
ADD COLUMN `unit_price` DECIMAL(15,2) NULL AFTER `quantity`;

