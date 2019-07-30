package cn.sini.cgb.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * 图片生成工具类
 * 
 * @author gaowei
 */
public class GenerateImageUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateImageUtils.class);

	public static void main(String[] args) throws Exception {
		// List<File> fileList = new ArrayList<File>();
		// // 背景图
		// File background = createBackground("F:/background.png", 1000, 650);
		// File background = new File("f:/background.png");
		// 头像 地址：f:/portrait.png
		// createPortrait("f:/portrait.png", "");
		// File portrait = new File("f:/portrait.png");
		// fileList.add(portrait);
		// 用戶名
		// File yonghuming = createTextImage(new Font("宋体", Font.BOLD, 60), "f:/wenzi.png", "asdf爱是否打算", Color.black, true, false);
		// fileList.add(yonghuming);
		// 拥抱团长
		// File cantuan = createTextImage(new Font("宋体", Font.BOLD, 60), "f:/cantuan.png", "第100次拥抱团长", Color.red, false, false);
		// File cantuan = new File("f:/ctl.png");
		// fileList.add(cantuan);
		// // 商品图片
		// File file2 = belowShareCompositeGraPh("231654654", new File("f:/123.jpg"), "f:/groupOrder1.png", new BigDecimal("152.99"), new BigDecimal("9.9"), 999, "f:/hhhhhhhhh2.png", true);
		// File file2 = new File("f:/222.png");
		// fileList.add(file2);
		// createTextImage(new Font("宋体", Font.BOLD, 55), "", "黄金苹果+5", Color.red, false);
		// createTextImage(font, filePath, text, color, isUserName)
		// File thumbnail = thumbnail(new File("f:/123.jpg"), 1000, 550, "f:/222.png");
		// // 我想拼团图片
		// FILE WYPT2 = THUMBNAIL(NEW FILE("F:/WYPT.PNG"), 890, 100, "F:/WYPT2.PNG");
		// FILELIST.ADD(WYPT2);
		// FILE SYNThesisPicture = synthesisPicture(background, fileList, "f:/hecheng.png", false);
		// File file = new File("f:/123123.jpg");
		// File file2 = new File("f:/12322.jpg");
		// Thumbnails.of(file).scale(1f).outputQuality(0.3f).toFile(file);
		// Thumbnails.of(file).scale(0.5f).toFile(file);
	}

	/**
	 * 分享图片合成
	 * 
	 * @param baseFile 基础文件(图片)
	 * @param coverFiles 覆盖文件(图片)
	 * @param filePath 合成后的目标文件地址
	 * @throws Exception
	 * @description 注意：coverFiles文件的顺序不能乱。第一个文件是头像，第二个是用户名，第三个是‘拥抱X次团长’，第四个是商品图片或拼团图片，第五个是我要拼团
	 */
	public static File synthesisPicture(File baseFile, List<File> coverFiles, String filePath, Boolean isOrder) {
		File file = null;
		BufferedImage baseImg;
		BufferedImage coverImg;
		try {
			file = new File(filePath);

			baseImg = ImageIO.read(baseFile);
			int baseWidth = baseImg.getWidth();
			int baseHeight = baseImg.getHeight();

			BufferedImage bi = new BufferedImage(baseWidth, baseHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			bi = g.getDeviceConfiguration().createCompatibleImage(baseWidth, baseHeight, Transparency.TRANSLUCENT);
			g.dispose();
			g = bi.createGraphics();
			g.drawImage(baseImg, 0, 0, baseWidth, baseHeight, null);
			int xCount = 0;// 用户间隔图片之间x的距离
			for (int i = 0; i < coverFiles.size(); i++) {
				coverImg = ImageIO.read(coverFiles.get(i));
				int coverWidth = coverImg.getWidth();
				int coverHeight = coverImg.getHeight();
				int x = 0;
				int y = 0;
				// x,y 控制图片的坐标
				if (isOrder) {
					if (i == 0) {
						x = 10;
						y = 10;
						xCount += (x + coverWidth) + 30;// 加30是间隔距离，下面同理
					} else if (i == 1) {
						x = xCount;
						y = 50;
						xCount += coverWidth + 35;
					} else if (i == 2) {
						x = 526;
						y = 50;
					} else if (i == 3) {
						x = 10;
						y = 175;
					} else if (i == 4) {
						x = 50;
						y = 690;
					}
				} else {
					if (i == 0) {
						x = 0;
						y = 0;
					} else if (i == 1) {
						x = 20;
						y = 520;
					}
				}
				g.drawImage(coverImg, x, y, coverWidth, coverHeight, null);
			}
			g.dispose();
			ImageIO.write(bi, "png", file);
		} catch (Exception e) {
			LOGGER.error("【分享图片合成错误】", e);
		}
		return file;
	}

	/**
	 * 生成背景图
	 * 
	 * @param filePath 目标文件地址
	 * @param width 图片宽
	 * @param height 图片高
	 * @return File
	 */
	public static File createBackground(String filePath, int width, int height) {
		File file = null;
		try {
			file = new File(filePath);
			// 生成透明背景图
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D gd = bi.createGraphics();
			bi = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			gd = bi.createGraphics();
			gd.dispose();
			ImageIO.write(bi, "png", file);
		} catch (Exception e) {
			LOGGER.error("【背景图生成错误】", e);
		}
		return file;
	}

	/**
	 * 生成圆形头像
	 * 
	 * @param filePath 目标文件地址
	 * @param portraitPath 头像地址
	 * @return File
	 */
	public static File createPortrait(String filePath, String portraitPath) {
		File file = null;
		try {
			file = new File(filePath);
			BufferedImage srcImage = null;
			URL url = null;
			if (StringUtils.isNotBlank(portraitPath)) {
				url = new URL(portraitPath);
				srcImage = ImageIO.read(url);
			} else {
				// 使用默认头像
				File cantuanFile = ResourceUtils.getFile("classpath:images/logo.png");
				srcImage = ImageIO.read(cantuanFile);
			}
			int width = srcImage.getWidth();
			int height = srcImage.getHeight();
			BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = dstImage.createGraphics();
			TexturePaint texturePaint = new TexturePaint(srcImage, new Rectangle2D.Float(0, 0, width, height));
			g2.setPaint(texturePaint);
			Ellipse2D.Float ellipse = new Ellipse2D.Float(0, 0, width, height);
			// 抗锯齿
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fill(ellipse);
			g2.dispose();
			ImageIO.write(dstImage, "png", file);
		} catch (Exception e) {
			LOGGER.error("【圆形头像生成错误】", e);
		}
		return file;
	}

	/**
	 * 生成文字图片
	 * 
	 * @param font 字体
	 * @param filePath 目标文件地址
	 * @param text 文字内容
	 * @param color 设置文字颜色
	 * @param isUserName 是否是用戶名文字
	 * @param isStrikethrough 是否添加中划线
	 * @description 注意：isUserName为true时用户名字数大致限制在5个字内，isUserName为false时商品名称及数量一共限制在大概16个字内。若超过，则用‘...’替代
	 * @return File
	 */
	public static File createTextImage(Font font, String filePath, String text, Color color, Boolean isUserName, Boolean isStrikethrough) {
		File file = null;
		try {
			file = new File(filePath);
			// 创建图片
			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // 临时画布，所以尺寸随意
			Graphics2D g2d = image.createGraphics();
			g2d.setFont(font);
			FontMetrics fm = g2d.getFontMetrics();
			// 根据字体情况测量画布的宽高
			int width = fm.stringWidth(text);
			if (isUserName && width > 250) {// 用户名
				// 如果文字太长，则用...代替。(目前长度控制在530内)
				while (width > 250) {
					text = text.substring(0, text.length() - 1);
					width = fm.stringWidth(text);
				}
				text = text + "...";
				width = fm.stringWidth(text);
			} else if (!isUserName && width > 820) {// 商品名称及数量
				// 如果文字太长，则用...代替。(目前长度控制在820内)
				while (width > 820) {
					text = text.substring(0, text.length() - 1);
					width = fm.stringWidth(text);
				}
				text = text + "...";
				width = fm.stringWidth(text);
			}
			int height = fm.getHeight();
			g2d.dispose();
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			g2d = image.createGraphics();
			g2d.setClip(0, 0, width, height);
			g2d.setFont(font);// 设置画笔字体
			g2d.setColor(color);
			fm = g2d.getFontMetrics();
			int offset = height - fm.getAscent();
			g2d.drawString(text, 0, height - offset);
			if (isStrikethrough) {
				g2d.drawLine(0, height / 2, width, height / 2);
			}
			g2d.dispose();
			ImageIO.write(image, "png", file);
		} catch (Exception e) {
			LOGGER.error("【文字图片生成错误】", e);
		}
		return file;
	}

	/**
	 * 按照固定宽高原图压缩
	 * 
	 * @param sourceFile 源文件地址
	 * @param width 压缩宽
	 * @param height 压缩高
	 * @param filePath 目标文件地址
	 * @return File
	 */
	public static File thumbnail(File sourceFile, int width, int height, String filePath) {
		File file = null;
		try {
			BufferedImage bi = ImageIO.read(sourceFile);
			Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = tag.getGraphics();
			g.setColor(Color.RED);
			g.drawImage(image, 0, 0, null); // 绘制处理后的图
			g.dispose();
			file = new File(filePath);
			ImageIO.write(tag, "png", file);
		} catch (Exception e) {
			LOGGER.error("缩略图生成错误", e);
		}
		return file;
	}

	/**
	 * 中间商品合成图（包括：团单图，拼团价，累计参团数，背景图，我想拼团）
	 * 
	 * @param number 确保为一个可区别与其他用户生成的图片的标识(团单ID或订单编号)
	 * @param groupOrderFile 团单图
	 * @param groupOrderPath 团单图生成路径
	 * @param xj 现价
	 * @param yj 原价
	 * @param cts 参团数
	 * @param thumbnailImagesPath 合成图片后的文件地址
	 * @param isChange 为true时，则显示拼团价图片。false时，则隐藏拼团家图片。（当团单只有一个商品的时候为true，否则为false）
	 * */
	public static File belowShareCompositeGraPh(String number, File groupOrderFile, String groupOrderPath, BigDecimal xj, BigDecimal yj, Integer cts, String thumbnailImagesPath, Boolean isChange) throws Exception {
		String path = Environment.getProperty("annex.path");
		String generatePath = path + Environment.getProperty("generate.images");
		List<File> coverFiles = new ArrayList<File>();
		// 团单图
		File thumbnail = thumbnail(groupOrderFile, 1000, 500, groupOrderPath);
		if (isChange) {
			// 拼团价
			File ptjFile = ResourceUtils.getFile("classpath:images/ptj.png");
			File ptjFile2 = thumbnail(ptjFile, 1000, 150, generatePath + "/" + number + "_ptj.png");
			coverFiles.add(ptjFile2);
			// 现价
			String xjPath = generatePath + "/" + number + "_xj.png";
			File yjFile = createTextImage(new Font("微软雅黑", Font.BOLD, 70), xjPath, xj.toString(), Color.red, false, false);
			coverFiles.add(yjFile);
			// 原价, 如果原价为空的话则不显示, 则生成一个透明的图
			File xjFile = null;
			String yjPath = generatePath + "/" + number + "_yj.png";
			if (yj != null) {
				xjFile = createTextImage(new Font("微软雅黑", Font.PLAIN, 30), yjPath, "￥" + yj, Color.red, false, true);
			} else {
				xjFile = createTextImage(new Font("微软雅黑", Font.PLAIN, 30), yjPath, " ", Color.ORANGE, false, true);
			}
			coverFiles.add(xjFile);
			// 累计参团数
			String ctsPath = generatePath + "/" + number + "_cts.png";
			File ctsFile = createTextImage(new Font("微软雅黑", Font.BOLD, 40), ctsPath, "累计参团数 " + cts, Color.ORANGE, false, false);
			coverFiles.add(ctsFile);
		} else {
			// 累计参团图(销量图)
			File ctsFile = ResourceUtils.getFile("classpath:images/cts.png");
			File ctsFile2 = thumbnail(ctsFile, 1000, 120, generatePath + "/" + number + "_cts.png");
			coverFiles.add(ctsFile2);
			// 累计参团
			String ljctPath = generatePath + "/" + number + "_ljct.png";
			File ljctFile = createTextImage(new Font("微软雅黑", Font.BOLD, 70), ljctPath, cts.toString(), Color.ORANGE, false, false);
			coverFiles.add(ljctFile);
		}
		File file = null;
		BufferedImage baseImg;
		BufferedImage coverImg;
		try {
			file = new File(thumbnailImagesPath);
			baseImg = ImageIO.read(thumbnail);// 基础图片
			int baseWidth = baseImg.getWidth();
			int baseHeight = baseImg.getHeight();

			BufferedImage bi = new BufferedImage(baseWidth, baseHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			bi = g.getDeviceConfiguration().createCompatibleImage(baseWidth, baseHeight, Transparency.TRANSLUCENT);
			g.dispose();
			g = bi.createGraphics();
			g.drawImage(baseImg, 0, 0, baseWidth, baseHeight, null);
			// 需要合成的图
			int countX = 0;
			for (int i = 0; i < coverFiles.size(); i++) {
				coverImg = ImageIO.read(coverFiles.get(i));
				int coverWidth = coverImg.getWidth();
				int coverHeight = coverImg.getHeight();
				// x,y 控制图片的坐标
				int x = 0;
				int y = 0;
				if (isChange) {
					if (i == 0) {
						x = 0;
						y = 350;
					} else if (i == 1) {
						x = 235;
						y = 500 - coverHeight - 13;
						countX = x + coverWidth + 10;
					} else if (i == 2) {
						x = countX;
						y = 500 - 23 - coverHeight;
					} else if (i == 3) {
						x = 600 + ((400 - coverWidth) / 2);
						y = 433;
					}
				} else {
					if (i == 0) {
						x = 0;
						y = 380;
					} else if (i == 1) {
						x = 200;
						y = 395;
					}
				}
				g.drawImage(coverImg, x, y, coverWidth, coverHeight, null);
			}
			g.dispose();
			ImageIO.write(bi, "png", file);
		} catch (Exception e) {
			LOGGER.error("【中间商品合成图错误】", e);
		}
		// 删除生成的图片
		coverFiles.add(thumbnail);
		for (File f : coverFiles) {
			f.delete();
		}
		return file;
	}
}