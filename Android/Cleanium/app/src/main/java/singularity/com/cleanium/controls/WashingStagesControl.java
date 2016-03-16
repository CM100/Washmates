package singularity.com.cleanium.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.singularity.cleanium.R;

import library.singularity.com.data.model.OrderStatus;

public class WashingStagesControl extends View {

    private Paint grayPaint, bluePaint, redPaint;
    private OrderStatus[] stages;
    private OrderStatus currentStatus;

    private Bitmap bmpDoneMark, bmpNotDoneMark;
    private int currentStageIndex = -1;
    private RectF oval;
    private TextView textView;
    private RelativeLayout layout;

    public WashingStagesControl(Context context) {
        super(context);
        this.init();
    }

    public WashingStagesControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public WashingStagesControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        grayPaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStyle(Paint.Style.STROKE);
        redPaint.setStyle(Paint.Style.STROKE);

        grayPaint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.thicker_progress_stages_circle_thickness));
        bluePaint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.thicker_progress_stages_circle_thickness));
        redPaint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.thicker_progress_stages_circle_thickness));

        grayPaint.setColor(getContext().getResources().getColor(R.color.progress_stages_circle_normal_state));
        bluePaint.setColor(getContext().getResources().getColor(R.color.progress_stages_circle_progress_state));
        redPaint.setColor(getContext().getResources().getColor(R.color.progress_stages_circle_on_hold_state));

        this.bmpDoneMark = BitmapFactory.decodeResource(getResources(), R.drawable.done_mark);
        this.bmpNotDoneMark = BitmapFactory.decodeResource(getResources(), R.drawable.not_done_mark);

        oval = new RectF();
        layout = new RelativeLayout(getContext());
        textView = new TextView(getContext());
        Typeface thin = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Light.ttf");
        textView.setTypeface(thin);
        textView.setVisibility(View.VISIBLE);
        textView.setSingleLine(false);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_size_40sp));
        textView.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int controlWidth = getWidth();
        int controlHeight = getHeight();

        int centerX = controlWidth / 2;
        int centerY = controlHeight / 2;

        int checkmarkCircleWidth = Math.min(bmpDoneMark.getWidth(), bmpDoneMark.getHeight());

        int diameter = Math.min(controlWidth - checkmarkCircleWidth, controlHeight - checkmarkCircleWidth);
        int radius = diameter / 2;

        if (stages == null) {
            canvas.drawCircle(centerX, centerY, radius, redPaint);
        } else {
            canvas.drawCircle(centerX, centerY, radius, grayPaint);
        }

        // Draw progress line
        if(currentStageIndex >= 0 && stages != null && stages.length > 1) {
            oval.set(checkmarkCircleWidth / 2, checkmarkCircleWidth / 2, controlWidth - (checkmarkCircleWidth / 2), controlHeight - (checkmarkCircleWidth / 2));
            if (currentStageIndex < stages.length - 1) {
                canvas.drawArc(oval, 270, 360f / (stages.length) * (currentStageIndex), false, bluePaint);
            } else {
                canvas.drawArc(oval, 270, 360f, false, bluePaint);
            }
        }

        // Place mark icons
        if (stages != null) {
            final int stagesLength = stages.length;
            double degreesOffset = 2 * Math.PI / stagesLength;
            for (int i = 0; i < stagesLength; i++) {
                double stageDegreesOffset = i * degreesOffset - Math.PI / 2;
                double x = centerX + radius * Math.cos(stageDegreesOffset) - checkmarkCircleWidth / 2;
                double y = centerY + radius * Math.sin(stageDegreesOffset) - checkmarkCircleWidth / 2;

                if (i <= currentStageIndex) {
                    canvas.drawBitmap(bmpDoneMark, (float) x, (float) y, grayPaint);
                } else {
                    canvas.drawBitmap(bmpNotDoneMark, (float) x, (float) y, grayPaint);
                }
            }
        }

        if (currentStatus != null) {
            if (stages == null) {
                textView.setTextColor(getResources().getColor(R.color.progress_stages_circle_on_hold_state));
            } else {
                textView.setTextColor(getResources().getColor(R.color.progress_stages_circle_progress_state));
            }
            textView.setText(currentStatus.getText());
            textView.setWidth(diameter-checkmarkCircleWidth);
            textView.setHeight(diameter-checkmarkCircleWidth);

            layout.removeAllViews();
            layout.addView(textView);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            params.setMargins((controlWidth - diameter + checkmarkCircleWidth)/2,
                    (controlHeight - diameter + checkmarkCircleWidth)/2,
                    (controlWidth - diameter + checkmarkCircleWidth)/2,
                    (controlHeight - diameter + checkmarkCircleWidth)/2);
            textView.setLayoutParams(params);
            layout.measure(canvas.getWidth(), canvas.getHeight());
            layout.layout(0, 0, controlWidth, controlHeight);
            layout.draw(canvas);
        }
    }

    public void setStages(OrderStatus[] stages) { this.stages = stages; }

    public void setCurrentOrderStatus(OrderStatus currentStatus) {
        this.currentStatus = currentStatus;
        if (stages == null || currentStatus == null) {
            currentStageIndex = -1;
            return;
        }

        for (int i = 0; i < stages.length; i++) {
            OrderStatus stage = stages[i];
            if (stage != null && stage.getName().equals(currentStatus.getName())) {
                currentStageIndex = i;
                break;
            }
        }

        invalidate();
        requestLayout();
    }
}
