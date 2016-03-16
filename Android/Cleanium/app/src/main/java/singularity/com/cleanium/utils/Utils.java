package singularity.com.cleanium.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static void showAlertDialog(Context context, String title, String message, String positiveButton) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	dialog.dismiss();
	        }
	     }).create();
		alertDialog.show();
	}

	
	public static void openFragment(FragmentActivity fragmentActivity, int viewId, Fragment fragmentToShow, String fragmentTag) {
		FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(viewId, fragmentToShow, fragmentTag);
		fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
	}

	public static void openFragment(FragmentActivity fragmentActivity, int viewId, Fragment fragmentToShow, String fragmentTag, int inAnimation, int outAnimation) {
		FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(inAnimation, outAnimation);
		fragmentTransaction.replace(viewId, fragmentToShow, fragmentTag);
		fragmentTransaction.disallowAddToBackStack();
		fragmentTransaction.commit();
	}

	public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
        int radius = width/2;
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		Bitmap temp = Bitmap.createScaledBitmap(output, bitmap.getWidth(), bitmap.getHeight(), true);
		output.recycle();
		output = null;
		return temp;
	}
}