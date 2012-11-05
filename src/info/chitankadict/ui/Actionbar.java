package info.chitankadict.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class Actionbar extends RelativeLayout {

	public static final int DEFAULT_ICON_ALPHA = 200;

	public Actionbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Actionbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Actionbar(Context context) {
		super(context);
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		setupButtons();
	}

	private void setupButtons() {
		// final ImageView actionButton = (ImageView)
		// findViewById(R.id.actionbar_info);
		//
		// actionButton.getDrawable().setAlpha(Actionbar.DEFAULT_ICON_ALPHA);
		//
		// actionButton.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO
		// }
		// });
	}
}
