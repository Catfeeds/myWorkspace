/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package kankan.wheel.widget.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kankan.wheel.R;

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

    int selectedItem;
    int currentItem;
    // items
    private T items[];

    /**
     * Constructor
     * @param context the current context
     * @param items the items
     */
    public ArrayWheelAdapter(Context context, T items[]) {
        super(context);
        
        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
        setTextSize(18);
    }
    
    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < items.length) {
            T item = items[index];
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return items.length;
    }


    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        currentItem = index;
        return super.getItem(index, convertView, parent);
    }

    @Override
    protected void configureTextView(TextView view) {
        super.configureTextView(view);
        view.setTextSize(16);
        view.setTextColor(context.getResources().getColor(R.color.wheel_gray));

        if (selectedItem == currentItem) {
            view.setTextColor(context.getResources().getColor(R.color.colorBlack2));
            view.setTextSize(18);
        }
        view.setTypeface(Typeface.SANS_SERIF);
        view.setLines(1);
        view.setEllipsize(TextUtils.TruncateAt.END);
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
