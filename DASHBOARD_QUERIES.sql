-- ============================================================
-- DASHBOARD QUERIES - MySQL Native SQL
-- ============================================================
-- File này chứa tất cả các query SQL được sử dụng trong Dashboard
-- Các query này được sử dụng trong ContractRepository.java
-- ============================================================

-- ============================================================
-- 1. ĐẾM SỐ HỢP ĐỒNG TRONG THÁNG
-- ============================================================
-- Mục đích: Đếm số hợp đồng trong tháng hiện tại
-- Điều kiện: status <> 'CANCELLED', lọc theo start_date và branchId
-- Sử dụng: Hiển thị "Số hợp đồng" trong chỉ số hiệu suất
-- ============================================================
SELECT COUNT(con.id)
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
  AND con.start_date >= :startDate
  AND con.start_date < :endDate;

-- Ví dụ cụ thể 1.1: Đếm số hợp đồng trong tháng 12/2024, chi nhánh cụ thể
SELECT COUNT(con.id)
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND con.pickup_branch_id = 'branch-001'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- Ví dụ cụ thể 1.2: Đếm số hợp đồng trong tháng 12/2024, tất cả chi nhánh
SELECT COUNT(con.id)
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- ============================================================
-- 2. ĐẾM SỐ XE ĐÃ THUÊ TRONG THÁNG
-- ============================================================
-- Mục đích: Đếm số bản ghi contract_car (số xe đã thuê)
-- Điều kiện: status <> 'CANCELLED', lọc theo start_date và branchId
-- Sử dụng: Hiển thị "Số xe đã thuê" trong chỉ số hiệu suất
-- ============================================================
SELECT COUNT(cc.id)
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
WHERE con.status <> 'CANCELLED'
  AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
  AND con.start_date >= :startDate
  AND con.start_date < :endDate;

-- Ví dụ cụ thể 2.1: Đếm số xe đã thuê trong tháng 12/2024, chi nhánh cụ thể
SELECT COUNT(cc.id)
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
WHERE con.status <> 'CANCELLED'
  AND con.pickup_branch_id = 'branch-001'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- Ví dụ cụ thể 2.2: Đếm số xe đã thuê trong tháng 12/2024, tất cả chi nhánh
SELECT COUNT(cc.id)
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
WHERE con.status <> 'CANCELLED'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- ============================================================
-- 3. TÍNH TỔNG DOANH THU (TỔNG DOANH THU ƯỚC TÍNH)
-- ============================================================
-- Mục đích: Tính tổng doanh thu trong tháng hiện tại
-- Điều kiện: status <> 'CANCELLED', lọc theo start_date và branchId
-- Sử dụng: Hiển thị "Tổng doanh thu ước tính" trong chỉ số hiệu suất
-- ============================================================
SELECT 
    COALESCE(SUM(con.final_amount), 0) AS contractAmount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
  AND con.start_date >= :startDate
  AND con.start_date < :endDate;

-- Ví dụ cụ thể 3.1: Tính tổng doanh thu trong tháng 12/2024, chi nhánh cụ thể
SELECT 
    COALESCE(SUM(con.final_amount), 0) AS contractAmount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND con.pickup_branch_id = 'branch-001'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- Ví dụ cụ thể 3.2: Tính tổng doanh thu trong tháng 12/2024, tất cả chi nhánh
SELECT 
    COALESCE(SUM(con.final_amount), 0) AS contractAmount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount
FROM contract con
WHERE con.status <> 'CANCELLED'
  AND con.start_date >= '2024-12-01'
  AND con.start_date < '2025-01-01';

