-- Migration: Cập nhật vehicle_code cho các xe cũ chưa có mã xe
-- Date: 2024
-- Description: Tự sinh vehicle_code theo format XE0001, XE0002, ... cho các xe cũ

-- Cập nhật vehicle_code cho các xe chưa có (nếu có)
SET @row_number = 0;
UPDATE car
SET vehicle_code = CONCAT('XE', LPAD(
    (SELECT @row_number := @row_number + 1) +
    (SELECT COALESCE(MAX(CAST(SUBSTRING(vehicle_code, 3) AS UNSIGNED)), 0)
     FROM (SELECT vehicle_code FROM car WHERE vehicle_code IS NOT NULL AND vehicle_code LIKE 'XE%') AS temp),
    4, '0'
))
WHERE vehicle_code IS NULL
ORDER BY id;
