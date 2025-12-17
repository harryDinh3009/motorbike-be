package com.motorbikebe.business.admin.carMng.impl;

import com.itextpdf.kernel.colors.DeviceRgb;
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
import com.motorbikebe.business.admin.carMng.service.CarReportService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.carMng.AvailableCarReportRequestDTO;
import com.motorbikebe.dto.business.admin.carMng.CarDTO;
import com.motorbikebe.dto.business.admin.carMng.CarSearchAvailableDTO;
import com.motorbikebe.dto.business.admin.carMng.CarSearchDTO;
import com.motorbikebe.dto.business.admin.carMng.RentableCarReportRequestDTO;
import com.motorbikebe.constant.enumconstant.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.motorbikebe.entity.domain.BranchEntity;
import com.motorbikebe.repository.business.admin.BranchRepository;
import com.motorbikebe.repository.business.admin.CarRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarReportServiceImpl implements CarReportService {

    private static final String STORE_NAME = "CỬA HÀNG CHO THUÊ XE MÁY MOTOGO";
    private static final String STORE_ADDRESS = "Địa chỉ: Tổ 1, Thôn Cầu Mè, Phương Thiện, Hà Giang, Việt Nam";
    private static final String STORE_PHONE = "SDT: 0859963204";

    private static final DateTimeFormatter REPORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CarRepository carRepository;
    private final BranchRepository branchRepository;

    @Override
    public byte[] exportAvailableCarsReport(AvailableCarReportRequestDTO request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        CarSearchAvailableDTO searchDTO = new CarSearchAvailableDTO();
        searchDTO.setStartDate(request.getStartDate());
        searchDTO.setEndDate(request.getEndDate());
        searchDTO.setBranchId(request.getBranchId());
        searchDTO.setModelName(request.getModelName());
        searchDTO.setCarType(request.getCarType());

        List<CarDTO> availableCars = carRepository.findAvailableCarsForReportAvailable(searchDTO);

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
                    throw new FileNotFoundException("Font files not found in resources/fonts/");
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

            document.add(new Paragraph("BÁO CÁO XE KHẢ DỤNG CHO THUÊ")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            String dateRange = String.format("Từ ngày %s tới ngày %s",
                    request.getStartDate().format(REPORT_DATE_TIME_FORMATTER),
                    request.getEndDate().format(REPORT_DATE_TIME_FORMATTER));
            document.add(new Paragraph(dateRange)
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

            float[] columnWidths = {40f, 120f, 110f, 150f, 100f, 110f, 90f, 90f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "STT", font, fontBold); // Căn giữa cho STT
            addLeftAlignedHeaderCell(table, "Mẫu xe", font, fontBold);
            addLeftAlignedHeaderCell(table, "Biển số", font, fontBold);
            addLeftAlignedHeaderCell(table, "Chi nhánh", font, fontBold);
            addLeftAlignedHeaderCell(table, "Loại xe", font, fontBold);
            addLeftAlignedHeaderCell(table, "Tình trạng", font, fontBold);
            addRightAlignedHeaderCell(table, "Giá ngày", font, fontBold);
            addRightAlignedHeaderCell(table, "Giá giờ", font, fontBold);

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            if (availableCars.isEmpty()) {
                Cell emptyCell = new Cell(1, 8)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                int index = 1;
                for (CarDTO car : availableCars) {
                    table.addCell(createBodyCell(String.valueOf(index++), font).setTextAlignment(TextAlignment.CENTER)); // Căn giữa cho STT
                    table.addCell(createBodyCell(defaultString(car.getModel()), font));
                    table.addCell(createBodyCell(defaultString(car.getLicensePlate()), font));
                    table.addCell(createBodyCell(defaultString(car.getBranchName()), font));
                    table.addCell(createBodyCell(defaultString(car.getCarType()), font));
                    table.addCell(createBodyCell(defaultString(car.getCondition()), font));
                    table.addCell(createRightAlignedCell(formatCurrency(car.getDailyPrice(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(car.getHourlyPrice(), currencyFormat), font));
                }
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
            log.error("Error exporting available car report", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String resolveBranchName(String branchId) {
        if (StringUtils.isBlank(branchId)) {
            return "Tất cả chi nhánh";
        }
        Optional<BranchEntity> branchOpt = branchRepository.findById(branchId);
        return branchOpt.map(BranchEntity::getName).orElse("Không xác định");
    }

    private void addHeaderCell(Table table, String text, PdfFont font, PdfFont fontBold) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(fontBold).setFontSize(11))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    private void addLeftAlignedHeaderCell(Table table, String text, PdfFont font, PdfFont fontBold) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(fontBold).setFontSize(11))
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(cell);
    }

    private void addRightAlignedHeaderCell(Table table, String text, PdfFont font, PdfFont fontBold) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(fontBold).setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);
    }

    private Cell createBodyCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell createRightAlignedCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell createSignCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11).setTextAlignment(TextAlignment.CENTER))
                .setBorder(null);
    }

    private String defaultString(String value) {
        return value != null ? value : "";
    }

    private String formatCurrency(BigDecimal value, NumberFormat format) {
        if (value == null) {
            return "";
        }
        return format.format(value);
    }

    @Override
    public byte[] exportRentableCarsReport(RentableCarReportRequestDTO request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        // Use CarSearchDTO with Date (same as contract modal)
        CarSearchDTO searchDTO = new CarSearchDTO();
        searchDTO.setStartDate(request.getStartDate());
        searchDTO.setEndDate(request.getEndDate());
        searchDTO.setBranchId(request.getBranchId());
        searchDTO.setModelName(request.getModelName());
        searchDTO.setCarType(request.getCarType());

        // Get all cars using searchAvailableCars (same logic as contract modal)
        Pageable pageable = PageRequest.of(0, 10000);
        Page<CarDTO> carPage = carRepository.searchAvailableCars(pageable, searchDTO);
        
        // Hiển thị cả AVAILABLE và NOT_AVAILABLE (giống modal)
        List<CarDTO> rentableCars = carPage.getContent();

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
                    throw new FileNotFoundException("Font files not found in resources/fonts/");
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
            document.add(new Paragraph("THỐNG KÊ XE KHẢ DỤNG")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Date range - FE đã gửi đúng timezone GMT+7
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
            String dateRange = String.format("Từ ngày %s tới ngày %s",
                    dateFormat.format(request.getStartDate()),
                    dateFormat.format(request.getEndDate()));
            document.add(new Paragraph(dateRange)
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
            float[] columnWidths = {40f, 120f, 110f, 150f, 100f, 110f, 90f, 90f, 100f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "STT", font, fontBold); // Căn giữa cho STT
            addLeftAlignedHeaderCell(table, "Mẫu xe", font, fontBold);
            addLeftAlignedHeaderCell(table, "Biển số", font, fontBold);
            addLeftAlignedHeaderCell(table, "Chi nhánh", font, fontBold);
            addLeftAlignedHeaderCell(table, "Loại xe", font, fontBold);
            addLeftAlignedHeaderCell(table, "Tình trạng", font, fontBold);
            addRightAlignedHeaderCell(table, "Giá ngày", font, fontBold);
            addRightAlignedHeaderCell(table, "Giá giờ", font, fontBold);
            addHeaderCell(table, "Kết quả", font, fontBold); // Căn giữa cho Kết quả

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            if (rentableCars.isEmpty()) {
                Cell emptyCell = new Cell(1, 9)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                int index = 1;
                for (CarDTO car : rentableCars) {
                    table.addCell(createBodyCell(String.valueOf(index++), font).setTextAlignment(TextAlignment.CENTER)); // Căn giữa cho STT
                    table.addCell(createBodyCell(defaultString(car.getModel()), font));
                    table.addCell(createBodyCell(defaultString(car.getLicensePlate()), font));
                    table.addCell(createBodyCell(defaultString(car.getBranchName()), font));
                    table.addCell(createBodyCell(defaultString(car.getCarType()), font));
                    table.addCell(createBodyCell(defaultString(car.getCondition()), font));
                    table.addCell(createRightAlignedCell(formatCurrency(car.getDailyPrice(), currencyFormat), font));
                    table.addCell(createRightAlignedCell(formatCurrency(car.getHourlyPrice(), currencyFormat), font));
                    
                    // Cột Kết quả
                    String result = (car.getStatus() == CarStatus.NOT_AVAILABLE) ? "Đã đặt thuê" : "Khả dụng";
                    Cell resultCell = createBodyCell(result, font).setTextAlignment(TextAlignment.CENTER);
                    if (car.getStatus() == CarStatus.NOT_AVAILABLE) {
                        resultCell.setFontColor(DeviceRgb.RED);
                    } else {
                        resultCell.setFontColor(new DeviceRgb(82, 196, 26)); // Green color #52c41a
                    }
                    table.addCell(resultCell);
                }
            }

            document.add(table);

            // Signature
            document.add(new Paragraph("\n\n"));
            Table signTable = new Table(new float[]{1, 1});
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(createSignCell("Bên cho thuê\n(ký ghi rõ họ tên)", fontBold));
            signTable.addCell(createSignCell("Người lập báo cáo\n(ký ghi rõ họ tên)", fontBold));
            document.add(signTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting rentable car report", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