-- ============================================================
-- 4. DOANH THU THEO NGÀY TRONG THÁNG (BIỂU ĐỒ)
-- ============================================================
-- Mục đích: Lấy doanh thu theo từng ngày trong tháng để hiển thị biểu đồ
-- Điều kiện: status = 'COMPLETED', lọc theo completed_date và branchId
-- Logic: Giống với báo cáo doanh thu theo ngày
-- Tính toán: totalRevenue = rentalAmount + surchargeAmount - discountAmount
-- Sử dụng: Hiển thị biểu đồ "Doanh thu theo ngày trong tháng này"
-- ============================================================
SELECT 
    DATE(con.completed_date) AS completedDate,
    COUNT(con.id) AS contractCount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount,
    COALESCE(SUM(con.discount_amount), 0) AS discountAmount
FROM contract con
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE(:startDate)
  AND DATE(con.completed_date) <= DATE(:endDate)
  AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
GROUP BY DATE(con.completed_date)
ORDER BY completedDate;

-- Ví dụ cụ thể 4.1: Doanh thu theo ngày trong tháng 12/2024, chi nhánh cụ thể
SELECT 
    DATE(con.completed_date) AS completedDate,
    COUNT(con.id) AS contractCount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount,
    COALESCE(SUM(con.discount_amount), 0) AS discountAmount
FROM contract con
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE('2024-12-01')
  AND DATE(con.completed_date) <= DATE('2024-12-31')
  AND con.pickup_branch_id = 'branch-001'
GROUP BY DATE(con.completed_date)
ORDER BY completedDate;

-- Ví dụ cụ thể 4.2: Doanh thu theo ngày trong tháng 12/2024, tất cả chi nhánh
SELECT 
    DATE(con.completed_date) AS completedDate,
    COUNT(con.id) AS contractCount,
    COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
    COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount,
    COALESCE(SUM(con.discount_amount), 0) AS discountAmount
FROM contract con
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE('2024-12-01')
  AND DATE(con.completed_date) <= DATE('2024-12-31')
GROUP BY DATE(con.completed_date)
ORDER BY completedDate;

-- ============================================================
-- 5. TOP 5 XE THUÊ NHIỀU NHẤT
-- ============================================================
-- Mục đích: Lấy top 5 mẫu xe được thuê nhiều nhất trong tháng
-- Điều kiện: status = 'COMPLETED', lọc theo completed_date và branchId
-- Logic: Giống với thống kê lượt thuê theo mẫu xe
-- Đếm: COUNT(cc.id) - đếm số bản ghi contract_car
-- Doanh thu: SUM(cc.total_amount) - tổng tiền thuê từ contract_car
-- Sử dụng: Hiển thị bảng "Top 5 xe thuê nhiều nhất"
-- ============================================================
SELECT 
    c.model AS model,
    COUNT(cc.id) AS rentalCount,
    COALESCE(SUM(cc.total_amount), 0) AS revenue
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
INNER JOIN car c ON cc.car_id = c.id
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE(:startDate)
  AND DATE(con.completed_date) <= DATE(:endDate)
  AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
GROUP BY c.model
ORDER BY rentalCount DESC, revenue DESC
LIMIT 5;

-- Ví dụ cụ thể 5.1: Top 5 xe thuê nhiều nhất trong tháng 12/2024, chi nhánh cụ thể
SELECT 
    c.model AS model,
    COUNT(cc.id) AS rentalCount,
    COALESCE(SUM(cc.total_amount), 0) AS revenue
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
INNER JOIN car c ON cc.car_id = c.id
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE('2024-12-01')
  AND DATE(con.completed_date) <= DATE('2024-12-31')
  AND con.pickup_branch_id = 'branch-001'
GROUP BY c.model
ORDER BY rentalCount DESC, revenue DESC
LIMIT 5;

-- Ví dụ cụ thể 5.2: Top 5 xe thuê nhiều nhất trong tháng 12/2024, tất cả chi nhánh
SELECT 
    c.model AS model,
    COUNT(cc.id) AS rentalCount,
    COALESCE(SUM(cc.total_amount), 0) AS revenue
