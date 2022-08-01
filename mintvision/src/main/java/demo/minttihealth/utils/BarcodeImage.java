package demo.minttihealth.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class BarcodeImage {

	private static final int IMAGE_HALF_WIDTH = 24;

	// 生成 二维码图片
	@Nullable
	public static Bitmap bitmap(Context context, String qrCode) {
		int BASE_WIDTH = 320;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int density = (int) metrics.density;
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();
			if (qrCode == null || "".equals(qrCode) || qrCode.length() < 1) {
				return null;
			}
			// 把输入的文本转为二维码
			writer.encode(qrCode, BarcodeFormat.QR_CODE, BASE_WIDTH * density, BASE_WIDTH * density);
			Hashtable<EncodeHintType, String> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(qrCode, BarcodeFormat.QR_CODE, BASE_WIDTH * density, BASE_WIDTH * density, hints);
			int[] pixels = new int[BASE_WIDTH * density * BASE_WIDTH * density];
			for (int y = 0; y < BASE_WIDTH * density; y++) {
				for (int x = 0; x < BASE_WIDTH * density; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * BASE_WIDTH * density + x] = 0xff000000;
					} else {
						pixels[y * BASE_WIDTH * density + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(BASE_WIDTH * density, BASE_WIDTH * density, Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, BASE_WIDTH * density, 0, 0, BASE_WIDTH * density, BASE_WIDTH * density);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成二维码
	 * 
	 * @param qrCode  二维码中包含的文本信息
	 * @param logo logo图片
	 * @return Bitmap 位图
	 */
	public static Bitmap bitmap(String qrCode, Bitmap logo) {
		Matrix m = new Matrix();
		float sx = (float) 2 * IMAGE_HALF_WIDTH / logo.getWidth();
		float sy = (float) 2 * IMAGE_HALF_WIDTH / logo.getHeight();
		m.setScale(sx, sy);// 设置缩放信息
		// 将logo图片按martix设置的信息缩放
		logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), m, false);
//		logo = RoundImageView.getCroppedRoundBitmap(logo, IMAGE_HALFWIDTH);
		logo = getRoundedCornerBitmap(logo);
		Hashtable<EncodeHintType, String> hst = new Hashtable<EncodeHintType, String>();
		hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 设置字符编码
		Bitmap bitmap = null;
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BitMatrix matrix = writer.encode(qrCode, BarcodeFormat.QR_CODE, 300, 300, hst);
			int width = matrix.getWidth();// 矩阵高度
			int height = matrix.getHeight();// 矩阵宽度
			int halfW = width / 2;
			int halfH = height / 2;
			int[] pixels = new int[width * height];// 定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
			for (int y = 0; y < height; y++) {// 从行开始迭代矩阵
				for (int x = 0; x < width; x++) {// 迭代列
					if (x > halfW - IMAGE_HALF_WIDTH && x < halfW + IMAGE_HALF_WIDTH && y > halfH - IMAGE_HALF_WIDTH && y < halfH + IMAGE_HALF_WIDTH) {// 该位置用于存放图片信息
						// 记录图片每个像素信息
						pixels[y * width + x] = logo.getPixel(x - halfW + IMAGE_HALF_WIDTH, y - halfH + IMAGE_HALF_WIDTH);
					} else {
						if (matrix.get(x, y)) {// 如果有黑块点，记录信息
							pixels[y * width + x] = 0xff000000;// 记录黑块信息
						} else {
							pixels[y * width + x] = 0xffffffff;
						}
					}
				}
			}
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			// 通过像素数组生成bitmap
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		} catch (WriterException e) {
			e.printStackTrace();
		}// 生成二维码矩阵信息
		return bitmap;
	}
	
	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff000000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		final float roundPx = width / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

}
