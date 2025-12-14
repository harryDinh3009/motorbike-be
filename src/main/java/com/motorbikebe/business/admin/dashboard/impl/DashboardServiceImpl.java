package com.motorbikebe.business.admin.dashboard.impl;

import com.motorbikebe.business.admin.dashboard.service.DashboardService;
import com.motorbikebe.dto.business.admin.dashboard.*;
import com.motorbikebe.repository.business.admin.CarRepository;
import com.motorbikebe.repository.business.admin.ContractRepository;
import com.motorbikebe.repository.business.admin.CustomerRepository;
import com.motorbikebe.repository.projection.ContractRevenueProjection;
import com.motorbikebe.repository.projection.TopCarRentalProjection;
import com.motorbikebe.repository.projection.TotalRevenueProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContractRepository contractRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    @Override
    public DashboardResponseDTO getDashboard(String branchId) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate currentMonthStart = today.withDayOfMonth(1);
        LocalDate nextMonthStart = currentMonthStart.plusMonths(1);
        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);

        DashboardRevenueBlockDTO todayRevenue = toRevenueBlock(
                contractRepository.sumRevenueByBranchAndDate(branchId, toDate(today), toDate(tomorrow)));

        DashboardRevenueBlockDTO thisMonthRevenue = toRevenueBlock(
                contractRepository.sumRevenueByBranchAndDate(branchId, toDate(currentMonthStart), toDate(nextMonthStart)));

        DashboardRevenueBlockDTO lastMonthRevenue = toRevenueBlock(
                contractRepository.sumRevenueByBranchAndDate(branchId, toDate(lastMonthStart), toDate(currentMonthStart)));

        // Tính 4 KPI theo yêu cầu mới
        // 1. Số hợp đồng hoàn thành (theo completed_date, status = COMPLETED)
        long completedContracts = contractRepository.countCompletedContractsByCompletedDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));

        // 2. Doanh thu (theo completed_date, status = COMPLETED)
        // Doanh thu = total_rental_amount + total_surcharge - discount_amount
        TotalRevenueProjection revenueProjection = contractRepository.sumTotalRevenueByCompletedDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (revenueProjection != null) {
            BigDecimal rentalAmount = defaultBigDecimal(revenueProjection.getRentalAmount());
            BigDecimal surchargeAmount = defaultBigDecimal(revenueProjection.getSurchargeAmount());
            BigDecimal discountAmount = defaultBigDecimal(revenueProjection.getDiscountAmount());
            totalRevenue = rentalAmount.add(surchargeAmount).subtract(discountAmount);
        }

        // 3. Số xe cho thuê (theo start_date, status <> 'CANCELLED')
        long totalCars = contractRepository.countRentedCarsByBranchAndDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));

        // 4. Số khách hàng mới (theo created_date)
        long newCustomers = customerRepository.countNewCustomersByDate(
                toDate(currentMonthStart), toDate(nextMonthStart));

        DashboardPerformanceDTO performance = DashboardPerformanceDTO.builder()
                .completedContracts(completedContracts)
                .totalRevenue(totalRevenue)
                .totalCars(totalCars)
                .newCustomers(newCustomers)
                .build();

        DashboardRevenueOverviewDTO overview = DashboardRevenueOverviewDTO.builder()
                .today(todayRevenue)
                .thisMonth(thisMonthRevenue)
                .lastMonth(lastMonthRevenue)
                .build();

        // Sử dụng logic giống báo cáo doanh thu theo ngày (dựa theo completed_date và status = COMPLETED)
        List<Object[]> rawDailyRevenue = contractRepository.sumDailyRevenueByCompletedDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));
        List<DashboardDailyRevenueDTO> daily = mapToDailyRevenueDTO(rawDailyRevenue, currentMonthStart, nextMonthStart);

        List<TopCarRentalProjection> topCarProjections = contractRepository.findTop5RentedCars(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));
        List<DashboardTopCarDTO> topCars = IntStream.range(0, topCarProjections.size())
                .mapToObj(i -> toTopCarDTO(topCarProjections.get(i), i + 1))
                .collect(Collectors.toList());

        return DashboardResponseDTO.builder()
                .performance(performance)
                .revenueOverview(overview)
                .dailyRevenue(daily)
                .topCars(topCars)
                .build();
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private DashboardRevenueBlockDTO toRevenueBlock(ContractRevenueProjection projection) {
        if (projection == null) {
            return DashboardRevenueBlockDTO.builder()
                    .contractAmount(BigDecimal.ZERO)
                    .rentalAmount(BigDecimal.ZERO)
                    .surchargeAmount(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal contractAmount = defaultBigDecimal(projection.getContractAmount());
        BigDecimal rentalAmount = defaultBigDecimal(projection.getRentalAmount());
        BigDecimal surchargeAmount = defaultBigDecimal(projection.getSurchargeAmount());

        return DashboardRevenueBlockDTO.builder()
                .contractAmount(contractAmount)
                .rentalAmount(rentalAmount)
                .surchargeAmount(surchargeAmount)
                .totalAmount(contractAmount)
                .build();
    }

    /**
     * Map dữ liệu doanh thu theo ngày từ Object[] sang DashboardDailyRevenueDTO
     * Logic giống với báo cáo doanh thu theo ngày
     */
    private List<DashboardDailyRevenueDTO> mapToDailyRevenueDTO(List<Object[]> raw, LocalDate startDate, LocalDate endDate) {
        // Convert raw data to map for quick lookup
        Map<LocalDate, DashboardDailyRevenueDTO> dataMap = new HashMap<>();
        for (Object[] obj : raw) {
            java.sql.Date sqlDate = (java.sql.Date) obj[0];
            LocalDate date = sqlDate.toLocalDate();
            BigDecimal rental = defaultBigDecimal((BigDecimal) obj[2]);
            BigDecimal surcharge = defaultBigDecimal((BigDecimal) obj[3]);
            BigDecimal discount = defaultBigDecimal((BigDecimal) obj[4]);
            // Tính tổng doanh thu = tiền thuê + phụ thu - giảm giá
            BigDecimal totalRevenue = rental.add(surcharge).subtract(discount);

            dataMap.put(date, DashboardDailyRevenueDTO.builder()
                    .date(date)
                    .contractAmount(totalRevenue) // Doanh thu thực thu
                    .rentalAmount(rental)
                    .surchargeAmount(surcharge)
                    .totalAmount(totalRevenue) // Tổng doanh thu hiển thị
                    .build());
        }

        // Generate all dates in range, fill with zero if no data
        List<DashboardDailyRevenueDTO> result = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            DashboardDailyRevenueDTO row = dataMap.get(date);
            if (row != null) {
                result.add(row);
            } else {
                result.add(DashboardDailyRevenueDTO.builder()
                        .date(date)
                        .contractAmount(BigDecimal.ZERO)
                        .rentalAmount(BigDecimal.ZERO)
                        .surchargeAmount(BigDecimal.ZERO)
                        .totalAmount(BigDecimal.ZERO)
                        .build());
            }
        }

        return result;
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private DashboardTopCarDTO toTopCarDTO(TopCarRentalProjection projection, int rank) {
        if (projection == null) {
            return null;
        }

        return DashboardTopCarDTO.builder()
                .rank(rank)
                .model(projection.getModel())
                .rentalCount(projection.getRentalCount() != null ? projection.getRentalCount() : 0L)
                .revenue(defaultBigDecimal(projection.getRevenue()))
                .build();
    }

    @Override
    public DashboardRevenueChartDTO getRevenueChart(String branchId, String period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today.plusDays(1);
        boolean isMonthly = false;
        
        switch (period) {
            case "7":
                startDate = today.minusDays(7);
                break;
            case "30":
                startDate = today.minusDays(30);
                break;
            case "year":
                startDate = today.withDayOfYear(1); // Đầu năm
                isMonthly = true;
                break;
            default:
                startDate = today.minusDays(30);
        }
        
        if (isMonthly) {
            // Lấy doanh thu theo tháng trong năm
            int currentYear = today.getYear();
            List<Object[]> rawMonthly = contractRepository.sumMonthlyRevenue(currentYear, branchId);
            return mapToMonthlyChartDTO(rawMonthly, period);
        } else {
            // Lấy doanh thu theo ngày
            List<Object[]> rawDaily = contractRepository.sumDailyRevenueByCompletedDate(
                branchId, toDate(startDate), toDate(endDate));
            return mapToDailyChartDTO(rawDaily, startDate, endDate, period);
        }
    }

    private DashboardRevenueChartDTO mapToDailyChartDTO(List<Object[]> raw, LocalDate startDate, LocalDate endDate, String period) {
        Map<LocalDate, BigDecimal> dataMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Map tất cả data từ raw vào dataMap
        for (Object[] obj : raw) {
            java.sql.Date sqlDate = (java.sql.Date) obj[0];
            LocalDate date = sqlDate.toLocalDate();
            BigDecimal rental = defaultBigDecimal((BigDecimal) obj[2]);
            BigDecimal surcharge = defaultBigDecimal((BigDecimal) obj[3]);
            BigDecimal discount = defaultBigDecimal((BigDecimal) obj[4]);
            BigDecimal totalRevenue = rental.add(surcharge).subtract(discount);
            dataMap.put(date, totalRevenue);
        }

        List<ChartDataPointDTO> result = new ArrayList<>();
        
        // Fill TẤT CẢ các ngày từ startDate đến endDate (kể cả ngày không có data)
        // endDate là ngày hôm nay + 1, nên ta fill đến hôm nay
        LocalDate today = LocalDate.now();
        LocalDate fillEndDate = endDate.isAfter(today) ? today : endDate.minusDays(1);
        
        for (LocalDate date = startDate; !date.isAfter(fillEndDate); date = date.plusDays(1)) {
            BigDecimal revenue = dataMap.getOrDefault(date, BigDecimal.ZERO);
            result.add(ChartDataPointDTO.builder()
                    .label(date.format(formatter))
                    .revenue(revenue)
                    .build());
        }

        return DashboardRevenueChartDTO.builder()
                .period(period)
                .data(result)
                .build();
    }

    private DashboardRevenueChartDTO mapToMonthlyChartDTO(List<Object[]> raw, String period) {
        Map<Integer, BigDecimal> dataMap = new HashMap<>();
        String[] monthNames = {"", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        
        for (Object[] obj : raw) {
            Integer month = ((Number) obj[0]).intValue();
            BigDecimal rental = defaultBigDecimal((BigDecimal) obj[2]);
            BigDecimal surcharge = defaultBigDecimal((BigDecimal) obj[3]);
            BigDecimal discount = defaultBigDecimal((BigDecimal) obj[4]);
            BigDecimal totalRevenue = rental.add(surcharge).subtract(discount);
            dataMap.put(month, totalRevenue);
        }

        List<ChartDataPointDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = dataMap.getOrDefault(month, BigDecimal.ZERO);
            result.add(ChartDataPointDTO.builder()
                    .label(monthNames[month])
                    .revenue(revenue)
                    .build());
        }

        return DashboardRevenueChartDTO.builder()
                .period(period)
                .data(result)
                .build();
    }
}