FROM contract_car cc
INNER JOIN contract con ON cc.contract_id = con.id
INNER JOIN car c ON cc.car_id = c.id
WHERE con.status = 'COMPLETED'
  AND con.completed_date IS NOT NULL
  AND DATE(con.completed_date) >= DATE('2024-12-01')
  AND DATE(con.completed_date) <= DATE('2024-12-31')
GROUP BY c.model
ORDER BY rentalCount DESC, revenue DESC
LIMIT 5;

-- ============================================================
-- 6. KIỂM TRA XE KHẢ DỤNG HIỆN TẠI
-- ============================================================
-- Mục đích: Đếm số xe khả dụng hiện tại (có thể cho thuê ngay)
-- Điều kiện: 
--   - status = 'AVAILABLE'
--   - Không đang trong hợp đồng có status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
--   - Lọc theo branchId (chi nhánh của xe)
-- Logic: Xe khả dụng là xe có status AVAILABLE và không bị trùng với 
--        các hợp đồng đang hoạt động (CONFIRMED, DELIVERED, RETURNED)
-- Sử dụng: Kiểm tra số lượng xe có sẵn để cho thuê
-- ============================================================
SELECT COUNT(c.id) AS availableCarCount
FROM car c
WHERE c.status = 'AVAILABLE'
  AND (:branchId IS NULL OR :branchId = '' OR c.branch_id = :branchId)
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  );

-- Ví dụ cụ thể 6.1: Đếm số xe khả dụng hiện tại, chi nhánh cụ thể
SELECT COUNT(c.id) AS availableCarCount
FROM car c
WHERE c.status = 'AVAILABLE'
  AND c.branch_id = 'branch-001'
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  );

-- Ví dụ cụ thể 6.2: Đếm số xe khả dụng hiện tại, tất cả chi nhánh
SELECT COUNT(c.id) AS availableCarCount
FROM car c
WHERE c.status = 'AVAILABLE'
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  );

-- ============================================================
-- 7. LIỆT KÊ XE KHẢ DỤNG HIỆN TẠI (CHI TIẾT)
-- ============================================================
-- Mục đích: Lấy danh sách chi tiết các xe khả dụng hiện tại
-- Điều kiện: Giống query 6, nhưng trả về thông tin chi tiết của từng xe
-- Sử dụng: Hiển thị danh sách xe có sẵn để cho thuê
-- ============================================================
SELECT 
    c.id,
    c.model,
    c.license_plate AS licensePlate,
    c.car_type AS carType,
    c.branch_id AS branchId,
    b.name AS branchName,
    c.daily_price AS dailyPrice,
    c.hourly_price AS hourlyPrice,
    c.condition,
    c.current_odometer AS currentOdometer,
    c.status,
    c.image_url AS imageUrl,
    c.note,
    c.year_of_manufacture AS yearOfManufacture,
    c.origin,
    c.value,
    c.frame_number AS frameNumber,
    c.engine_number AS engineNumber,
    c.color,
    c.registration_number AS registrationNumber,
    c.registered_owner_name AS registeredOwnerName,
    c.registration_place AS registrationPlace,
    c.insurance_contract_number AS insuranceContractNumber,
    c.insurance_expiry_date AS insuranceExpiryDate
FROM car c
LEFT JOIN branch b ON c.branch_id = b.id
WHERE c.status = 'AVAILABLE'
  AND (:branchId IS NULL OR :branchId = '' OR c.branch_id = :branchId)
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  )
ORDER BY c.model ASC, c.license_plate ASC;

-- Ví dụ cụ thể 7.1: Liệt kê xe khả dụng hiện tại, chi nhánh cụ thể
SELECT 
    c.id,
    c.model,
    c.license_plate AS licensePlate,
    c.car_type AS carType,
    c.branch_id AS branchId,
    b.name AS branchName,
    c.daily_price AS dailyPrice,
    c.hourly_price AS hourlyPrice,
    c.condition,
    c.current_odometer AS currentOdometer,
    c.status,
    c.image_url AS imageUrl,
    c.note,
    c.year_of_manufacture AS yearOfManufacture,
    c.origin,
    c.value,
    c.frame_number AS frameNumber,
    c.engine_number AS engineNumber,
    c.color,
    c.registration_number AS registrationNumber,
    c.registered_owner_name AS registeredOwnerName,
    c.registration_place AS registrationPlace,
    c.insurance_contract_number AS insuranceContractNumber,
    c.insurance_expiry_date AS insuranceExpiryDate
