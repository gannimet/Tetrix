package de.riwo.tetrix;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ListPreferenceMultiSelect extends ListPreference implements
		MultiSelectStorable {

	private static final String DEFAULT_SEPARATOR = ",";
	
	private Drawable[] entryImages;
	private boolean[] selectedIndices;
	private String separator = DEFAULT_SEPARATOR;
	private Context context;

	public ListPreferenceMultiSelect(Context context) {
		this(context, null);
	}

	public ListPreferenceMultiSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		
		initializeFromAttributes(attrs);
		selectedIndices = new boolean[entryImages.length];
	}
	
	private void initializeFromAttributes(AttributeSet attrs) {
		TypedArray attributes = context.obtainStyledAttributes(attrs,
			R.styleable.ListPreferenceMultiSelect);
		
		separator = attributes.getString(R.styleable.ListPreferenceMultiSelect_separator);
		
		if(separator == null || "".equals(separator))
			separator = DEFAULT_SEPARATOR;
		
		Resources resources = context.getResources();
		int imagesResId =
			attributes.getResourceId(R.styleable.ListPreferenceMultiSelect_entryImages, 0);
		TypedArray imgArray = resources.obtainTypedArray(imagesResId);
		
		entryImages = new Drawable[imgArray.length()];
		for(int i = 0; i < entryImages.length; i++) {
			entryImages[i] = imgArray.getDrawable(i);
		}
		
		attributes.recycle();
	}

	/**
	 * Joins the selected items with their corresponding
	 * entryValues on {@code separator}.
	 * @return The joined String to be stored to the preferences
	 */
	private String getJoinedValueString() {
		StringBuilder result = new StringBuilder();
		CharSequence[] entryValues = getEntryValues();
		
		for(int i = 0; i < selectedIndices.length; i++) {
			if(selectedIndices[i]) {
				result.append(entryValues[i]);
				result.append(separator);
			}
		}
		
		// cut the last separator
		int oldLength = result.length();
		if(oldLength > 0) {
			result.setLength(oldLength-1);
		}
		
		return result.toString();
	}
	
	/**
	 * Reads the stored values from the preferences and updates
	 * the selectedIndices array
	 */
	private void restoreCheckedEntries() {
		CharSequence[] entryValues = getEntryValues();
		List<String> storedValues = Arrays.asList(parseStoredValue());
		
		for(int i = 0; i < entryValues.length; i++) {
			if(storedValues.contains(entryValues[i]))
				selectedIndices[i] = true;
			else
				selectedIndices[i] = false;
		}
	}

	public void clicked(int index, boolean checked) {
		selectedIndices[index] = checked;
	}

	protected void onPrepareDialogBuilder(Builder builder) {
		CharSequence[] entries = getEntries();
		CharSequence[] entryValues = getEntryValues();
		
		if(entries == null || entryValues == null || entries.length != entryValues.length) {
			throw new IllegalStateException("ListPreference requires an entries array and an " +
				"entryValues array which are both the same length");
		}
		
		restoreCheckedEntries();
		
		builder
		.setAdapter(
			new StoneImagesAdapter(
				context, R.id.stone_type_name, entryImages, entries, selectedIndices, this
			),
			null
		)
		.setPositiveButton(
			R.string.save, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setValue(getJoinedValueString());
				}
				
			}
		)
		.setNegativeButton(R.string.cancel, null);
	}

	private String[] parseStoredValue() {
		return getValue().split(separator);
	}
}