package com.dance.core.utils.excel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel公共导出类
 * 
 * @author zzm
 */
public class ExcelManager {
	/**
	 * 根据Model读取office2007Excel
	 * 
	 * @param filePath
	 * @param model
	 * @return 数据集合
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Vector<Object> readExcel(String filePath, Class<?> model)
			throws Exception {
		Vector<Object> vt = new Vector<Object>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(filePath);
			XSSFSheet sheet = wb.getSheetAt(0);

			for (Iterator rit = sheet.rowIterator(); rit.hasNext();) {
				XSSFRow row = (XSSFRow) rit.next();
				if (row.getRowNum() > 0) {
					Object obj = model.newInstance();
					Map<Integer, String> map = ExcelAnnotationTool
							.getColumonMapper(model, "IN");
					for (Iterator cit = row.cellIterator(); cit.hasNext();) {
						XSSFCell cell = (XSSFCell) cit.next();
						String value = parseValue(cell);
						String fieldName = map.get(cell.getColumnIndex());
						if (fieldName != null) {
							Field fd = model.getDeclaredField(fieldName);
							fd.setAccessible(true);
							fd.set(obj, getValue(value, fd));
						}
					}
					vt.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vt;
	}

	@SuppressWarnings({"rawtypes" })
	public Vector<Object> readExcel(InputStream in, Class<?> model)
			throws Exception {
		Vector<Object> vt = new Vector<Object>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(in);
			XSSFSheet sheet = wb.getSheetAt(0);

			for (Iterator rit = sheet.rowIterator(); rit.hasNext();) {
				XSSFRow row = (XSSFRow) rit.next();
				if (row.getRowNum() > 0) {
					Object obj = model.newInstance();
					Map<Integer, String> map = ExcelAnnotationTool
							.getColumonMapper(model, "IN");
					for (Iterator cit = row.cellIterator(); cit.hasNext();) {
						XSSFCell cell = (XSSFCell) cit.next();
						String value = parseValue(cell);
						String fieldName = map.get(cell.getColumnIndex());
						if (fieldName != null) {
							Field fd = model.getDeclaredField(fieldName);
							fd.setAccessible(true);
							fd.set(obj, getValue(value, fd));
						}
					}
					vt.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		return vt;
	}

	public Object getValue(String preValue, Field fd) {
		if (fd.getType().equals(java.lang.String.class)) {
			return preValue;
		} else if (fd.getType().equals(java.lang.Integer.class)) {
			int idx = preValue.indexOf(".");
			if (idx != -1)
				return Integer.parseInt(preValue.substring(0, idx));
			else
				return Integer.parseInt(preValue);
		} else if (fd.getType().equals(java.lang.Long.class)) {
			if (preValue == null || "".equals(preValue)) {
				return 0l;
			}
			int idx = preValue.indexOf(".");
			if (idx != -1)
				return Long.valueOf(preValue
						.substring(0, preValue.indexOf(".")));
			else
				return Long.valueOf(preValue);
		} else if (fd.getType().equals(java.lang.Boolean.class)) {
			return Boolean.valueOf(preValue);
		} else if (fd.getType().equals(java.lang.Float.class)) {
			return Float.valueOf(preValue);
		}
		return null;
	}

	/**
	 * 解析Cell值
	 * 
	 * @param cell
	 * @return
	 */
	public String parseValue(XSSFCell cell) {
		String value = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().getString();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				java.util.Date date = cell.getDateCellValue();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				value = format.format(date);
			} else {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				value = cell.getStringCellValue();
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = " " + cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			value = cell.getCellFormula();
			break;
		}
		return value;
	}

	/**
	 * 传统Excel导出
	 * 
	 * @param header
	 *            表头为一个Map，以integer表示列数，String为Name
	 * @param data
	 *            Body数据，以Integer表示行数，ListObject为实际数据
	 * @return
	 */
	public XSSFWorkbook convertDataToExcel(Map<Integer, String> header,
			Map<Integer, List<Object>> data) {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			Sheet sheet = wb.createSheet("sheet1");
			Row headerRow = sheet.createRow(0);

			setHeader(header, headerRow);

			Row bodyRow = null;
			Cell cell = null;
			for (int i = 0; i < data.size(); i++) {
				bodyRow = sheet.createRow(i + 1);
				for (int j = 0; j < header.size(); j++) {
					cell = bodyRow.createCell(j);
					cell.setCellValue(data.get(i).get(j).toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return wb;
	}

	private void setHeader(Map<Integer, String> headerMap, Row headerRow) {
		if (headerMap != null) {
			Set<Integer> key = headerMap.keySet();
			for (Integer id : key) {
				Cell cell = headerRow.createCell(id);
				cell.setCellValue(headerMap.get(id));
			}
		}
	}

	/**
	 * 将ListData转换为XSSFWorkbook
	 * 
	 * @param data
	 * @param modelClass
	 * @return
	 */
	public XSSFWorkbook convertDataToExcel(Map<Integer, String> header,
			List<Object> data, Class<?> modelClass) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet("Sheet1");
			Row headerRow = sheet.createRow(0);
			Map<Integer, String> map = ExcelAnnotationTool.getColumonMapper(
					modelClass, "OUT");
			/*
			 * if(map != null) { Set<Integer> key = map.keySet(); for (Integer
			 * id : key) { Cell cell = headerRow.createCell(id);
			 * cell.setCellValue(map.get(id)); } }
			 */
			setHeader(header, headerRow);

			Row bodyRow = null;
			Cell cell = null;
			for (int i = 0; i < data.size(); i++) {
				bodyRow = sheet.createRow(i + 1);
				for (int j = 0; j < map.size(); j++) {
					String fdName = map.get(j);
					Field fd = modelClass.getDeclaredField(fdName);
					fd.setAccessible(true);
					Object value = fd.get(data.get(i));
					cell = bodyRow.createCell(j);
					cell.setCellValue(value == null ? "" : value.toString());
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据Model导出office2007 Excel
	 * 
	 * @param filePath
	 * @param data
	 * @param modelClass
	 */
	public void exportExcel(String filePath, Map<Integer, String> headermap,
			List<Object> data, Class<?> modelClass) {
		BufferedOutputStream bos = null;
		try {
			XSSFWorkbook wb = convertDataToExcel(headermap, data, modelClass);
			if (wb != null) {
				bos = new BufferedOutputStream(new FileOutputStream(filePath));
				wb.write(bos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// ExcelManager manager = new ExcelManager();
		// try {
		// Vector<Object> vt = manager.readExcel("D:\\tmp001.xlsx",Model.class);
		// for (Object obj : vt) {
		// Model pass = (Model) obj;
		// System.out.println(pass.getPassport() + ">>>>>" +
		// pass.getUsername());
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// List<Object> list = new ArrayList<Object>();
		// for (int i = 0; i < 50; i++) {
		// Passport ps = new Passport();
		// ps.setPassport(i+"xiaoming@1" +i +".com");
		// ps.setUsername(i+"xiaoming" +i +"--");
		// list.add(ps);
		// }
		// manager.exportExcel("D:\\pass-export.xlsx", list, Passport.class);
		// Long s = 100L;
		// System.out.println(s.toString());

		/*
		 * List<Object> list = new ArrayList<Object>(); for(int i=0 ;i<10;
		 * i++ ) { SeedUpdate seedUpdate = new SeedUpdate();
		 * seedUpdate.setCompName("host"+i); seedUpdate.setMoniTime(new Date());
		 * seedUpdate.setRunState(1L); list.add(seedUpdate); } Map<Integer,String>
		 * map = new HashMap<Integer,String>(); map.put(0, "主机"); map.put(1,
		 * "时间"); map.put(2, "状态");
		 * 
		 * 
		 * ExcelManager manager = new ExcelManager();
		 * manager.exportExcel("D:\\pass-export.xlsx", map,
		 * list,SeedUpdate.class);
		 */
	}
}