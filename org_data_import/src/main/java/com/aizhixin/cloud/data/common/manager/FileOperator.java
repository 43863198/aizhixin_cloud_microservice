package com.aizhixin.cloud.data.common.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.data.syn.core.FileNameConstant;
import com.aizhixin.cloud.data.syn.dto.BaseDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class FileOperator {
	private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	private String baseDir;

	private static String DB_DIR = "db";

	private static String OUT_DIR = "out";

	public FileOperator() {
		String dir = System.getProperty("operator.base.dir");
		if (null == dir) {
			dir = System.getProperty("user.dir");
		}
		baseDir = dir;
	}

	public String getDBDir(Date date) {
		StringBuilder sb = new StringBuilder(baseDir);
		if (!baseDir.endsWith(File.separator)) {
			sb.append(File.separator);
		}
		sb.append(DB_DIR).append(File.separator).append(format.format(date));
		return sb.toString();
	}

	public String getCurrentDbDataDir() {
		return getDBDir(new Date());
	}

	public String getOutDir(Date date) {
		StringBuilder sb = new StringBuilder(baseDir);
		if (!baseDir.equals(File.separator)) {
			sb.append(File.separator);
		}
		sb.append(OUT_DIR).append(File.separator).append(format.format(date));
		return sb.toString();
	}

	public String getCurrentOutDataDir() {
		return getOutDir(new Date());
	}

	public File createDir(String dir) {
		File fdir = new File(dir);
		if (!fdir.exists()) {
			int i = 0;
			while (i < 5) {
				if (!fdir.mkdirs()) {
					i++;
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
		}
		return fdir;
	}

	public void closeOutputStream(OutputStream out) {
		if (null != out) {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FileOutputStream getFileOutputStream(String dir, String fileName) {
		File file = new File(createDir(dir), fileName);
		try {
			return new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getYesterdayDBFile(String fileName) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String dir = getDBDir(calendar.getTime());
		return new File(dir + File.separator + fileName);
	}

	public File getCurrentdayDBFile(String fileName) {
		String dir = getCurrentDbDataDir();
		return new File(dir + File.separator + fileName);
	}

	public void outDBDir(List<BaseDTO> allNowLines, String fileName, String param) {
		String dir = getDBDir(new Date());
		File file = new File(createDir(dir), fileName);
		JsonUtil.encode(file, allNowLines);
		log.info("Out {} data to file ({}) base line ({})", param, file.toString(), allNowLines.size());
	}

	public File getCurrentdayAddFile(String fileName) {
		return new File(createDir(getCurrentOutDataDir()), FileNameConstant.ADD_PRE + fileName);
	}

	public File getCurrentdayUpdateFile(String fileName) {
		return new File(createDir(getCurrentOutDataDir()), FileNameConstant.UPDATE_PRE + fileName);
	}

	public void outOutCompactDir(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList,
			String fileName) {
		String dir = getCurrentOutDataDir();
		File file = null;
		if (!addList.isEmpty()) {
			file = new File(createDir(dir), FileNameConstant.ADD_PRE + fileName);
			JsonUtil.encode(file, addList);
		}

		if (!upadateList.isEmpty()) {
			file = new File(createDir(dir), FileNameConstant.UPDATE_PRE + fileName);
			JsonUtil.encode(file, upadateList);
		}

		if (!delList.isEmpty()) {
			file = new File(createDir(dir), FileNameConstant.DELETE_PRE + fileName);
			JsonUtil.encode(file, delList);
		}
	}

	public void outOutCompactDir(Map<String, Set<String>> addMap, Map<String, Set<String>> delMap, String fileName) {
		String dir = getCurrentOutDataDir();
		File file = null;
		if (!addMap.isEmpty()) {
			file = new File(createDir(dir), FileNameConstant.ADD_PRE + fileName);
			JsonUtil.encode(file, addMap);
		}

		if (!delMap.isEmpty()) {
			file = new File(createDir(dir), FileNameConstant.DELETE_PRE + fileName);
			JsonUtil.encode(file, delMap);
		}
	}

	public void outOutCompactDir(List<BaseDTO> list, String fileName) {
		String dir = getCurrentOutDataDir();
		File file = null;
		if (!list.isEmpty()) {
			file = new File(createDir(dir), fileName);
			JsonUtil.encode(file, list);
		}
	}

	public OutputStream getOutputStream(String fileName) {
		String dir = getCurrentOutDataDir();
		return getFileOutputStream(dir, fileName);
	}
}
