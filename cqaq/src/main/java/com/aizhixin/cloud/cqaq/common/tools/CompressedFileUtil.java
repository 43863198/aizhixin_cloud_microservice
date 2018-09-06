package com.aizhixin.cloud.cqaq.common.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 文件压缩工具类 将指定文件/文件夹压缩成zip、rar压缩文件
 */
@Component
@Slf4j
public class CompressedFileUtil {

	public CompressedFileUtil() {
	}

	/**
	 * @desc 将源文件/文件夹生成指定格式的压缩文件,格式zip
	 * @param resourePath
	 *            源文件/文件夹
	 * @param targetPath
	 *            压缩文件保存路径
	 * @return void
	 * @throws Exception
	 */
	public static void compressedFile(String resourcesPath, String targetPath) throws Exception {
		File resourcesFile = new File(resourcesPath); // 源文件
		File targetFile = new File(targetPath); // 压缩文件保存路径
		// 如果路径不存在，则新建
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		String targetName = resourcesFile.getName() + ".zip"; // 压缩文件名
		FileOutputStream outputStream = new FileOutputStream(targetPath + "\\" + targetName);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));
		createCompressedFile(out, resourcesFile, "");
		out.close();
	}

	/**
	 * @desc 生成压缩文件。 如果是文件夹，则使用递归，进行文件遍历、压缩 如果是文件，直接压缩
	 * @param out
	 *            输出流
	 * @param file
	 *            目标文件
	 * @return void
	 * @throws Exception
	 */
	public static void createCompressedFile(ZipOutputStream out, File file, String dir) throws Exception {
		// 如果当前的是文件夹，则进行进一步处理
		if (file.isDirectory()) {
			// 得到文件列表信息
			File[] files = file.listFiles();
			// 将文件夹添加到下一级打包目录
			out.putNextEntry(new ZipEntry(dir + "/"));
			dir = dir.length() == 0 ? "" : dir + "/";
			// 循环将文件夹中的文件打包
			for (int i = 0; i < files.length; i++) {
				createCompressedFile(out, files[i], dir + files[i].getName()); // 递归处理
			}
		} else { // 当前的是文件，打包处理
			// 文件输入流
			FileInputStream fis = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(dir));
			// 进行写操作
			int j = 0;
			byte[] buffer = new byte[1024];
			while ((j = fis.read(buffer)) > 0) {
				out.write(buffer, 0, j);
			}
			// 关闭输入流
			fis.close();
		}
	}

	public void zip() {
		try {
			Date date = new Date();
			String dateNow = DateFormatUtil.format(date, "yyyy-MM-dd");
			CompressedFileUtil.compressedFile("E:\\zhixinshu\\data\\out\\" + dateNow, "E:\\zhixinshu\\data\\out");
			log.info("The compressed file generated success");
		} catch (Exception e) {
			log.info("The compressed file generation failed");
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// CompressedFileUtil compressedFileUtil = new CompressedFileUtil();
	// try {
	// compressedFileUtil.compressedFile("G:\\outnew", "G:\\");
	// System.out.println("压缩文件已经生成...");
	// } catch (Exception e) {
	// System.out.println("压缩文件生成失败...");
	// e.printStackTrace();
	// }
	// }
}