package com.example.tipcalculator;

import java.math.BigDecimal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TipCalculatorActivity extends Activity {
	private SeekBar sbService;
	private TextView tvServiceValue;
	private RatingBar rbSplit;
	private TextView tvSplitValue;
	private EditText etCheckAmount;
	private TextView tvTotalTipValue;
	private TextView tvTotalWithTipValue;
	private TextView tvTotalPerPersonValue;
	private TextView tvTipPerPersonValue;
	private RelativeLayout mOutputContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calculator);
		sbService = (SeekBar) findViewById(R.id.sbService);
		tvServiceValue = (TextView) findViewById(R.id.tvServiceValue);
		sbService.setProgress(15);		
		tvServiceValue.setText(Integer.toString(sbService.getProgress()) + "%");
		
		rbSplit = (RatingBar) findViewById(R.id.rbSplit);
		tvSplitValue = (TextView) findViewById(R.id.tvSplitValue);
		tvSplitValue.setText(String.valueOf(Math.round(rbSplit.getRating())));
		
		etCheckAmount = (EditText) findViewById(R.id.etCheckAmount);
		tvTotalTipValue = (TextView) findViewById(R.id.tvTotalTipValue);
		tvTotalWithTipValue = (TextView) findViewById(R.id.tvTotalWithTipValue);
		tvTotalPerPersonValue = (TextView) findViewById(R.id.tvTotalPerPersonValue);
		tvTipPerPersonValue = (TextView) findViewById(R.id.tvTipPerPersonValue);
		mOutputContainer = (RelativeLayout) findViewById(R.id.tipOutput);
		mOutputContainer.setAlpha(0);

		setupSeekBarListner();
		setupRatingBarListner();
		setupCheckAmountListner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tip_calculator, menu);
		return true;
	}

	private void setupSeekBarListner() {
		sbService.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			final int stepSize = 1; // Configurable.

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue,
					boolean fromUser) {
				// Logic to configure stepSize in case it is other than 1.
				progresValue = ((int) Math.round(progresValue / stepSize)) * stepSize;
				seekBar.setProgress(progresValue);
				tvServiceValue.setText(Integer.toString(seekBar.getProgress()) + "%");
				calculateAndCommitValues();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	private void setupRatingBarListner() {
		rbSplit.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if(rating < 1) {
					rating = 1;
					ratingBar.setRating(rating);
				}
				tvSplitValue.setText(String.valueOf(Math.round(rating)));
				calculateAndCommitValues();
			}
		});
	}

	private void setupCheckAmountListner() {
		final Handler handler = new Handler();
		etCheckAmount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOutputContainer.setAlpha(0);
			}
		});
		etCheckAmount.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {
	            	handler.postDelayed(new Runnable() {
						@Override
						public void run() {
			            	mOutputContainer.setAlpha(1);
			            	mOutputContainer.startAnimation(AnimationUtils.loadAnimation(TipCalculatorActivity.this, R.animator.slideup));
			            	calculateAndCommitValues();							
						}
	            	}, 300);
	            }
	            return false;
	        }			
		});
	}
	
	public void onClickAddPerson(View v) {
		int split = (int)safeParseFloat(String.valueOf(tvSplitValue.getText()));
		split++;
		tvSplitValue.setText(String.valueOf(split));
		if(split < rbSplit.getMax()) {
			rbSplit.setRating(split);
		}
		else {
			rbSplit.setRating(rbSplit.getMax());
		}
		calculateAndCommitValues();
	}
	
	private float safeParseFloat(final String s) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
		}
		return 0;
	}
	
	private String round(float num, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }
	
	private void calculateAndCommitValues(){
		float checkAmount = safeParseFloat(String.valueOf(etCheckAmount.getText()));
		float service = safeParseFloat(String.valueOf(tvServiceValue.getText()).substring(0, String.valueOf(tvServiceValue.getText()).length() - 1));
		float totalTip = (checkAmount * service) / 100;
		float totalWithTip = checkAmount + totalTip;
		int split = (int)safeParseFloat(String.valueOf(tvSplitValue.getText()));
		float totalPerPerson = totalWithTip / split;
		float tipPerPerson = totalTip / split;		
		tvTotalTipValue.setText(round(totalTip, 2));
		tvTotalWithTipValue.setText(round(totalWithTip, 2));
		tvTotalPerPersonValue.setText(round(totalPerPerson, 2));
		tvTipPerPersonValue.setText(round(tipPerPerson, 2));
	}
}
