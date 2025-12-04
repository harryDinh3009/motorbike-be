# Script test API lịch đặt xe
# Sử dụng: .\test-schedule-api.ps1

$baseUrl = "http://localhost:8345/api"
$endpoint = "$baseUrl/a/contract-mng/schedule"

# Lưu ý: Cần thay YOUR_TOKEN_HERE bằng token thực tế từ login
$token = "YOUR_TOKEN_HERE"

# Test 1: Lấy lịch đặt xe tháng 11/2024, tất cả chi nhánh, tất cả trạng thái
Write-Host "`n=== Test 1: Tất cả chi nhánh, tất cả trạng thái ===" -ForegroundColor Cyan

$body1 = @{
    branchId = $null
    status = $null
    startDate = "2024-11-01"
    endDate = "2024-11-30"
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri $endpoint `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -Body $body1
    
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Số lượng items: $($response1.data.Count)" -ForegroundColor Green
    if ($response1.data.Count -gt 0) {
        Write-Host "`nVí dụ item đầu tiên:" -ForegroundColor Yellow
        $response1.data[0] | ConvertTo-Json -Depth 3
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

# Test 2: Lấy lịch đặt xe với filter status CONFIRMED
Write-Host "`n=== Test 2: Tất cả chi nhánh, chỉ CONFIRMED ===" -ForegroundColor Cyan

$body2 = @{
    branchId = $null
    status = "CONFIRMED"
    startDate = "2024-11-01"
    endDate = "2024-11-30"
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri $endpoint `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -Body $body2
    
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Số lượng items: $($response2.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Lấy lịch đặt xe với empty string (should work như null)
Write-Host "`n=== Test 3: Empty string (should work như null) ===" -ForegroundColor Cyan

$body3 = @{
    branchId = ""
    status = ""
    startDate = "2024-11-01"
    endDate = "2024-11-30"
} | ConvertTo-Json

try {
    $response3 = Invoke-RestMethod -Uri $endpoint `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -Body $body3
    
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Số lượng items: $($response3.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Test hoàn tất ===" -ForegroundColor Cyan

