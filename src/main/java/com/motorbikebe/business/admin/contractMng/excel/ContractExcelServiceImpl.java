package com.motorbikebe.business.admin.contractMng.excel;

import com.motorbikebe.business.admin.contractMng.service.ContractMngService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.contractMng.ContractDTO;
import com.motorbikebe.dto.business.admin.contractMng.ContractSearchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Implementation for Contract Excel operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractExcelServiceImpl implements ContractExcelService {
    
    private final ContractMngService contractMngService;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public ByteArrayOutputStream exportContracts(ContractSearchDTO searchDTO) {
        try {
            // Set size to MAX to get all results
            searchDTO.setSize(Integer.MAX_VALUE);
            
            // Get contracts based on search criteria
            PageableObject<ContractDTO> result = contractMngService.searchContracts(searchDTO);
            List<ContractDTO> contracts = result.getData();
            
            if (contracts == null || contracts.isEmpty()) {
                throw new RestApiException(ApiStatus.BAD_REQUEST_VALID);
            }
            
            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Danh sách hợp đồng");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "STT", "Mã hợp đồng", "Tên khách hàng", "Số điện thoại", "Email",
                "Nguồn", "Ngày thuê", "Ngày trả", "Chi nhánh giao xe", "Chi nhánh trả xe",
                "Địa chỉ giao xe", "Địa chỉ trả xe", "Tổng tiền thuê", "Tổng phụ thu",
                "Giảm giá", "Tổng tiền cuối", "Đã thanh toán", "Còn lại", "Trạng thái",
                "Ghi chú", "Thời gian giao xe", "Thời gian nhận xe", "Ngày hoàn thành"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Fill data rows
            int rowNum = 1;
            for (ContractDTO contract : contracts) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                
                // STT
                createCell(row, colNum++, rowNum - 1, dataStyle);
                
                // Mã hợp đồng
                createCell(row, colNum++, contract.getContractCode(), dataStyle);
                
                // Tên khách hàng
                createCell(row, colNum++, contract.getCustomerName(), dataStyle);
                
                // Số điện thoại
                createCell(row, colNum++, contract.getPhoneNumber(), dataStyle);
                
                // Email
                createCell(row, colNum++, contract.getEmail(), dataStyle);
                
                // Nguồn
                createCell(row, colNum++, contract.getSource(), dataStyle);
                
                // Ngày thuê
                if (contract.getStartDate() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(DATE_FORMAT.format(contract.getStartDate()));
                    cell.setCellStyle(dateStyle);
                } else {
                    createCell(row, colNum++, "", dateStyle);
                }
                
                // Ngày trả
                if (contract.getEndDate() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(DATE_FORMAT.format(contract.getEndDate()));
                    cell.setCellStyle(dateStyle);
                } else {
                    createCell(row, colNum++, "", dateStyle);
                }
                
                // Chi nhánh giao xe
                createCell(row, colNum++, contract.getPickupBranchName(), dataStyle);
                
                // Chi nhánh trả xe
                createCell(row, colNum++, contract.getReturnBranchName(), dataStyle);
                
                // Địa chỉ giao xe
                createCell(row, colNum++, contract.getPickupAddress(), dataStyle);
                
                // Địa chỉ trả xe
                createCell(row, colNum++, contract.getReturnAddress(), dataStyle);
                
                // Tổng tiền thuê
                if (contract.getTotalRentalAmount() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getTotalRentalAmount().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Tổng phụ thu
                if (contract.getTotalSurcharge() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getTotalSurcharge().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Giảm giá
                if (contract.getDiscountAmount() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getDiscountAmount().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Tổng tiền cuối
                if (contract.getFinalAmount() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getFinalAmount().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Đã thanh toán
                if (contract.getPaidAmount() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getPaidAmount().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Còn lại
                if (contract.getRemainingAmount() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(contract.getRemainingAmount().doubleValue());
                    cell.setCellStyle(currencyStyle);
                } else {
                    createCell(row, colNum++, 0, currencyStyle);
                }
                
                // Trạng thái
                createCell(row, colNum++, contract.getStatusNm(), dataStyle);
                
                // Ghi chú
                createCell(row, colNum++, contract.getNotes(), dataStyle);
                
                // Thời gian giao xe
                if (contract.getDeliveryTime() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(DATE_FORMAT.format(contract.getDeliveryTime()));
                    cell.setCellStyle(dateStyle);
                } else {
                    createCell(row, colNum++, "", dateStyle);
                }
                
                // Thời gian nhận xe
                if (contract.getReturnTime() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(DATE_FORMAT.format(contract.getReturnTime()));
                    cell.setCellStyle(dateStyle);
                } else {
                    createCell(row, colNum++, "", dateStyle);
                }
                
                // Ngày hoàn thành
                if (contract.getCompletedDate() != null) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(DATE_FORMAT.format(contract.getCompletedDate()));
                    cell.setCellStyle(dateStyle);
                } else {
                    createCell(row, colNum++, "", dateStyle);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Add extra width for better readability
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }
            
            // Write to output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            
            log.info("Exported {} contracts to Excel", contracts.size());
            return out;
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error exporting contracts to Excel", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            }
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}

