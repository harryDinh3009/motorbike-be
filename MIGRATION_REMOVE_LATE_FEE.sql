-- Migration: Xóa cột late_fee khỏi bảng contract_car
-- Date: 2024-12-11
-- Description: Xóa cột tiền phạt do trả muộn (late_fee) khỏi bảng contract_car

-- Xóa cột late_fee
ALTER TABLE `contract_car` 
DROP COLUMN `late_fee`;

