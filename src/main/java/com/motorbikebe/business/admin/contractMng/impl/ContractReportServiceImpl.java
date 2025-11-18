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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

            float[] columnWidths = {80f, 130f, 130f, 130f, 130f};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "Tháng", fontBold);
            addHeaderCell(table, "Tiền thuê xe", fontBold);
            addHeaderCell(table, "Tiền phụ thu", fontBold);
            addHeaderCell(table, "Giảm giá", fontBold);
            addHeaderCell(table, "Doanh thu", fontBold);

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            BigDecimal totalRental = BigDecimal.ZERO;
            BigDecimal totalSurcharge = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            if (rows.isEmpty()) {
                Cell emptyCell = new Cell(1, 5)
                        .add(new Paragraph("Không có dữ liệu").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                for (MonthlyRevenueRowDTO row : rows) {
                    table.addCell(createBodyCell(String.valueOf(row.getMonth()), font));
                    table.addCell(createBodyCell(formatCurrency(row.getRentalAmount(), currencyFormat), font));
                    table.addCell(createBodyCell(formatCurrency(row.getSurchargeAmount(), currencyFormat), font));
                    table.addCell(createBodyCell(formatCurrency(row.getDiscountAmount(), currencyFormat), font));
                    table.addCell(createBodyCell(formatCurrency(row.getRevenue(), currencyFormat), font));

                    totalRental = totalRental.add(orZero(row.getRentalAmount()));
                    totalSurcharge = totalSurcharge.add(orZero(row.getSurchargeAmount()));
                    totalDiscount = totalDiscount.add(orZero(row.getDiscountAmount()));
                    totalRevenue = totalRevenue.add(orZero(row.getRevenue()));
                }

                table.addCell(createFooterCell("Tổng", fontBold));
                table.addCell(createFooterCell(formatCurrency(totalRental, currencyFormat), fontBold));
                table.addCell(createFooterCell(formatCurrency(totalSurcharge, currencyFormat), fontBold));
                table.addCell(createFooterCell(formatCurrency(totalDiscount, currencyFormat), fontBold));
                table.addCell(createFooterCell(formatCurrency(totalRevenue, currencyFormat), fontBold));
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
        List<MonthlyRevenueRowDTO> rows = new ArrayList<>();
        for (Object[] obj : raw) {
            int month = ((Number) obj[0]).intValue();
            BigDecimal rental = (BigDecimal) obj[1];
            BigDecimal surcharge = (BigDecimal) obj[2];
            BigDecimal discount = (BigDecimal) obj[3];
            BigDecimal revenue = rental.add(surcharge).subtract(discount);
            rows.add(MonthlyRevenueRowDTO.builder()
                    .month(month)
                    .rentalAmount(rental)
                    .surchargeAmount(surcharge)
                    .discountAmount(discount)
                    .revenue(revenue)
                    .build());
        }
        return rows;
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setTextAlignment(TextAlignment.CENTER));
    }

    private Cell createBodyCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createFooterCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setTextAlignment(TextAlignment.CENTER);
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
}

