package de.riwo.tetrix;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoneImagesAdapter extends ArrayAdapter<CharSequence> implements OnClickListener {

	private Drawable[] images;
	private CharSequence[] entries;
	private boolean[] selected;
	private Context context;
	private CheckBox stoneChecked;
	private MultiSelectStorable storable;
	private int numberOfSelected = 0;
	private final int MINIMUM_NUMBER_OF_SELECTED = 3;
	
	public StoneImagesAdapter(Context context, int textViewResourceId, Drawable[] images,
			CharSequence[] entries, boolean[] selected, MultiSelectStorable storable) {
		super(context, textViewResourceId);
		
		if(images.length != entries.length)
			throw new AssertionError("images and entries have to be of same size");
		
		this.images = images;
		this.entries = entries;
		this.selected = selected;
		this.context = context;
		this.storable = storable;
		
		for(boolean value : selected) {
			if(value)
				numberOfSelected++;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
				R.layout.stone_types_list_element, parent, false);
		}
		
		//convertView.setClickable(true);
		convertView.setId(position);
		convertView.setOnClickListener(this);
		
		ImageView stoneImg = (ImageView) convertView.findViewById(R.id.stone_type_image);
		stoneImg.setImageDrawable(images[position]);
		
		TextView stoneName = (TextView) convertView.findViewById(R.id.stone_type_name);
		stoneName.setText(entries[position]);
		
		/*CheckBox */stoneChecked = (CheckBox) convertView.findViewById(R.id.stone_type_checked);
		stoneChecked.setChecked(selected[position]);
		stoneChecked.setClickable(false);
		
		return convertView;
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public void onClick(View view) {
		/*CheckBox */stoneChecked = (CheckBox) view.findViewById(R.id.stone_type_checked);
		boolean wasChecked = stoneChecked.isChecked();
		
		if(wasChecked) {
			if(numberOfSelected > MINIMUM_NUMBER_OF_SELECTED) {
				// this checkbox is free to be deselected
				stoneChecked.setChecked(false);
				numberOfSelected--;
			} else {
				// if we would deselect this item, we wouldn't
				// have enough selected ones anymore -> tell the user
				showWarnMessage();
			}
		} else {
			// box wasn't checked before, so it's definitely
			// fine to check it
			stoneChecked.setChecked(true);
			numberOfSelected++;
		}
		
		storable.clicked(view.getId(), stoneChecked.isChecked());
	}
	
	private void showWarnMessage() {
		Toast t = Toast.makeText(
			context,
			R.string.not_enough_types_selected_warning,
			Toast.LENGTH_SHORT
		);
		t.show();
	}

}