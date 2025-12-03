package com.motorbikebe.business.admin.dashboard.impl;

import com.motorbikebe.business.admin.dashboard.service.DashboardService;
import com.motorbikebe.dto.business.admin.dashboard.*;
import com.motorbikebe.repository.business.admin.CarRepository;
import com.motorbikebe.repository.business.admin.ContractRepository;
import com.motorbikebe.repository.projection.ContractRevenueProjection;
import com.motorbikebe.repository.projection.TopCarRentalProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContractRepository contractRepository;
    private final CarRepository carRepository;

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

        long totalContracts = contractRepository.countContractsByBranchAndDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));

        long totalCars = contractRepository.countRentedCarsByBranchAndDate(
                branchId, toDate(currentMonthStart), toDate(nextMonthStart));

        DashboardPerformanceDTO performance = DashboardPerformanceDTO.builder()
                .totalContracts(totalContracts)
                .totalCars(totalCars)
                .totalRevenue(thisMonthRevenue.getTotalAmount())
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
}

