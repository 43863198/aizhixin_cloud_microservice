package com.aizhixin.cloud.ew.allapidetail.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.allapidetail.dto.ApiDetailDto;
import com.aizhixin.cloud.ew.common.core.ApiReturnConstants;
import com.aizhixin.cloud.ew.common.util.PackageUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/apiDetail")
@Api(description = "API管理")
public class ApiDetailController {
	@Value("${spring.jpa.database}")
	private String database;
	@Value("${dl.dledu.back.host}")
	private String aiZhiXinHost;
	@Value("${dl.org.back.host}")
	private String OrgMaanagerHost;
	@Value("${liquibase.default-schema}")
	private String sheetName;

	@RequestMapping(value = "/apiByFilePath", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询API详情", response = Void.class, notes = "查询API详情<br><br><b>@author bly</b>")
	public ResponseEntity<Map<String, Object>> apiByFilePath(
			@ApiParam(value = "filePath 项目文件路径", required = true) @RequestParam(value = "filePath") String filePath) {
		Map<String, Object> resBody = new HashMap<>();
		List<ApiDetailDto> apiDetailDtos = null;
		try {
			apiDetailDtos = getParse(filePath);
			resBody.put("apiDetail", apiDetailDtos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.OK);
	}

	@RequestMapping(value = "/exportApiDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "导出API详情", httpMethod = "POST", response = Void.class, notes = "导出API详情<br></br><b>@author bly</b>")
	public ResponseEntity<?> exportApiDetail(
			@ApiParam(value = "filePath 项目文件路径<br/>保存至C:/API详情.xls", required = true) @RequestParam(value = "filePath") String filePath) {
		List<ApiDetailDto> apiDetailDtos = null;
		try {
			apiDetailDtos = getParse(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// get export data
		Map<String, Object> result = new HashMap<>();
		if (apiDetailDtos != null && apiDetailDtos.size() > 0) {
			FileOutputStream fos = null;
			FileInputStream fis = null;
			// ClassPathResource template = null;
			HSSFWorkbook wb = null;
			try {
				// 创建文件
				File file = new File("C:/API详情.xls");
				// 判断文件是否存在，存在则读取流
				if (!file.exists()) {
					file.createNewFile();
					wb = new HSSFWorkbook();
				} else {
					fis = new FileInputStream(file);
					wb = new HSSFWorkbook(fis);
				}
				exprotData(wb, apiDetailDtos);
				// 导出,创建输出流
				fos = new FileOutputStream(file);
				// 写入输出流
				wb.write(fos);
				result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			} catch (Exception e) {
				result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.flush();
						fos.close();
					}
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			result.put(ApiReturnConstants.DATA, null);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		}
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

	/**
	 * 获取解析的数据
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public List<ApiDetailDto> getParse(String filePath) throws Exception {
		List<String> packageName = new ArrayList<>();
		List<String> classNames = PackageUtil.getClassNameByFile(filePath, packageName, true);
		List<ApiDetailDto> apiDetailDtos = new ArrayList<>();
		if (classNames != null) {
			for (String className : classNames) {
				// 判断类上是否有该注解
				Class<?> clz = Class.forName(className);
				boolean confirmApi = clz.isAnnotationPresent(Api.class);
				ApiDetailDto apiDetailDto = new ApiDetailDto();
				if (confirmApi) {
					// proName
					Api annotation = clz.getAnnotation(Api.class);
					if (annotation.value().length() > 0) {
						apiDetailDto.setProName(annotation.value());
					} else {
						apiDetailDto.setProName(annotation.description());
					}
					// proApiName
					Method[] methods = clz.getDeclaredMethods();
					if (methods != null) {
						List<String> o = new ArrayList<>();
						List<String> h = new ArrayList<>();
						List<List<Object>> ap = new ArrayList<>();
						List<List<Object>> pv = new ArrayList<>();
						List<List<Object>> rh = new ArrayList<>();
						List<List<Object>> rp = new ArrayList<>();
						for (Method method : methods) {
							// 判断方法上是否有该注解
							boolean confirmApiOperation = method.isAnnotationPresent(ApiOperation.class);
							if (confirmApiOperation) {
								// 获取该注解参数的notes值
								String operation = method.getAnnotation(ApiOperation.class).notes();
								String httpMeth = method.getAnnotation(ApiOperation.class).httpMethod();
								int index = operation.indexOf("<");
								if (index >= 0) {
									String notesValue = operation.substring(0, index);
									o.add("\"" + notesValue + "\"");
								} else {
									o.add("\"" + operation + "\"");
								}
								h.add("\"" + httpMeth + "\"");
							}
							apiDetailDto.setApiName(o);
							apiDetailDto.setApiHttpMeth(h);
							List<Object> apiParam = getApiParam(method);
							ap.add(apiParam);
							apiDetailDto.setApiParam(ap);
							List<Object> pathVariable = getPathVariable(method);
							pv.add(pathVariable);
							apiDetailDto.setPathVariable(pv);
							List<Object> requestHeader = getRequestHeader(method);
							rh.add(requestHeader);
							apiDetailDto.setRequestHeader(rh);
							List<Object> requestParam = getRequestParam(method);
							rp.add(requestParam);
							apiDetailDto.setRequestParam(rp);
						}
						apiDetailDto.setDepend("数据库：" + database + "，依赖API：" + aiZhiXinHost + " 和 " + OrgMaanagerHost);
						apiDetailDtos.add(apiDetailDto);
					}
				}
			}
		}
		return apiDetailDtos;
	}

	/**
	 * 遍历解析的数据到excel表
	 * 
	 * @param wb
	 * @param dataset
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void exprotData(HSSFWorkbook wb, List<ApiDetailDto> dataset) {
		int num = wb.getNumberOfSheets();// 当前总sheet页数
		HSSFSheet sheet = wb.createSheet(sheetName + num);
		HSSFRow row = null;
		HSSFCell cell = null;
		if (null == dataset) {
			return;
		}
		if (dataset != null && dataset.size() > 0) {
			// 遍历集合数据，产生数据行
			Iterator<ApiDetailDto> it = dataset.iterator();
			int index = 0;
			while (it.hasNext()) {
				if (index == 0) {
					// 合并单元格
					CellRangeAddress region = new CellRangeAddress((short) 0, (short) 0, (short) 0, (short) 7);
					sheet.addMergedRegion(region);
					row = sheet.createRow(0);
					cell = row.createCell(0);
					cell.setCellValue("API_DETAIL");
					cell.setCellStyle(titleStyle(wb));
					index++;
					continue;
				}
				if (index == 1) {
					row = sheet.createRow(1);
					for (int i = 1; i < 9; i++) {
						cell = row.createCell(i - 1);
						cell.setCellStyle(columnNameStyle(wb));
						switch (i) {
						case 1:
							cell.setCellValue("pro_name");
							break;
						case 2:
							cell.setCellValue("api_depend");
							break;
						case 3:
							cell.setCellValue("api_name");
							break;
						case 4:
							cell.setCellValue("api_http_method");
							break;
						case 5:
							cell.setCellValue("api_param");
							break;
						case 6:
							cell.setCellValue("path_variable");
							break;
						case 7:
							cell.setCellValue("request_header");
							break;
						case 8:
							cell.setCellValue("request_param");
							break;
						}
					}
				}
				index++;
				if (index >= 2) {
					row = sheet.createRow(index);
					ApiDetailDto t = (ApiDetailDto) it.next();
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					Field[] fields = t.getClass().getDeclaredFields();
					for (short i = 0; i < fields.length; i++) {
						cell = row.createCell(i);
						Field field = fields[i];
						String fieldName = field.getName();
						String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						try {
							Class tCls = t.getClass();
							Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
							Object value = getMethod.invoke(t, new Object[] {});
							// 判断值的类型后进行强制类型转换
							String textValue = value == null ? "" : value.toString();
							// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
							if (textValue != null) {
								Pattern p = Pattern.compile("^//d+(//.//d+)?$");
								Matcher matcher = p.matcher(textValue);
								if (matcher.matches()) {
									// 是数字当作double处理
									cell.setCellValue(Double.parseDouble(textValue));
								} else {
									cell.setCellValue(textValue);
								}
							}
							row.setHeightInPoints((short) 200);// 行高
							// sheet.autoSizeColumn(i); //列宽自适应方法
							sheet.setDefaultColumnWidth((short) 50);// 列宽
							// cell.setCellType(HSSFCell.CELL_TYPE_STRING);//设置单元格类型
							cell.setCellStyle(contentStyle(wb));
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							// 清理资源
						}
					}
				}
			}
		}
	}

	// 设置单元格格式为文本格式
	public HSSFCellStyle dataFormat(HSSFWorkbook wb) {
		HSSFCellStyle textStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();
		textStyle.setDataFormat(format.getFormat("@"));
		return textStyle;
	}

	// 详情样式
	@SuppressWarnings("deprecation")
	public HSSFCellStyle contentStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style = dataFormat(wb);
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 填充方式
		style.setBorderBottom(HSSFCellStyle.BORDER_THICK); // 下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THICK);// 左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THICK);// 上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THICK);// 右边框
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		style.setBottomBorderColor(HSSFColor.PINK.index);// 边框颜色
		style.setTopBorderColor(HSSFColor.PINK.index);
		style.setLeftBorderColor(HSSFColor.PINK.index);
		style.setRightBorderColor(HSSFColor.PINK.index);
		HSSFFont font = wb.createFont();
		font.setFontName("楷体");
		font.setFontHeightInPoints((short) 14);// 设置字体大小
		style.setFont(font);// 选择需要用到的字体格式
		style.setWrapText(true);// 设置自动换行
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 水平居中且垂直居中
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return style;
	}

	// 标题样式
	@SuppressWarnings("deprecation")
	public HSSFCellStyle titleStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 填充方式
		HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 28);// 设置字体大小
		style.setFont(font);// 选择需要用到的字体格式
		return style;
	}

	// 列名样式
	@SuppressWarnings("deprecation")
	public HSSFCellStyle columnNameStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 填充方式
		style.setBorderBottom(HSSFCellStyle.BORDER_THICK); // 下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THICK);// 左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THICK);// 上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THICK);// 右边框
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		style.setBottomBorderColor(HSSFColor.PINK.index);// 边框颜色
		style.setTopBorderColor(HSSFColor.PINK.index);
		style.setLeftBorderColor(HSSFColor.PINK.index);
		style.setRightBorderColor(HSSFColor.PINK.index);
		HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 20);// 设置字体大小
		style.setFont(font);// 选择需要用到的字体格式
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 水平居中且垂直居中
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return style;
	}

	// --------------------------------------------------------------解析的方法------------------------------------------------//

	/**
	 * 获取指定方法的参数值
	 * 
	 * @param method
	 *            要获取参数值的方法
	 * @return 按参数顺序排列的参数值列表
	 */
	public static List<Object> getApiParam(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<>();
		for (Annotation[] parameterAnnotation : parameterAnnotations) {
			for (Annotation annotation : parameterAnnotation) {
				if (annotation instanceof ApiParam) {
					ApiParam param = (ApiParam) annotation;
					list.add(((ApiParam) param).value());
				}
			}
		}
		return list;
	}

	public static List<Object> getRequestParam(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<>();
		for (Annotation[] parameterAnnotation : parameterAnnotations) {
			for (Annotation annotation : parameterAnnotation) {
				if (annotation instanceof RequestParam) {
					RequestParam param = (RequestParam) annotation;
					list.add(((RequestParam) param).value() + " → " + ((RequestParam) param).required());
				}
			}
		}
		return list;
	}

	public static List<Object> getPathVariable(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<>();
		for (Annotation[] parameterAnnotation : parameterAnnotations) {
			for (Annotation annotation : parameterAnnotation) {
				if (annotation instanceof PathVariable) {
					PathVariable param = (PathVariable) annotation;
					list.add(((PathVariable) param).value());
				}
			}
		}
		return list;
	}

	public static List<Object> getRequestHeader(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<>();
		for (Annotation[] parameterAnnotation : parameterAnnotations) {
			for (Annotation annotation : parameterAnnotation) {
				if (annotation instanceof RequestHeader) {
					RequestHeader param = (RequestHeader) annotation;
					list.add(((RequestHeader) param).value());
				}
			}
		}
		return list;
	}
}
