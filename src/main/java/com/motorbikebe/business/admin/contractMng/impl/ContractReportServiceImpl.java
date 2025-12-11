package com.motorbikebe.business.admin.contractMng.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.motorbikebe.business.admin.contractMng.service.ContractReportService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.contractMng.DailyRevenueReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.DailyRevenueRowDTO;
import com.motorbikebe.dto.business.admin.contractMng.ModelRentalReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.ModelRentalRowDTO;
import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueRowDTO;
import com.motorbikebe.repository.business.admin.BranchRepository;
import com.motorbikebe.repository.business.admin.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractReportServiceImpl implements ContractReportService {

    private static final String STORE_NAME = "CỬA HÀNG CHO THUÊ XE MÁY MOTOGO";
    private static final String STORE_ADDRESS = "Địa chỉ: Tổ 1, Thôn Cầu Mè, Phương Thiện, Hà Giang, Việt Nam";
    private static final String STORE_PHONE = "SDT: 0859963204";

    private final ContractRepository contractRepository;
    private final BranchRepository branchRepository;

    @Override
    public List<MonthlyRevenueRowDTO> getMonthlyRevenueData(MonthlyRevenueReportRequestDTO request) {
        if (request.getYear() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        List<Object[]> raw = contractRepository.sumMonthlyRevenue(request.getYear(), request.getBranchId());
        return mapToRows(raw);
    }

    @Override
    public byte[] exportMonthlyRevenueReport(MonthlyRevenueReportRequestDTO request) {
        if (request.getYear() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        List<Object[]> raw = contractRepository.sumMonthlyRevenue(request.getYear(), request.getBranchId());
        List<MonthlyRevenueRowDTO> rows = mapToRows(raw);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20, 30, 40, 30);

            PdfFont font;
            PdfFont fontBold;
            try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/times.ttf");
                 InputStream fontBoldStream = getClass().getClassLoader().getResourceAsStream("fonts/timesbd.ttf")) {
                if (fontStream == null || fontBoldStream == null) {
                    throw new FileNotFoundException("Font files not found");
                }
                font = PdfFontFactory.createFont(fontStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                fontBold = PdfFontFactory.createFont(fontBoldStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
            }

            document.add(new Paragraph("MOTOGO")
                    .setFont(fontBold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(0));
            document.add(new Paragraph(STORE_NAME).setFont(fontBold).setFontSize(12).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_ADDRESS).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_PHONE).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(10));

            document.add(new Paragraph("BÁO CÁO DOANH THU THEO THÁNG")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            document.add(new Paragraph("Năm: " + request.getYear())
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            String branchLabel = resolveBranchName(request.getBranchId());
            document.add(new Paragraph("Chi nhánh: " + branchLabel)
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            float[] columnWidths = {70f, 90f, 120f, 120f, 100f, 120f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "Tháng", fontBold); // Căn giữa
            addHeaderCell(table, "Số HĐ hoàn thành", fontBold); // Căn giữa
            addRightAlignedHeaderCell(table, "Tiền thuê xe", fontBold);
            addRightAlignedHeaderCell(table, "Tiền phụ thu", fontBold);
            addRightAlignedHeaderCell(table, "Giảm giá", fontBold);
            addRightAlignedHeaderCell(table, "Tổng doanh thu", fontBold);

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            int totalContracts = 0;
            BigDecimal totalRental = BigDecimal.ZERO;
            BigDecimal totalSurcharge = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            if (rows.isEmpty()) {
                Cell emptyCell = new Cell(1, 6)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                for (MonthlyRevenueRowDTO row : rows) {
                    table.addCell(createBodyCell(String.valueOf(row.getMonth()), font)); // Căn giữa
                    table.addCell(createBodyCell(String.valueOf(row.getContractCount()), font)); // Căn giữa
                    table.addCell(createRightAlignedCell(formatCurrency(row.getRentalAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getSurchargeAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getDiscountAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getRevenue(), currencyFormat), font));

                    totalContracts += row.getContractCount();
                    totalRental = totalRental.add(orZero(row.getRentalAmount()));
                    totalSurcharge = totalSurcharge.add(orZero(row.getSurchargeAmount()));
                    totalDiscount = totalDiscount.add(orZero(row.getDiscountAmount()));
                    totalRevenue = totalRevenue.add(orZero(row.getRevenue()));
                }

                table.addCell(createFooterCell("Tổng", fontBold).setTextAlignment(TextAlignment.LEFT));
                table.addCell(createFooterCell(String.valueOf(totalContracts), fontBold)); // Căn giữa
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalRental, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalSurcharge, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalDiscount, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalRevenue, currencyFormat), fontBold));
            }

            document.add(table);

            document.add(new Paragraph("\n\n"));
            Table signTable = new Table(new float[]{1, 1});
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(createSignCell("Bên cho thuê\n(ký ghi rõ họ tên)", fontBold));
            signTable.addCell(createSignCell("Người lập báo cáo\n(ký ghi rõ họ tên)", fontBold));
            document.add(signTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting monthly revenue report", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<MonthlyRevenueRowDTO> mapToRows(List<Object[]> raw) {
        // Tạo map từ dữ liệu query
        Map<Integer, MonthlyRevenueRowDTO> dataMap = new HashMap<>();
        for (Object[] obj : raw) {
            int month = ((Number) obj[0]).intValue();
            int contractCount = ((Number) obj[1]).intValue();
            BigDecimal rental = (BigDecimal) obj[2];
            BigDecimal surcharge = (BigDecimal) obj[3];
            BigDecimal discount = (BigDecimal) obj[4];
            BigDecimal revenue = rental.add(surcharge).subtract(discount);
            
            dataMap.put(month, MonthlyRevenueRowDTO.builder()
                    .month(month)
                    .contractCount(contractCount)
                    .rentalAmount(rental)
                    .surchargeAmount(surcharge)
                    .discountAmount(discount)
                    .revenue(revenue)
                    .build());
        }

        // Fill đủ 12 tháng (tháng 1 - 12)
        List<MonthlyRevenueRowDTO> rows = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            MonthlyRevenueRowDTO row = dataMap.get(month);
            if (row != null) {
                rows.add(row);
            } else {
                // Tháng không có dữ liệu → fill 0
                rows.add(MonthlyRevenueRowDTO.builder()
                        .month(month)
                        .contractCount(0)
                        .rentalAmount(BigDecimal.ZERO)
                        .surchargeAmount(BigDecimal.ZERO)
                        .discountAmount(BigDecimal.ZERO)
                        .revenue(BigDecimal.ZERO)
                        .build());
            }
        }
        return rows;
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setTextAlignment(TextAlignment.CENTER));
    }

    private void addLeftAlignedHeaderCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
    }

    private void addRightAlignedHeaderCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell createBodyCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createRightAlignedCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell createFooterCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createRightAlignedFooterCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell createSignCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11).setTextAlignment(TextAlignment.CENTER))
                .setBorder(null);
    }

    private String formatCurrency(BigDecimal value, NumberFormat formatter) {
        if (value == null) {
            return "0";
        }
        return formatter.format(value);
    }

    private String resolveBranchName(String branchId) {
        if (StringUtils.isBlank(branchId)) {
            return "Tất cả chi nhánh";
        }
        return branchRepository.findById(branchId)
                .map(b -> b.getName())
                .orElse("Không xác định");
    }

    @Override
    public List<DailyRevenueRowDTO> getDailyRevenueData(DailyRevenueReportRequestDTO request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        List<Object[]> raw = contractRepository.sumDailyRevenueByCompletedDate(
                request.getBranchId(),
                request.getStartDate(),
                request.getEndDate()
        );

        return mapToDailyRows(raw, request.getStartDate(), request.getEndDate());
    }

    @Override
    public byte[] exportDailyRevenueReport(DailyRevenueReportRequestDTO request) {
        List<DailyRevenueRowDTO> rows = getDailyRevenueData(request);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20, 30, 40, 30);

            PdfFont font;
            PdfFont fontBold;
            try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/times.ttf");
                 InputStream fontBoldStream = getClass().getClassLoader().getResourceAsStream("fonts/timesbd.ttf")) {
                if (fontStream == null || fontBoldStream == null) {
                    throw new FileNotFoundException("Font files not found");
                }
                font = PdfFontFactory.createFont(fontStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                fontBold = PdfFontFactory.createFont(fontBoldStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
            }

            // Header
            document.add(new Paragraph("MOTOGO")
                    .setFont(fontBold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(0));
            document.add(new Paragraph(STORE_NAME).setFont(fontBold).setFontSize(12).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_ADDRESS).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_PHONE).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(10));

            // Title
            document.add(new Paragraph("BÁO CÁO DOANH THU THEO NGÀY")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Date range - sử dụng timezone GMT+7 để khớp với DTO
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String startDateStr = sdf.format(request.getStartDate());
            String endDateStr = sdf.format(request.getEndDate());
            document.add(new Paragraph("Từ ngày: " + startDateStr + " - Đến ngày: " + endDateStr)
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Branch
            String branchLabel = resolveBranchName(request.getBranchId());
            document.add(new Paragraph("Chi nhánh: " + branchLabel)
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Table
            float[] columnWidths = {100f, 100f, 120f, 120f, 100f, 120f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "Ngày", fontBold); // Căn giữa
            addHeaderCell(table, "Số HĐ hoàn thành", fontBold); // Căn giữa
            addRightAlignedHeaderCell(table, "Tiền thuê xe", fontBold);
            addRightAlignedHeaderCell(table, "Tiền phụ thu", fontBold);
            addRightAlignedHeaderCell(table, "Giảm giá", fontBold);
            addRightAlignedHeaderCell(table, "Tổng doanh thu", fontBold);

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            BigDecimal totalRental = BigDecimal.ZERO;
            BigDecimal totalSurcharge = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalContracts = 0;

            if (rows.isEmpty()) {
                Cell emptyCell = new Cell(1, 6)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                for (DailyRevenueRowDTO row : rows) {
                    table.addCell(createBodyCell(row.getDate().format(dateFormatter), font)); // Căn giữa
                    table.addCell(createBodyCell(String.valueOf(row.getContractCount()), font)); // Căn giữa
                    table.addCell(createRightAlignedCell(formatCurrency(row.getRentalAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getSurchargeAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getDiscountAmount(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getRevenue(), currencyFormat), font));

                    totalContracts += row.getContractCount();
                    totalRental = totalRental.add(orZero(row.getRentalAmount()));
                    totalSurcharge = totalSurcharge.add(orZero(row.getSurchargeAmount()));
                    totalDiscount = totalDiscount.add(orZero(row.getDiscountAmount()));
                    totalRevenue = totalRevenue.add(orZero(row.getRevenue()));
                }

                // Total row
                table.addCell(createFooterCell("Tổng", fontBold)); // Căn giữa
                table.addCell(createFooterCell(String.valueOf(totalContracts), fontBold)); // Căn giữa
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalRental, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalSurcharge, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalDiscount, currencyFormat), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalRevenue, currencyFormat), fontBold));
            }

            document.add(table);

            // Signature section
            document.add(new Paragraph("\n\n"));
            Table signTable = new Table(new float[]{1, 1});
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(createSignCell("Bên cho thuê\n(ký ghi rõ họ tên)", fontBold));
            signTable.addCell(createSignCell("Người lập báo cáo\n(ký ghi rõ họ tên)", fontBold));
            document.add(signTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting daily revenue report", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<DailyRevenueRowDTO> mapToDailyRows(List<Object[]> raw, Date startDate, Date endDate) {
        // Convert raw data to map for quick lookup
        Map<LocalDate, DailyRevenueRowDTO> dataMap = new HashMap<>();
        for (Object[] obj : raw) {
            java.sql.Date sqlDate = (java.sql.Date) obj[0];
            LocalDate date = sqlDate.toLocalDate();
            int contractCount = ((Number) obj[1]).intValue();
            BigDecimal rental = (BigDecimal) obj[2];
            BigDecimal surcharge = (BigDecimal) obj[3];
            BigDecimal discount = (BigDecimal) obj[4];
            BigDecimal revenue = rental.add(surcharge).subtract(discount);

            dataMap.put(date, DailyRevenueRowDTO.builder()
                    .date(date)
                    .contractCount(contractCount)
                    .rentalAmount(rental)
                    .surchargeAmount(surcharge)
                    .discountAmount(discount)
                    .revenue(revenue)
                    .build());
        }

        // Generate all dates in range, fill with zero if no data
        List<DailyRevenueRowDTO> result = new ArrayList<>();
        LocalDate start = startDate.toInstant().atZone(ZoneId.of("GMT+7")).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.of("GMT+7")).toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DailyRevenueRowDTO row = dataMap.get(date);
            if (row != null) {
                result.add(row);
            } else {
                result.add(DailyRevenueRowDTO.builder()
                        .date(date)
                        .contractCount(0)
                        .rentalAmount(BigDecimal.ZERO)
                        .surchargeAmount(BigDecimal.ZERO)
                        .discountAmount(BigDecimal.ZERO)
                        .revenue(BigDecimal.ZERO)
                        .build());
            }
        }

        return result;
    }

    @Override
    public List<ModelRentalRowDTO> getModelRentalData(ModelRentalReportRequestDTO request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        // Lấy tất cả mẫu xe theo chi nhánh
        List<String> allModels = contractRepository.findAllDistinctModels(request.getBranchId());

        // Lấy dữ liệu thống kê
        List<Object[]> raw = contractRepository.sumRentalByModel(
                request.getBranchId(),
                request.getStartDate(),
                request.getEndDate()
        );

        // Merge: tất cả mẫu xe + dữ liệu thống kê
        return mapToModelRentalRowsWithAllModels(allModels, raw);
    }

    @Override
    public byte[] exportModelRentalReport(ModelRentalReportRequestDTO request) {
        List<ModelRentalRowDTO> rows = getModelRentalData(request);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 30, 40, 30);

            PdfFont font;
            PdfFont fontBold;
            try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/times.ttf");
                 InputStream fontBoldStream = getClass().getClassLoader().getResourceAsStream("fonts/timesbd.ttf")) {
                if (fontStream == null || fontBoldStream == null) {
                    throw new FileNotFoundException("Font files not found");
                }
                font = PdfFontFactory.createFont(fontStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                fontBold = PdfFontFactory.createFont(fontBoldStream.readAllBytes(), com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
            }

            // Header
            document.add(new Paragraph("MOTOGO")
                    .setFont(fontBold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(0));
            document.add(new Paragraph(STORE_NAME).setFont(fontBold).setFontSize(12).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_ADDRESS).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(0));
            document.add(new Paragraph(STORE_PHONE).setFont(font).setFontSize(11).setMarginTop(0).setMarginBottom(10));

            // Title
            document.add(new Paragraph("THỐNG KÊ LƯỢT THUÊ THEO MẪU XE")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Date range - sử dụng timezone GMT+7 để khớp với DTO
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String startDateStr = sdf.format(request.getStartDate());
            String endDateStr = sdf.format(request.getEndDate());
            document.add(new Paragraph("Từ ngày: " + startDateStr + " - Đến ngày: " + endDateStr)
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Branch
            String branchLabel = resolveBranchName(request.getBranchId());
            document.add(new Paragraph("Chi nhánh: " + branchLabel)
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Table
            float[] columnWidths = {60f, 200f, 100f, 150f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "STT", fontBold); // Căn giữa cho STT
            addLeftAlignedHeaderCell(table, "Mẫu xe", fontBold);
            addRightAlignedHeaderCell(table, "Số lượt thuê", fontBold);
            addRightAlignedHeaderCell(table, "Tiền thuê xe", fontBold);

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            int totalRentalCount = 0;
            BigDecimal totalRentalAmount = BigDecimal.ZERO;

            if (rows.isEmpty()) {
                Cell emptyCell = new Cell(1, 4)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                for (ModelRentalRowDTO row : rows) {
                    table.addCell(createBodyCell(String.valueOf(row.getStt()), font)); // Căn giữa cho STT
                    table.addCell(createBodyCell(row.getModelName(), font).setTextAlignment(TextAlignment.LEFT));
                    table.addCell(createRightAlignedCell(String.valueOf(row.getRentalCount()), font));
                    table.addCell(createRightAlignedCell(formatCurrency(row.getRentalAmount(), currencyFormat), font));

                    totalRentalCount += row.getRentalCount();
                    totalRentalAmount = totalRentalAmount.add(orZero(row.getRentalAmount()));
                }

                // Total row
                table.addCell(createFooterCell("", fontBold));
                table.addCell(createFooterCell("Tổng", fontBold).setTextAlignment(TextAlignment.LEFT));
                table.addCell(createRightAlignedFooterCell(String.valueOf(totalRentalCount), fontBold));
                table.addCell(createRightAlignedFooterCell(formatCurrency(totalRentalAmount, currencyFormat), fontBold));
            }

            document.add(table);

            // Signature section
            document.add(new Paragraph("\n\n"));
            Table signTable = new Table(new float[]{1, 1});
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(createSignCell("Bên cho thuê\n(ký ghi rõ họ tên)", fontBold));
            signTable.addCell(createSignCell("Người lập báo cáo\n(ký ghi rõ họ tên)", fontBold));
            document.add(signTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting model rental report", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<ModelRentalRowDTO> mapToModelRentalRowsWithAllModels(List<String> allModels, List<Object[]> raw) {
        // Tạo map từ dữ liệu thống kê
        Map<String, Object[]> statsMap = new HashMap<>();
        for (Object[] obj : raw) {
            String modelName = (String) obj[0];
            statsMap.put(modelName, obj);
        }

        // Tạo kết quả với tất cả mẫu xe
        List<ModelRentalRowDTO> result = new ArrayList<>();
        int stt = 1;
        
        // Thêm mẫu xe có dữ liệu trước (sắp xếp theo rentalCount DESC)
        for (Object[] obj : raw) {
            String modelName = (String) obj[0];
            int rentalCount = ((Number) obj[1]).intValue();
            BigDecimal rentalAmount = obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO;

            result.add(ModelRentalRowDTO.builder()
                    .stt(stt++)
                    .modelName(modelName)
                    .rentalCount(rentalCount)
                    .rentalAmount(rentalAmount)
                    .build());
        }

        // Thêm mẫu xe không có dữ liệu (rentalCount = 0)
        for (String model : allModels) {
            if (!statsMap.containsKey(model)) {
                result.add(ModelRentalRowDTO.builder()
                        .stt(stt++)
                        .modelName(model)
                        .rentalCount(0)
                        .rentalAmount(BigDecimal.ZERO)
                        .build());
            }
        }

        return result;
    }
}