FROM car c
LEFT JOIN branch b ON c.branch_id = b.id
WHERE c.status = 'AVAILABLE'
  AND c.branch_id = 'branch-001'
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  )
ORDER BY c.model ASC, c.license_plate ASC;

-- Ví dụ cụ thể 7.2: Liệt kê xe khả dụng hiện tại, tất cả chi nhánh
SELECT 
    c.id,
    c.model,
    c.license_plate AS licensePlate,
    c.car_type AS carType,
    c.branch_id AS branchId,
    b.name AS branchName,
    c.daily_price AS dailyPrice,
    c.hourly_price AS hourlyPrice,
    c.condition,
    c.current_odometer AS currentOdometer,
    c.status,
    c.image_url AS imageUrl,
    c.note,
    c.year_of_manufacture AS yearOfManufacture,
    c.origin,
    c.value,
    c.frame_number AS frameNumber,
    c.engine_number AS engineNumber,
    c.color,
    c.registration_number AS registrationNumber,
    c.registered_owner_name AS registeredOwnerName,
    c.registration_place AS registrationPlace,
    c.insurance_contract_number AS insuranceContractNumber,
    c.insurance_expiry_date AS insuranceExpiryDate
FROM car c
LEFT JOIN branch b ON c.branch_id = b.id
WHERE c.status = 'AVAILABLE'
  AND NOT EXISTS (
      SELECT 1
      FROM contract_car cc
      INNER JOIN contract con ON cc.contract_id = con.id
      WHERE cc.car_id = c.id
        AND con.status IN ('CONFIRMED', 'DELIVERED', 'RETURNED')
        AND CURDATE() BETWEEN con.start_date AND con.end_date
  )
ORDER BY c.model ASC, c.license_plate ASC;

-- ============================================================
-- TÓM TẮT LOGIC:
-- ============================================================
-- - Số hợp đồng & Số xe đã thuê: Tính theo start_date, status <> 'CANCELLED'
-- - Tổng doanh thu ước tính: Tính theo start_date, status <> 'CANCELLED'
-- - Doanh thu theo ngày: Tính theo completed_date, status = 'COMPLETED'
-- - Top 5 xe: Tính theo completed_date, status = 'COMPLETED'
-- - Xe khả dụng hiện tại: status = 'AVAILABLE' và không đang trong hợp đồng đang hoạt động
--
-- Tất cả các query đều hỗ trợ filter theo branchId:
-- - branchId = NULL hoặc branchId = '': Lấy tất cả chi nhánh
-- - branchId có giá trị: Chỉ lấy dữ liệu của chi nhánh đó
--   + Query 1-5: Theo pickup_branch_id (chi nhánh thuê)
--   + Query 6-7: Theo branch_id (chi nhánh của xe)
-- ============================================================

-- ============================================================
-- LƯU Ý:
-- ============================================================
-- - Tất cả các ví dụ trên sử dụng giá trị cụ thể:
--   + branchId = 'branch-001' (chi nhánh cụ thể)
--   + startDate = '2024-12-01' (ngày bắt đầu tháng 12/2024)
--   + endDate = '2025-01-01' hoặc '2024-12-31' (ngày kết thúc)
--
-- - Để chạy với dữ liệu thực tế, bạn cần thay thế:
--   + 'branch-001' bằng ID chi nhánh thực tế trong database
--   + '2024-12-01' và '2024-12-31' bằng ngày tháng bạn muốn truy vấn
--
-- - Các query có thể chạy trực tiếp trên MySQL Workbench hoặc bất kỳ MySQL client nào
-- ============================================================

