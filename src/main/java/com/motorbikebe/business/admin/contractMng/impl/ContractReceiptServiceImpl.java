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
import com.motorbikebe.business.admin.contractMng.service.ContractMngService;
import com.motorbikebe.business.admin.contractMng.service.ContractReceiptService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.constant.enumconstant.ContractStatus;
import com.motorbikebe.dto.business.admin.contractMng.ContractReceiptRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.ContractDTO;
import com.motorbikebe.dto.business.admin.contractMng.ContractCarDTO;
import com.motorbikebe.dto.business.admin.contractMng.PaymentTransactionDTO;
import com.motorbikebe.repository.business.admin.PaymentTransactionRepository;
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
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractReceiptServiceImpl implements ContractReceiptService {

    private static final String STORE_NAME = "CỬA HÀNG CHO THUÊ XE MÁY MOTOGO";
    private static final String STORE_ADDRESS = "Tổ 1, Thôn Cầu Mè, Phương Thiện, Hà Giang, Việt Nam";
    private static final String STORE_PHONE = "0859963204";

    private final ContractMngService contractMngService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public byte[] exportReceipt(ContractReceiptRequestDTO request) {
        if (request.getContractId() == null) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        ContractDTO contract = contractMngService.getContractDetail(request.getContractId());
        if (contract == null || contract.getStatus() == ContractStatus.CANCELLED) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        List<ContractCarDTO> cars = contract.getCars();
        List<PaymentTransactionDTO> payments = paymentTransactionRepository.findByContractIdWithEmployee(contract.getId());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 30, 30, 30);

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

            document.add(new Paragraph(STORE_NAME).setFont(fontBold).setFontSize(16).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph(STORE_ADDRESS).setFont(font).setFontSize(11).setMarginBottom(0));
            document.add(new Paragraph("SĐT: " + STORE_PHONE).setFont(font).setFontSize(11).setMarginBottom(10));

            document.add(new Paragraph("BIÊN NHẬN TRẢ XE")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            dateTimeFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));

            // Trừ 7 giờ cho thời gian thuê và trả xe
            java.util.Date adjustedStartDate = contract.getStartDate() != null ? 
                new java.util.Date(contract.getStartDate().getTime() - 7 * 60 * 60 * 1000) : null;
            java.util.Date adjustedEndDate = contract.getEndDate() != null ? 
                new java.util.Date(contract.getEndDate().getTime() - 7 * 60 * 60 * 1000) : null;

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            infoTable.addCell(makeBorderlessCell("Họ tên người thuê: " + defaultString(contract.getCustomerName()), font));
            infoTable.addCell(makeBorderlessCell("Mã hợp đồng: " + defaultString(contract.getContractCode()), font));
            infoTable.addCell(makeBorderlessCell("SĐT: " + defaultString(contract.getPhoneNumber()), font));
            infoTable.addCell(makeBorderlessCell("Thuê lúc: " + formatDate(adjustedStartDate, dateTimeFormat), font));
            infoTable.addCell(makeBorderlessCell("CMND/CCCD: " + defaultString(contract.getCitizenId()), font));
            infoTable.addCell(makeBorderlessCell("Trả lúc: " + formatDate(adjustedEndDate, dateTimeFormat), font));
            document.add(infoTable);

            document.add(new Paragraph("\nXE THUÊ").setFont(fontBold).setFontSize(12));

            float[] carTableWidths = {40f, 100f, 180f, 120f, 100f};
            Table carTable = new Table(carTableWidths);
            carTable.setWidth(UnitValue.createPercentValue(100));
            addHeaderCell(carTable, "STT", fontBold);
            addHeaderCell(carTable, "Loại xe", fontBold);
            addHeaderCell(carTable, "Xe", fontBold);
            addHeaderCell(carTable, "Biển số", fontBold);
            addHeaderCell(carTable, "Giá thuê", fontBold);

        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            int index = 1;
            for (ContractCarDTO car : cars) {
                carTable.addCell(createBodyCell(String.valueOf(index++), font));
                carTable.addCell(createBodyCell(defaultString(car.getCarType()), font));
                carTable.addCell(createBodyCell(defaultString(car.getCarModel()), font));
                carTable.addCell(createBodyCell(defaultString(car.getLicensePlate()), font));
            BigDecimal carAmount = calculateCarRentalAmount(contract, car);
            carTable.addCell(createBodyCell(formatCurrency(carAmount, currencyFormat), font));
            }
            document.add(carTable);

            document.add(new Paragraph("\n"));

            Table summaryTable = new Table(new float[]{180f, 100f});
            summaryTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

            addSummaryRow(summaryTable, "Tiền thuê xe", contract.getTotalRentalAmount(), font, fontBold, currencyFormat);
            addSummaryRow(summaryTable, "Phụ thu", contract.getTotalSurcharge(), font, fontBold, currencyFormat);
            addSummaryRow(summaryTable, "Giảm giá", contract.getDiscountAmount(), font, fontBold, currencyFormat);
            addSummaryRow(summaryTable, "Tổng phải thu", contract.getFinalAmount(), font, fontBold, currencyFormat);
            addSummaryRow(summaryTable, "Đã thanh toán", contract.getPaidAmount(), font, fontBold, currencyFormat);
            addSummaryRow(summaryTable, "Phải thu khách", contract.getRemainingAmount(), font, fontBold, currencyFormat);

            document.add(summaryTable);

            document.add(new Paragraph("\n"));
            Table signTable = new Table(new float[]{1, 1});
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(createSignCell("Bên thuê\n(ký ghi rõ họ tên)", fontBold));
            signTable.addCell(createSignCell("Bên cho thuê\n(ký ghi rõ họ tên)", fontBold));
            document.add(signTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting receipt", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Cell makeBorderlessCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setBorder(null);
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(11)).setTextAlignment(TextAlignment.CENTER));
    }

    private Cell createBodyCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(10));
    }

    private void addSummaryRow(Table table, String label, BigDecimal value, PdfFont font, PdfFont fontBold, NumberFormat format) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(fontBold).setFontSize(11)));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(value, format)).setFont(fontBold).setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT));
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
            return "0";
        }
        return format.format(value);
    }

    private String formatDate(java.util.Date date, SimpleDateFormat format) {
        if (date == null) {
            return "";
        }
        // Đảm bảo timezone đã được set trước khi format
        if (format.getTimeZone() == null || format.getTimeZone().getID().equals("GMT")) {
            format.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        }
        return format.format(date);
    }

    private BigDecimal calculateCarRentalAmount(ContractDTO contract, ContractCarDTO car) {
        java.util.Date startDate = contract.getStartDate();
        java.util.Date endDate = contract.getEndDate();

        if (startDate == null || endDate == null) {
            return safeAmount(car.getTotalAmount());
        }

        long diffMillis = endDate.getTime() - startDate.getTime();
        if (diffMillis <= 0) {
            return safeAmount(car.getTotalAmount());
        }

        long totalMinutes = diffMillis / (60 * 1000);
        long minutesPerDay = 24L * 60L;

        long fullDays = totalMinutes / minutesPerDay;
        long remainingMinutes = totalMinutes % minutesPerDay;
        long extraHours = 0;

        if (fullDays == 0) {
            fullDays = 1;
        } else {
            extraHours = remainingMinutes / 60;
            long remainingMinutesAfterHours = remainingMinutes % 60;
            if (remainingMinutesAfterHours > 30) {
                extraHours++;
            }
            if (extraHours > 8) {
                fullDays++;
                extraHours = 0;
            }
        }

        BigDecimal dayPrice = safeAmount(car.getDailyPrice());
        BigDecimal hourPrice = safeAmount(car.getHourlyPrice());

        BigDecimal total = dayPrice.multiply(BigDecimal.valueOf(fullDays));
        if (extraHours > 0) {
            total = total.add(hourPrice.multiply(BigDecimal.valueOf(extraHours)));
        }
        return total;
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

